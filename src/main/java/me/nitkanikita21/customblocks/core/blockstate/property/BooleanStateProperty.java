package me.nitkanikita21.customblocks.core.blockstate.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.libs.kyori.adventure.nbt.CompoundBinaryTag;

@RequiredArgsConstructor
public class BooleanStateProperty implements BlockStateProperty<Boolean>{
    @Getter
    private final String name;

    @Override
    public Boolean deserialize(CompoundBinaryTag compound) {
        return compound.getBoolean(name);
    }

    @Override
    public void serialize(CompoundBinaryTag compound, Boolean value) {
        compound.putBoolean(name, value);
    }
}
