package me.nitkanikita21.customblocks.core.blockentity;

import io.vavr.control.Option;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import org.joml.Vector3i;

public class BlockEntityType<T extends BlockEntity> {
    private final BlockEntityFactory<T> factory;

    public BlockEntityType(BlockEntityFactory<T> factory) {
        this.factory = factory;
    }

    public T create(Vector3i pos, BlockState state) {
        return factory.create(pos, state);
    }

    public Option<T> get(WorldAccessor worldAccessor, Vector3i pos) {
        Option<BlockEntity> blockEntity = worldAccessor.getManager().getBlockEntity(pos);
//        return (T)(blockEntity != null && blockEntity.getType() == this ? blockEntity : null);
        return blockEntity
            .filter((be) -> be.getType() == this)
            .map(be -> (T) be);
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(Vector3i pos, BlockState state);
    }
}
