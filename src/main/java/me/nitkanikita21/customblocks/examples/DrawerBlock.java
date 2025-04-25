package me.nitkanikita21.customblocks.examples;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.vavr.control.Option;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.block.ActionResult;
import me.nitkanikita21.customblocks.core.block.BlockProperties;
import me.nitkanikita21.customblocks.core.block.BlockWithEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityType;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.blockstate.DefaultStateProperties;
import me.nitkanikita21.customblocks.core.util.Vector3iUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.UUID;

import static me.nitkanikita21.customblocks.core.registry.BlockEntityTypes.DRAWER;

public class DrawerBlock extends BlockWithEntity {
    private static final String HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjlkY2U1OWJhNzJmODQ3MzZlMWVhOWMzYjQzZWIxYWFkYzQ5N2IyOTA0YTFjZGEwODk3MjY5MmRjMWI0Y2E3NSJ9fX0=";


    public DrawerBlock() {
        super(
            BlockProperties.builder()
                .name(
                    MiniMessage.miniMessage()
                        .deserialize("<i:false><aqua>Storage drawer")
                )
                .hardness(2)
                .build()
        );
    }

    @Override
    public @NotNull BlockState getDefaultState() {
        return super.getDefaultState().setProperty(DefaultStateProperties.HORIZONTAL_FACING, BlockFace.SOUTH);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return DRAWER;
    }

    @Override
    public WrappedBlockState getClientBlock(BlockState state, WorldAccessor world, Vector3i pos) {
        BlockData blockData = Material.BEEHIVE.createBlockData();

        ((Directional) blockData).setFacing(state.getProperty(DefaultStateProperties.HORIZONTAL_FACING).get());
        return SpigotConversionUtil.fromBukkitBlockData(blockData);
    }

    @Override
    public void onPlace(BlockState state, WorldAccessor world, Vector3i pos) {
        DrawerBlockEntity blockEntity = (DrawerBlockEntity) world.getManager().getBlockEntity(pos).get();
        blockEntity.onPlaced(world, state);
    }

    @Override
    public void onPlace(BlockState state, WorldAccessor world, Vector3i pos, Player player, Action action, BlockFace face) {
        BlockState newState = state.setProperty(
            DefaultStateProperties.HORIZONTAL_FACING,
            player.getFacing().getOppositeFace()
        );
        world.getManager().setBlockState(
            pos, newState
        );
        onPlace(newState, world, pos);
    }

    @Override
    public void onRemove(BlockState state, WorldAccessor world, Vector3i pos) {
        DrawerBlockEntity blockEntity = (DrawerBlockEntity) world.getManager().getBlockEntity(pos).get();
        blockEntity.onDestroy();
    }

    @Override
    public boolean onBreak(BlockState state, WorldAccessor world, Vector3i pos, Player player, Action action, BlockFace face) {
        BlockFace blockFace = state.getProperty(DefaultStateProperties.HORIZONTAL_FACING).get();
        if (blockFace == face && player.getGameMode() == GameMode.CREATIVE) {
            return false;
        } else {
            onRemove(state, world, pos);
            world.getWorld().spawnParticle(
                Particle.BLOCK,
                Vector3iUtils.toLocation(world.getWorld(), new Vector3f(pos).add(0.5f, 0.5f, 0.5f)),
                45,
                0.25, 0.25, 0.25,
                Material.BEEHIVE.createBlockData()
            );

//            world.getWorld().
            return true;
        }
    }

    @Override
    public ActionResult onInteract(BlockState state, WorldAccessor world, Vector3i pos, Player player, Action action, BlockFace face) {
        System.out.println("INTERACT");

        BlockFace blockFace = state.getProperty(DefaultStateProperties.HORIZONTAL_FACING).getOrElse(face);
        if (blockFace != face) return ActionResult.PASS;

        Option<DrawerBlockEntity> optionalEntity = world.getManager().getBlockEntity(pos).map(e -> (DrawerBlockEntity) e);
        if (optionalEntity.isEmpty()) return ActionResult.FAIL;

        DrawerBlockEntity blockEntity = optionalEntity.get();

        PlayerInventory inventory = player.getInventory();
        ItemStack itemInMainHand = inventory.getItemInMainHand();

        if (action.isRightClick()) {
            if (blockEntity.getItemStack().isEmpty()) {
                if (itemInMainHand.isEmpty()) return ActionResult.FAIL;

                inventory.remove(itemInMainHand);
                blockEntity.setItemStack(itemInMainHand.clone());
                blockEntity.setAmount(itemInMainHand.getAmount());

            } else if (blockEntity.getItemStack().isSimilar(itemInMainHand)) {
                blockEntity.setAmount(blockEntity.getAmount() + itemInMainHand.getAmount());
                inventory.removeItem(itemInMainHand);
            } else {
                return ActionResult.PASS;
            }

            player.swingMainHand();
            return ActionResult.SUCCESS;

        } else if (action.isLeftClick()) {
            if (blockEntity.getItemStack().isEmpty()) return ActionResult.FAIL;

            ItemStack itemStack = blockEntity.getItemStack().clone();

            int available = blockEntity.getAmount();
            int maxStack = itemStack.getType().getMaxStackSize();
            int amountToDrop = player.isSneaking() ? Math.min(available, maxStack) : 1;

            itemStack.setAmount(amountToDrop);
            player.getWorld().dropItem(player.getLocation(), itemStack);
            blockEntity.setAmount(available - amountToDrop);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    protected ItemStack buildItemStack() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        head.editMeta(meta -> {
            meta.displayName(properties.getName());
            SkullMeta skullMeta = (SkullMeta) meta;

            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", HEAD_TEXTURE));
            skullMeta.setPlayerProfile(profile);
            head.setItemMeta(meta);
        });
        return head;
    }
}
