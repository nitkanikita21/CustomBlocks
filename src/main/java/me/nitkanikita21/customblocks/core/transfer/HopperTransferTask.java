package me.nitkanikita21.customblocks.core.transfer;

import io.papermc.paper.math.BlockPosition;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockStorageProvider;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class HopperTransferTask implements Runnable {
    private final WorldAccessor accessor;

    @Override
    public void run() {
        for (Chunk chunk : accessor.getWorld().getLoadedChunks()) {
            for (BlockState state : chunk.getTileEntities()) {
                if (state instanceof Hopper hopper) {



                    hopper.getInventory();

                    Option<BlockEntity> blockEntity = accessor.getManager()
                        .getBlockEntity(
                            hopper.getBlock().getRelative(((Directional) hopper.getBlockData()).getFacing())
                                .getLocation().toVector().toVector3i()
                        );
                    blockEntity
                        .toTry()
                        .mapTry((be) -> (BlockStorageProvider<InventoryStorage>) be)
                        .map(BlockStorageProvider::getStorage)
                        .peek((targetStorage) -> {
                            TransactionContext ctx = new TransactionContext();
                            InventoryStorage sourceStorage = new InventoryStorage(hopper.getInventory());
                            try (Transaction tx = ctx.openTransaction()) {
                                ItemStack picked = sourceStorage.tryExtractAny(1, ctx);

                                if (picked == null) return;

                                long inserted = targetStorage.tryInsert(picked, picked.getAmount(), ctx);
                                if (inserted > 0) tx.commit();
                            }
                        });


                }


            }
        }
    }
}
