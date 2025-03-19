package me.nitkanikita21.customblocks.core.blockstate.property;

import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.nbt.CompoundBinaryTag;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class EnumStateProperty<T extends Enum<T>> implements BlockStateProperty<T>{
    @Getter
    private final String name;
    private final Class<T> enumClass;

    @Override
    public T load(CompoundBinaryTag compound) {
        return List.of(enumClass.getEnumConstants())
            .find(t -> t.name().equals(compound.getBoolean(name)))
            .getOrElseThrow( () -> new IllegalArgumentException("Unknown enum value: " + compound.getBoolean(name)));
    }

    @Override
    public CompoundBinaryTag save(CompoundBinaryTag compound, Object value) {
        return compound.putString(name, ((T) value).name());
    }
}
