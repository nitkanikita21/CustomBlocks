package me.nitkanikita21.customblocks.core.registry;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.block.BlockProperties;
import me.nitkanikita21.customblocks.core.defimpl.NotFoundBlock;
import me.nitkanikita21.registry.DeferredRegistry;
import me.nitkanikita21.registry.Registry;
import net.kyori.adventure.text.Component;

@UtilityClass
public class Blocks {
    public static final DeferredRegistry<Block> DEFERRED =
        new DeferredRegistry<>("customblocks", Registries.BLOCKS);

    public static final Block NOT_FOUND = DEFERRED.register("not_found",
        new NotFoundBlock(
            BlockProperties.builder()
                .name(Component.text("Block not found"))
                .build()
        )
    );
}
