package me.nitkanikita21.customblocks.core.registry;

import me.nitkanikita21.customblocks.core.defimpl.NotFoundBlock;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityType;
import me.nitkanikita21.customblocks.core.defimpl.NotFoundBlockEntity;
import me.nitkanikita21.registry.DeferredRegistry;

public class BlockEntityTypes {
    static final DeferredRegistry<BlockEntityType<?>> DEFERRED =
        new DeferredRegistry<>("customblocks", Registries.BLOCK_ENTITY_TYPES);

    public static final BlockEntityType<NotFoundBlockEntity> NOT_FOUND = DEFERRED.register("not_found",
        new BlockEntityType<>(NotFoundBlockEntity::new)
    );
}
