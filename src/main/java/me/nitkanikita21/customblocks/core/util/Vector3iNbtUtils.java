package me.nitkanikita21.customblocks.core.util;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.joml.Vector3i;

public class Vector3iNbtUtils {
    public static CompoundBinaryTag serialize(CompoundBinaryTag compound, Vector3i vector) {
        compound = compound.putInt("x", vector.x());
        compound = compound.putInt("y", vector.y());
        compound = compound.putInt("z", vector.z());
        return compound;
    }

    public static Vector3i deserialize(CompoundBinaryTag compound) {
        return new Vector3i(
            compound.getInt("x"),
            compound.getInt("y"),
            compound.getInt("z")
        );
    }
}
