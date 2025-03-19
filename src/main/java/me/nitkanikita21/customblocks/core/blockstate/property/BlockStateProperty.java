package me.nitkanikita21.customblocks.core.blockstate.property;


import net.kyori.adventure.nbt.CompoundBinaryTag;

public interface BlockStateProperty<T> {
    String getName();
    T load(CompoundBinaryTag compound);
    CompoundBinaryTag save(CompoundBinaryTag compound, Object value);
}
