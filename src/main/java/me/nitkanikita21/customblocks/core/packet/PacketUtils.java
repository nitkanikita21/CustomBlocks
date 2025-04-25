package me.nitkanikita21.customblocks.core.packet;

import org.bukkit.block.BlockFace;

public class PacketUtils {
    public static BlockFace toBukkitFace(com.github.retrooper.packetevents.protocol.world.BlockFace face) {
        return switch (face) {
            case DOWN -> BlockFace.DOWN;
            case UP -> BlockFace.UP;
            case NORTH -> BlockFace.NORTH;
            case SOUTH -> BlockFace.SOUTH;
            case WEST -> BlockFace.WEST;
            case EAST -> BlockFace.EAST;

            //what?
            case OTHER -> BlockFace.SELF;
        };
    }
}