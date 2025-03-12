package me.nitkanikita21.customblocks.core;

import de.tr7zw.nbtapi.NBT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.registry.Blocks;
import me.nitkanikita21.customblocks.core.registry.Registries;
import me.nitkanikita21.registry.Identifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)return;
        if(event.getItem() == null || event.getItem().isEmpty())return;
        ItemStack item = event.getItem();

        if(!isBlockItem(item))return;
        event.setCancelled(true);

        Block block = getBlock(item);
        Player player = event.getPlayer();
        WorldAccessor accessor = serverManager.getAccessor(player.getWorld());

        accessor.getManager().placeBlock(event.getInteractionPoint().toVector().toVector3i(), block);

    }


}
