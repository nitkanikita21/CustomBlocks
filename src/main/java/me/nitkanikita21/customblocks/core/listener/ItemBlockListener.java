package me.nitkanikita21.customblocks.core.listener;

import de.tr7zw.nbtapi.NBT;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.block.ActionResult;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.registry.Blocks;
import me.nitkanikita21.customblocks.core.registry.Registries;
import me.nitkanikita21.registry.Identifier;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.joml.Vector3i;

@Slf4j
@RequiredArgsConstructor
public class ItemBlockListener implements Listener {
    private final ServerBlockManager serverManager;

    public static boolean isBlockItem(ItemStack itemStack) {
        return NBT.get(itemStack, nbt -> {
            return nbt.hasTag(Block.BLOCK_ITEM_TAG);
        });
    }

    public static Block getBlock(ItemStack itemStack) {
        Identifier blockId = NBT.get(itemStack, nbt -> {
            return new Identifier(nbt.getString(Block.BLOCK_ITEM_TAG));
        });


        return Registries.BLOCKS.get(blockId)
            .onEmpty(() -> {
                log.error("Block with id {} not found in registry", blockId);
                log.warn("Instead, customblocks:not_found will be used");
            })
            .getOrElse(() -> Blocks.NOT_FOUND);
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        WorldAccessor accessor = serverManager.getAccessor(player.getWorld());
        Vector3i pos = event.getClickedBlock().getLocation().toVector().toVector3i();

        boolean isServerSideNoteBlock = event.getClickedBlock().getType() == Material.NOTE_BLOCK;
        Option<Boolean> canBeClientSideNoteBlock = accessor.getManager().tryGetBlockState(pos)
            .map(bs -> bs.getOwner().getClientBlock(bs, accessor, pos, player))
            .map(wbs -> SpigotConversionUtil.toBukkitBlockData(wbs).getMaterial() == Material.NOTE_BLOCK);

        Option<BlockState> optState = accessor.getManager().tryGetBlockState(pos);
        if (optState.isDefined()) {
            BlockState state = optState.get();
            ActionResult result = state.getOwner().onInteract(
                state,
                accessor,
                pos,
                player,
                event.getAction(),
                event.getBlockFace()
            );

            switch (result) {
                case SUCCESS, FAIL -> {
                    event.setCancelled(true);
                    return;
                }
                case CONSUME -> {
                    event.setCancelled(true);
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        ItemStack item = player.getInventory().getItem(event.getHand());
                        if (item != null) item.setAmount(item.getAmount() - 1);
                    }
                    return;
                }
                case PASS -> {
                    // нічого не робимо — продовжуємо як є
                }
            }
        }

        ItemStack item = player.getInventory().getItemInMainHand();
//        var isCBMainHand = isBlockItem(item);
        if (item == null || item.isEmpty() || !isBlockItem(item)) {
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            if (offHandItem != null && !offHandItem.isEmpty() && isBlockItem(offHandItem)) {
                item = offHandItem;
            }

        }

        if (item == null || item.isEmpty()) return;

        if (event.hasBlock() && !isBlockItem(item) && !canBeClientSideNoteBlock.isEmpty() && player.isSneaking()) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.ALLOW);
            return;

        }

//        var isCBOffHand = isBlockItem(item);
        if (!isBlockItem(item)) return;

        if (event.hasBlock()) {
            if (isServerSideNoteBlock && isBlockItem(item) && isHead(item.getType())) {
                player.sendMessage("kaki");
                event.setUseItemInHand(Event.Result.DENY);
                event.setUseInteractedBlock(Event.Result.ALLOW);
                return;
            }
            if (isServerSideNoteBlock) return;

            var chadCondition = (!canBeClientSideNoteBlock.isEmpty()) &&
                                canBeClientSideNoteBlock.get() &&
                                event.useInteractedBlock() != Event.Result.ALLOW /*&&
                                player.isSneaking()*/;

            var replaceableChadCondition = !canBeClientSideNoteBlock.isEmpty() && event.getHand() == EquipmentSlot.OFF_HAND;

            if (chadCondition || replaceableChadCondition) {
                event.setCancelled(true);
                return;
            }
            /*if(!event.getClickedBlock().isReplaceable() && event.getHand() == EquipmentSlot.OFF_HAND){
                player.sendMessage("sosi");
                return;
            }*/

        }



        Block block = getBlock(item);
        Location interactionPoint = event.getInteractionPoint().add(
            event.getClickedBlock().getType().isSolid() ? event.getBlockFace().getDirection().multiply(0.2f) : new Vector(0.0, 0.0, 0.0)
        );

        var bukkitBlock = accessor.getWorld().getBlockAt(interactionPoint);
        BoundingBox boundingBox = BoundingBox.of(interactionPoint.toBlockLocation().toCenterLocation(), 0.5, 0.5, 0.5);
        boolean canPlace = !player.getBoundingBox().overlaps(boundingBox);

//        if(true)return;

        Vector3i clickedBlockPos = pos;
        serverManager.getScheduler().runTaskLater(
            () -> accessor.getManager().updateBlocksForPlayersAround(clickedBlockPos),
            5
        );

        if (bukkitBlock.isReplaceable() && canPlace) {
            event.setCancelled(true);
            accessor.getManager().placeBlock(interactionPoint.toVector().toVector3i(), block, player, event.getAction(), event.getBlockFace());
            if (player.getGameMode() != GameMode.CREATIVE) item.setAmount(item.getAmount() - 1);
        }

    }

    public boolean isHead(Material material) {
        return material.toString().contains("HEAD");
    }
}
