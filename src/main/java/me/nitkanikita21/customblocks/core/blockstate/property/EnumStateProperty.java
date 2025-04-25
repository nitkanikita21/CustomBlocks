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

    @Getter
    public final List<T> allowed;

    public EnumStateProperty(String name, Class<T> enumClass) {
        this.name = name;
        this.enumClass = enumClass;
        allowed = List.empty();
    }

    @Override
    public T load(CompoundBinaryTag compound) {
        return List.of(enumClass.getEnumConstants())
            .find(t -> t.name().equals(compound.getString(name)))
            .getOrElseThrow( () -> new IllegalArgumentException("Unknown enum value: " + compound.getString(name)));
    }

    @Override
    public CompoundBinaryTag save(CompoundBinaryTag compound, Object value) {
        return compound.putString(name, ((T) value).name());
    }

    @Override
    public boolean validate(Object value) {
        T casted = (T) value;
        if (allowed.isEmpty())return true;
        else return allowed.contains(casted);
    }
}
