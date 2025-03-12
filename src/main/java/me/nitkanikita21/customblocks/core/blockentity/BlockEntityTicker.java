package me.nitkanikita21.customblocks.core.blockentity;

import io.papermc.paper.math.BlockPosition;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import org.joml.Vector3i;

@FunctionalInterface
public interface BlockEntityTicker<T extends BlockEntity> {
    void tick(WorldAccessor world, Vector3i pos, BlockState state, T blockEntity);
}
