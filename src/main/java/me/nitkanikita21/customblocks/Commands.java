package me.nitkanikita21.customblocks;

import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.registry.Blocks;
import me.nitkanikita21.customblocks.core.registry.Registries;
import me.nitkanikita21.registry.cloud.RegistryEntryValueParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;

import static me.nitkanikita21.registry.cloud.RegistryEntryValueParser.registryEntryValueParser;

public class Commands {
    public Commands(ServerBlockManager serverBlockManager, CommandManager<Source> manager) {
        Command.Builder<Source> root = manager.commandBuilder("customblocks");
        manager.command(
            root.literal("test")
                .senderType(PlayerSource.class)
                .required("block", registryEntryValueParser(Registries.BLOCKS))
                .handler(ctx -> {
                    Block block = ctx.get("block");

                    Player source = ctx.sender().source();
                    source.getInventory()
                        .addItem(block.getItemStack(source));
                })
        );

        manager.command(
            root.literal("save-all")
                .handler(ctx -> {
                    Bukkit.getServer().getWorlds().forEach(serverBlockManager::saveWorld);
                })
        );
    }



}
