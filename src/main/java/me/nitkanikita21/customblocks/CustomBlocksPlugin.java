package me.nitkanikita21.customblocks;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.vavr.collection.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.packet.BlockPacketListener;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.Command;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomBlocksPlugin extends JavaPlugin {
    PaperCommandManager<Source> commandManager;

    @Override
    public void onEnable() {
//        initConfigs("config.conf", "events.conf");
        initCommandManager();

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
            .reEncodeByDefault(true);
        PacketEvents.getAPI().load();

        new ServerBlockManager(this, commandManager);
    }



    private void initConfigs(String... configs) {
        List.ofAll(Arrays.stream(configs)).forEach(config -> {
            File configFile = new File(getDataFolder(), config);
            if (configFile.exists()) return;

            try (InputStream inputStream = getClassLoader().getResourceAsStream(config)) {
                if (inputStream == null) throw new IOException(String.format("Resource %s not found!", config));
                Files.copy(inputStream, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initCommandManager() {
        commandManager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this);
        commandManager.captionRegistry().registerProvider(MinecraftHelp.defaultCaptionsProvider());
    }

    private void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}
