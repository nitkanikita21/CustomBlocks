package me.nitkanikita21.customblocks.core.defimpl;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.math.BlockPosition;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.block.BlockProperties;
import me.nitkanikita21.customblocks.core.block.BlockWithEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityTicker;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityType;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.blockstate.property.BooleanStateProperty;
import me.nitkanikita21.customblocks.core.registry.BlockEntityTypes;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

public class NotFoundBlock extends BlockWithEntity {

    public static BooleanStateProperty FLIPPED = new BooleanStateProperty("flipped");

    public NotFoundBlock(BlockProperties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockState getDefaultState() {
        BlockState state = super.getDefaultState();
        state.setProperty(FLIPPED, false);
        return state;
    }

    @Override
    public WrappedBlockState getClientBlock(BlockState state, WorldAccessor world, Vector3i pos) {
        Material type;
        Boolean isFlipped = state.getProperty(FLIPPED).getOrElse(false);
        if(isFlipped) {
            type = Material.BLACK_CONCRETE;
        } else {
            type = Material.PURPLE_CONCRETE;
        }

        state.setProperty(FLIPPED, !isFlipped);

        return SpigotConversionUtil.fromBukkitBlockData(type.createBlockData());
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntityTypes.NOT_FOUND;
    }

    @Override
    public BlockEntity createBlockEntity(WorldAccessor world, Vector3i pos, BlockState blockState) {
        return new NotFoundBlockEntity(pos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(WorldAccessor world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, BlockEntityTypes.NOT_FOUND, NotFoundBlockEntity::tick);
    }
}
