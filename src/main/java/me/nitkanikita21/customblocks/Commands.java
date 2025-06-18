package me.nitkanikita21.customblocks;

import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.registry.Blocks;
import me.nitkanikita21.customblocks.core.registry.Registries;
import me.nitkanikita21.registry.cloud.RegistryEntryValueParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;

import static me.nitkanikita21.registry.cloud.RegistryEntryValueParser.registryEntryValueParser;

public class Commands {
    public Commands(ServerBlockManager serverBlockManager, CommandManager<Source> manager) {
        Command.Builder<Source> root = manager.commandBuilder("customblocks");
        manager.command(
            root.literal("test")
                .senderType(PlayerSource.class)
                .required("block", registryEntryValueParser(Registries.BLOCKS))
                .optional("count", IntegerParser.integerParser())
                .handler(ctx -> {
                    Block block = ctx.get("block");



                    Player source = ctx.sender().source();
                    ItemStack itemStack = block.getItemStack(source);

                    itemStack.setAmount(ctx.getOrDefault("count", 1));

                    source.getInventory()
                        .addItem(itemStack);
                })
        );

        manager.command(
            root.literal("save-all")
                .handler(ctx -> {
                    Bukkit.getServer().getWorlds().forEach(serverBlockManager::saveWorld);
                })
        );

        /*manager.command(
            root.literal("cb")
                .required("id", StringParser.stringParser())
                .handler(ctx -> {
                    if(ctx.sender() instanceof PlayerSource plSource) {
                        serverBlockManager.getScheduler().runTask(() -> {
                            Player pl = plSource.source();
                            BlockData blockData = Material.COMMAND_BLOCK.createBlockData();
                            pl.getWorld().setBlockData(
                                pl.getLocation(),
                                blockData
                            );

                            org.bukkit.block.Block block = pl.getWorld().getBlockAt(pl.getLocation());
                            if (block.getState() instanceof CommandBlock commandBlock) {
                                commandBlock.setCommand("customblocks cb 1");
                                commandBlock.update();
                            }
                        });
                    }
                })
        );*/
    }



}
