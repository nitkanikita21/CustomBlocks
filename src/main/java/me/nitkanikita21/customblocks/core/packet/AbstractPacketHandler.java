package me.nitkanikita21.customblocks.core.packet;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public abstract class AbstractPacketHandler<E extends ProtocolPacketEvent, W extends PacketWrapper<W>> {
    private final Enum<?> packetType;
    private final Function<E, W> wrapperFactory;

    public void handleCast(E event, PacketWrapper<?> wrapper) {
        handle(event, (W)wrapper);
    }
    public abstract void handle(E event, W wrapper);
}
