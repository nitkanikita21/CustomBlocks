package me.nitkanikita21.customblocks.core.blockentity;

import io.papermc.paper.math.BlockPosition;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

public interface BlockEntityProvider {

    BlockEntityType<?> getBlockEntityType();
    BlockEntity createBlockEntity(WorldAccessor world, Vector3i pos, BlockState blockState);

    @Nullable
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(WorldAccessor world, BlockState state, BlockEntityType<T> type) {
        return null;
    }
}