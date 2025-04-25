package me.nitkanikita21.customblocks.core.registry;

import lombok.experimental.UtilityClass;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.examples.DrawerBlock;
import me.nitkanikita21.customblocks.examples.EnderChestBlock;
import me.nitkanikita21.customblocks.examples.MissingBlock;
import me.nitkanikita21.registry.DeferredRegistry;

@UtilityClass
public class Blocks {
    public static final DeferredRegistry<Block> DEFERRED =
        new DeferredRegistry<>("customblocks", Registries.BLOCKS);

    public static final Block NOT_FOUND = DEFERRED.register("missing",
        new MissingBlock()
    );

    public static final Block ENDER_CHEST = DEFERRED.register("ender_chest",
        new EnderChestBlock()
    );
    public static final Block DRAWER = DEFERRED.register("drawer",
        new DrawerBlock()
    );
}
