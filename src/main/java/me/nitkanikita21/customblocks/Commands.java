package me.nitkanikita21.customblocks;

import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.registry.Blocks;
import me.nitkanikita21.customblocks.core.registry.Registries;
import me.nitkanikita21.customblocks.core.transfer.InventoryStorage;
import me.nitkanikita21.customblocks.core.transfer.Transaction;
import me.nitkanikita21.customblocks.core.transfer.TransactionContext;
import me.nitkanikita21.registry.cloud.RegistryEntryValueParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
        Command.Builder<Source> root = manager.commandBuilder("customblocks", "cb");
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

        manager.command(
            root.literal("test-transfer-api")
                .handler( commandCtx -> {
                    Inventory inv1 = Bukkit.createInventory(null, 9, "Source");
                    Inventory inv2 = Bukkit.createInventory(null, 9, "Target");

                    InventoryStorage source = new InventoryStorage(inv1);
                    InventoryStorage target = new InventoryStorage(inv2);

                    TransactionContext ctx = new TransactionContext();

                    // Add 5 diamonds to source
                    inv1.addItem(new ItemStack(Material.DIAMOND, 5));

                    ItemStack diamonds = new ItemStack(Material.DIAMOND, 5);

                    // Extract from source
                    long extracted = source.tryExtract(diamonds, 5, ctx);
                    System.out.println("Extracted from source: " + extracted);

                    // Insert into target
                    long inserted = target.tryInsert(diamonds, extracted, ctx);
                    System.out.println("Inserted into target: " + inserted);

                    // Check contents
                    System.out.println("Source inventory:");
                    for (ItemStack stack : inv1.getContents()) {
                        System.out.println(stack);
                    }

                    System.out.println("Target inventory:");
                    for (ItemStack stack : inv2.getContents()) {
                        System.out.println(stack);
                    }

                    // Test rollback
                    ItemStack diamonds2 = new ItemStack(Material.DIAMOND, 3);
                    try (Transaction tx = ctx.openTransaction()) {
                        source.tryInsert(diamonds2, 3, ctx); // insert
                        tx.addRollback(() -> System.out.println("Rollback executed!"));
                        // no commit → should rollback automatically
                    }
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
