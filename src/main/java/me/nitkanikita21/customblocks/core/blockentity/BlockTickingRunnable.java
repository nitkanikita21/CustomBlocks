package me.nitkanikita21.customblocks.core.blockentity;

import io.papermc.paper.math.BlockPosition;
import lombok.RequiredArgsConstructor;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import org.joml.Vector3i;

@RequiredArgsConstructor
public class BlockTickingRunnable<T extends BlockEntity> implements Runnable {
    public final Vector3i pos;
    public final WorldAccessor accessor;
    public final BlockEntityProvider provider;

    @Override
    public void run() {
        BlockState blockState = accessor.getManager().getBlockState(pos);
        BlockEntityType<T> blockEntityType = (BlockEntityType<T>) provider.getBlockEntityType();
        provider.getTicker(accessor, blockState, blockEntityType)
            .tick(accessor, pos, blockState, blockEntityType.get(accessor, pos).getOrElseThrow(RuntimeException::new));
    }
}
