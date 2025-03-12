package me.nitkanikita21.customblocks.core;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.packet.BlockPacketListener;
import me.nitkanikita21.customblocks.core.registry.Blocks;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.util.sender.Source;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServerBlockManager {
    final Server server;
    final Map<World, BlockManager> blockStateManagers;
    private final CommandManager<Source> commandManager;

    public ServerBlockManager(Plugin plugin, CommandManager<Source> commandManager) {
        this.server = plugin.getServer();
        this.commandManager = commandManager;
        BukkitTaskScheduler bukkitTaskScheduler = new BukkitTaskScheduler(plugin);

        Blocks.DEFERRED.registerAll();
        Blocks.DEFERRED.registerAll();

        blockStateManagers = Iterator.ofAll(server.getWorlds())
            .toMap(w -> w, w -> new BlockManager(w, bukkitTaskScheduler));

        server.getPluginManager().registerEvents(
            new ItemBlockListener(this),
            plugin
        );
        PacketEvents.getAPI().getEventManager().registerListener(new BlockPacketListener(this),
            PacketListenerPriority.HIGHEST);
        new Commands(this, commandManager);
    }

    public BlockManager getManager(World world) {
        return blockStateManagers.get(world).getOrElseThrow(RuntimeException::new);
    }

    public WorldAccessor getAccessor(World world) {
        return blockStateManagers.get(world)
            .map(m -> new WorldAccessor(world, m))
            .getOrElseThrow(RuntimeException::new);
    }

    public Option<BlockManager> getSafeManager(World world) {
        return blockStateManagers.get(world);
    }

    public Option<WorldAccessor> getSafeAccessor(World world) {
        return blockStateManagers.get(world)
            .map(m -> new WorldAccessor(world, m));
    }
}
