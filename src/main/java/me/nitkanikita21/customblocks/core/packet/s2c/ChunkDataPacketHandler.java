package me.nitkanikita21.customblocks.core.packet.s2c;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import io.vavr.control.Option;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.packet.AbstractPacketHandler;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.joml.Vector3i;

import java.util.stream.IntStream;

public class ChunkDataPacketHandler extends AbstractPacketHandler<PacketSendEvent, WrapperPlayServerChunkData> {
    final ServerBlockManager manager;
    final BukkitTaskScheduler scheduler;

    public ChunkDataPacketHandler(ServerBlockManager manager, BukkitTaskScheduler scheduler) {
        super(PacketType.Play.Server.CHUNK_DATA, WrapperPlayServerChunkData::new);
        this.manager = manager;
        this.scheduler = scheduler;
    }

    @Override
    public void handle(PacketSendEvent event, WrapperPlayServerChunkData wrapper) {
        int chunkX = wrapper.getColumn().getX();
        int chunkZ = wrapper.getColumn().getZ();

        BaseChunk[] chunks = wrapper.getColumn().getChunks();
        for (int i = 0; i < chunks.length; i++) {
            final int chunkI = i;
            BaseChunk chunk = chunks[chunkI];
            if (chunk == null) continue;
            IntStream.range(0, 16).forEach(dx ->
                IntStream.range(0, 16).forEach(dy ->
                    IntStream.range(0, 16).forEach(dz -> {
                        Player player = event.getPlayer();
                        World world = player.getWorld();

                        int globalX = (chunkX << 4) + dx;
                        int globalZ = (chunkZ << 4) + dz;
                        int globalY = (chunkI << 4) + dy + world.getMinHeight();

                        WorldAccessor accessor = manager.getAccessor(world);
//                                Vector3i pos = new Vector3i(globalX, globalY, globalZ);
                        Vector3i vector3i = new Vector3i(globalX, globalY, globalZ);

                        Option<BlockState> blockStates = manager.getManager(world).tryGetBlockState(vector3i);
                        blockStates.peek(bs -> {
                            WrappedBlockState clientBlock = bs.getOwner().getClientBlock(bs, accessor, vector3i, player);
                            chunk.set(dx, dy, dz, clientBlock);
                        });


//                                manager.getScheduler().runTaskLater(() -> world.setType(globalX, globalY, globalZ, Material.RED_WOOL), 20*5);
                    })));
        }
    }
}
