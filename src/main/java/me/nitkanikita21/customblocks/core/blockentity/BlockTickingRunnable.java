package me.nitkanikita21.customblocks.core.blockentity;

import io.vavr.control.Option;
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

        Option.of(provider.getTicker(accessor, blockState, blockEntityType))
            .peek(t -> t.tick(
                accessor,
                pos,
                blockState,
                blockEntityType.get(accessor, pos)
                    .getOrElseThrow(RuntimeException::new)
            ));
    }
}
