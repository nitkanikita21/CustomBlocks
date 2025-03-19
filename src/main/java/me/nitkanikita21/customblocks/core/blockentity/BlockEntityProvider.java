package me.nitkanikita21.customblocks.core.blockentity;

import io.papermc.paper.math.BlockPosition;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

public interface BlockEntityProvider {

    BlockEntityType<?> getBlockEntityType();
    default BlockEntity createBlockEntity(Vector3i pos, BlockState blockState) {
        return getBlockEntityType().create(pos, blockState);
    }

    @Nullable
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(WorldAccessor world, BlockState state, BlockEntityType<T> type) {
        return null;
    }
}