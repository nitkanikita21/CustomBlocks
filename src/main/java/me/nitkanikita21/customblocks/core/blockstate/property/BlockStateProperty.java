package me.nitkanikita21.customblocks.core.blockstate.property;

import me.clip.placeholderapi.libs.kyori.adventure.nbt.CompoundBinaryTag;

public interface BlockStateProperty<T> {
    String getName();
    T deserialize(CompoundBinaryTag compound);
    void serialize(CompoundBinaryTag compound, T value);
}
