package me.nitkanikita21.customblocks.core;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.vavr.Tuple2;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.nitkanikita21.customblocks.Commands;
import me.nitkanikita21.customblocks.common.EventRegister;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.listener.ItemBlockListener;
import me.nitkanikita21.customblocks.core.packet.CustomBlocksPacketListener;
import me.nitkanikita21.customblocks.core.registry.BlockEntityTypes;
import me.nitkanikita21.customblocks.core.registry.Blocks;
import me.nitkanikita21.customblocks.core.snapshot.WorldSnapshot;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.util.sender.Source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServerBlockManager implements Listener {
    final Server server;
    private final EventRegister eventRegister;
    Map<World, BlockManager> blockStateManagers;
    private final CommandManager<Source> commandManager;
    private final BukkitTaskScheduler scheduler;

    public ServerBlockManager(Plugin plugin, CommandManager<Source> commandManager) {
        this.server = plugin.getServer();
        this.commandManager = commandManager;
        this.scheduler = new BukkitTaskScheduler(plugin);
        this.eventRegister = new EventRegister(plugin);

        Blocks.DEFERRED.registerAll();
        BlockEntityTypes.DEFERRED.registerAll();

        blockStateManagers = Iterator.ofAll(server.getWorlds())
            .toMap(w -> w, this::createBlockManager);

        server.getPluginManager().registerEvents(
            new ItemBlockListener(this),
            plugin
        );
        server.getPluginManager().registerEvents(
            this,
            plugin
        );
        PacketEvents.getAPI().getEventManager().registerListener(new CustomBlocksPacketListener(this, scheduler),
            PacketListenerPriority.HIGHEST);
        new Commands(this, commandManager);

    }

    public BlockManager getManager(World world) {
//        return blockStateManagers.get(world).getOrElseThrow(RuntimeException::new);
        Tuple2<BlockManager, ? extends Map<World, BlockManager>> t = blockStateManagers.computeIfAbsent(world, this::createBlockManager);
        blockStateManagers = t._2;
        return t._1;
    }

    private BlockManager createBlockManager(World world) {
        return new BlockManager(world, scheduler, eventRegister).initialize();
    }

    public WorldAccessor getAccessor(World world) {
        return blockStateManagers.get(world)
            .map(m -> new WorldAccessor(world, m))
            .getOrElseThrow(RuntimeException::new);
    }

    public Option<BlockManager> tryGetManager(World world) {
        return blockStateManagers.get(world);
    }

    public Option<WorldAccessor> tryGetAccessor(World world) {
        return blockStateManagers.get(world)
            .map(m -> new WorldAccessor(world, m));
    }


    @EventHandler
    private void loadWorld(WorldLoadEvent event) {
        World world = event.getWorld();
        BlockManager manager = getManager(world);
        File file = new File(world.getWorldFolder(), "customblocks.dat");
        if (!file.exists()) return;

        try (FileInputStream stream = new FileInputStream(file)) {
            CompoundBinaryTag snapshotCompound = BinaryTagIO.reader().read(stream);
            WorldSnapshot worldSnapshot = new WorldSnapshot();
            worldSnapshot.load(snapshotCompound);
            manager.applySnapshot(worldSnapshot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveWorld(World world) {
        BlockManager manager = getManager(world);
        File file = new File(world.getWorldFolder(), "customblocks.dat");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream stream = new FileOutputStream(file)) {
            CompoundBinaryTag snapshotCompound = CompoundBinaryTag.empty();
            snapshotCompound = manager.getSnapshot().save(snapshotCompound);
            BinaryTagIO.writer().write(snapshotCompound, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
