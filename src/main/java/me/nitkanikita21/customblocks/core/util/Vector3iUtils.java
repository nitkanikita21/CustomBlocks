package me.nitkanikita21.customblocks.core.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Vector3iUtils {
    public static Location toLocation(World world, Vector3i pos) {
        return new Location(world, pos.x, pos.y, pos.z);
    }
    public static Location toLocation(World world, Vector3f pos) {
        return new Location(world, pos.x, pos.y, pos.z);
    }
}
