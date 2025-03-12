package me.nitkanikita21.customblocks.core.registry;

import io.vavr.collection.Iterator;
import io.vavr.control.Option;
import lombok.experimental.UtilityClass;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityType;
import me.nitkanikita21.registry.Identifier;
import me.nitkanikita21.registry.Registry;
import me.nitkanikita21.registry.RegistryEntry;

@UtilityClass
public class Registries {
    public static Registry<Block> BLOCKS = Registry.create(new Identifier("customblocks:blocks"));
    public static Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = Registry.create(new Identifier("customblocks:block_entity_types"));

    public static <T> Option<Identifier> getIdentifier(Registry<T> registry, T entry) {
        return Iterator.ofAll(registry.getAll())
            .find(regEntry -> regEntry.getValue() == entry)
            .map(RegistryEntry::getId);
    }
}
