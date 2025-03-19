package me.nitkanikita21.customblocks.core.defimpl;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.block.BlockProperties;
import me.nitkanikita21.customblocks.core.block.BlockWithEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityTicker;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityType;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.blockstate.property.BooleanStateProperty;
import me.nitkanikita21.customblocks.core.registry.BlockEntityTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

public class EnderChestBlock extends BlockWithEntity {
    public static BooleanStateProperty IS_OPEN = new BooleanStateProperty("IsOpen");

    public EnderChestBlock() {
        super(
            BlockProperties.builder()
                .name(Component.text("Ender chest"))
                .build()
        );
    }

    @Override
    public @NotNull BlockState getDefaultState() {
        return super.getDefaultState()
            .setProperty(IS_OPEN, false);
    }

    @Override
    public WrappedBlockState getClientBlock(BlockState state, WorldAccessor world, Vector3i pos) {
        BlockData blockData = Material.BARREL.createBlockData();
        if(state.getProperty(IS_OPEN).getOrElse(false)){
            ((Barrel)blockData).setOpen(true);
        }
        ((Directional)blockData).setFacing(BlockFace.UP);

        return SpigotConversionUtil.fromBukkitBlockData(blockData);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntityTypes.ENDER_CHEST;
    }

    @Override
    public void onInteract(BlockState state, WorldAccessor world, Vector3i pos, Player player, Action action, BlockFace face) {
        EnderChestBlockEntity blockEntity = (EnderChestBlockEntity) world.getManager().getBlockEntity(pos).get();
        if(player.getGameMode() == GameMode.CREATIVE && action == Action.LEFT_CLICK_BLOCK)return;

        if(player.isSneaking()) {
            Material channel = player.getInventory().getItemInMainHand().getType();
            blockEntity.setChannel(channel);
            player.sendMessage(Component.text("Set channel to " + channel.name()));
        } else {
            blockEntity.openInventory(player);
        }


    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(WorldAccessor world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, BlockEntityTypes.ENDER_CHEST, EnderChestBlockEntity::tick);
    }
}
