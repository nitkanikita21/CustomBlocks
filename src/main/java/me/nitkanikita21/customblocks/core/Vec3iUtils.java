package me.nitkanikita21.customblocks.core;

import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Vector3i;

public class Vec3iUtils {
    public static Location toLocation(World world, Vector3i pos) {
        return new Location(world, pos.x, pos.y, pos.z);
    }
}
