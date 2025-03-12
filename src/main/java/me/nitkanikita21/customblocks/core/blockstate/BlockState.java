package me.nitkanikita21.customblocks.core.blockstate;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.blockstate.property.BlockStateProperty;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BlockState implements Cloneable {
    @Getter
    final Block owner;
    final Map<BlockStateProperty<?>, Object> properties;

    public <T> Option<T> getProperty(BlockStateProperty<T> property) {
        return (Option<T>) properties.get(property);
    }

    public <T> BlockState setProperty(BlockStateProperty<T> property, T value) {
        return new BlockState(owner, properties.put(property, value));
    }

    @Override
    public BlockState clone() {
        return new BlockState(owner, properties);
    }

    @Override
    public String toString() {
        return properties.toString();
    }
}
