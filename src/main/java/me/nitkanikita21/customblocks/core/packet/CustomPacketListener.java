package me.nitkanikita21.customblocks.core.packet;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.packet.c2s.BlockBreakPacketHandler;
import me.nitkanikita21.customblocks.core.packet.s2c.BlockChangePacketHandler;
import me.nitkanikita21.customblocks.core.packet.s2c.ChunkDataPacketHandler;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomPacketListener implements PacketListener {

    List<AbstractPacketHandler<PacketReceiveEvent, ?>> c2sHandlers = List.empty();
    List<AbstractPacketHandler<PacketSendEvent, ?>> s2cHandlers = List.empty();

    public CustomPacketListener(ServerBlockManager manager, BukkitTaskScheduler scheduler) {

        s2c(
            new BlockChangePacketHandler(manager, scheduler),
            new ChunkDataPacketHandler(manager, scheduler)
        );
        c2s(
            new BlockBreakPacketHandler(manager, scheduler)
//            new PlayerDiggingPacketHandler(manager, scheduler)
        );
    }

    @SafeVarargs
    private void s2c(AbstractPacketHandler<PacketSendEvent, ?>... handlers) {
        s2cHandlers = s2cHandlers.appendAll(Iterator.of(handlers));
    }

    @SafeVarargs
    private void c2s(AbstractPacketHandler<PacketReceiveEvent, ?>... handlers) {
        c2sHandlers = c2sHandlers.appendAll(Iterator.of(handlers));
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        s2cHandlers.find(h -> h.getPacketType().equals(event.getPacketType()))
            .peek(handler -> {
                handler.handleCast(
                    event,
                    handler.getWrapperFactory().apply(event)
                );
            });
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        c2sHandlers.find(h -> h.getPacketType().equals(event.getPacketType()))
            .peek(handler -> {
                handler.handleCast(
                    event,
                    handler.getWrapperFactory().apply(event)
                );
            });
    }
}
