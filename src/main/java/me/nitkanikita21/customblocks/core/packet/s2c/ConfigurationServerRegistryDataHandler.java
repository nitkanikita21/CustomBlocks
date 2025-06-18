package me.nitkanikita21.customblocks.core.packet.s2c;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.configuration.server.WrapperConfigServerRegistryData;
import io.vavr.collection.Iterator;
import me.nitkanikita21.customblocks.core.packet.AbstractPacketHandler;

public class ConfigurationServerRegistryDataHandler extends AbstractPacketHandler<PacketSendEvent, WrapperConfigServerRegistryData> {
    public ConfigurationServerRegistryDataHandler() {
        super(PacketType.Configuration.Server.REGISTRY_DATA, WrapperConfigServerRegistryData::new);
    }

    @Override
    public void handle(PacketSendEvent event, WrapperConfigServerRegistryData wrapper) {
        wrapper.setElements(
            Iterator.ofAll(wrapper.getElements())
                .filter(
                    e -> {
//                        System.out.println(e.getId());
                        return e.getId().getNamespace().equals("minecraft");
                    }
                )
                .toJavaList()
        );
    }
}
