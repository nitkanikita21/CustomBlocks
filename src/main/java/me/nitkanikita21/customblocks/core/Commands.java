package me.nitkanikita21.customblocks.core;

import me.nitkanikita21.customblocks.core.registry.Blocks;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;

public class Commands {
    public Commands(ServerBlockManager serverBlockManager, CommandManager<Source> manager) {
        Command.Builder<Source> root = manager.commandBuilder("customblocks");


        manager.command(
            root.literal("test")
                .senderType(PlayerSource.class)
                .handler(ctx -> {
                    Player source = ctx.sender().source();
                    source.getInventory()
                        .addItem(Blocks.NOT_FOUND.getItemStack(source));
                })
        );
    }



}
