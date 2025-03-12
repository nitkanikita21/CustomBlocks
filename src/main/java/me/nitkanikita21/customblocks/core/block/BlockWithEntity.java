package me.nitkanikita21.customblocks.core.block;

import io.papermc.paper.math.BlockPosition;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityProvider;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityTicker;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityType;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class BlockWithEntity extends Block implements BlockEntityProvider {
    public BlockWithEntity(BlockProperties properties) {
        super(properties);
    }

    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> validateTicker(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
}
