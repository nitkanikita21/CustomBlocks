package me.nitkanikita21.customblocks.core.registry;

import me.nitkanikita21.customblocks.core.blockentity.BlockEntityType;
import me.nitkanikita21.customblocks.examples.DrawerBlockEntity;
import me.nitkanikita21.customblocks.examples.EnderChestBlockEntity;
import me.nitkanikita21.registry.DeferredRegistry;

public class BlockEntityTypes {
    public static final DeferredRegistry<BlockEntityType<?>> DEFERRED =
        new DeferredRegistry<>("customblocks", Registries.BLOCK_ENTITY_TYPES);

    public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST = DEFERRED.register("ender_chest",
        new BlockEntityType<>(EnderChestBlockEntity::new)
    );
    public static final BlockEntityType<DrawerBlockEntity> DRAWER = DEFERRED.register("drawer",
        new BlockEntityType<>(DrawerBlockEntity::new)
    );
}
