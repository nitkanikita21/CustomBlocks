package me.nitkanikita21.customblocks.core.blockstate.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.nbt.CompoundBinaryTag;

@RequiredArgsConstructor
public class BooleanStateProperty implements BlockStateProperty<Boolean>{
    @Getter
    private final String name;

    @Override
    public Boolean load(CompoundBinaryTag compound) {
        return compound.getBoolean(name);
    }

    @Override
    public CompoundBinaryTag save(CompoundBinaryTag compound, Object value) {
        return compound.putBoolean(name, (boolean) value);
    }
}
