package me.nitkanikita21.customblocks.core.transfer;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class InventoryStorage implements Storage<ItemStack> {

    @Getter
    private final Inventory inventory;

    public long tryInsert(ItemStack stack, long maxAmount, TransactionContext ctx) {
        try (Transaction tx = ctx.openTransaction()) {
            // Take a snapshot of current inventory for rollback
            Map<Integer, ItemStack> snapshot = HashMap.ofEntries(
                List.of(inventory.getContents())
                    .zipWithIndex()
                    .filter(tuple -> tuple._1 != null)
                    .map(tuple -> io.vavr.Tuple.of(tuple._2, tuple._1.clone()))
            );

            // Attempt to insert
            ItemStack clone = stack.clone();
            clone.setAmount((int) maxAmount);
            Map<Integer, ItemStack> leftover = HashMap.ofAll(inventory.addItem(clone));

            int inserted = (int) (maxAmount - leftover.values().map(ItemStack::getAmount).sum().longValue());

            // Register rollback action
            tx.addRollback(() -> snapshot.forEach((i, s) -> inventory.setItem(i, s)));

            if (inserted > 0) tx.commit();
            return inserted;
        }
    }

    public long tryExtract(ItemStack stack, long maxAmount, TransactionContext ctx) {
        try (Transaction tx = ctx.openTransaction()) {
            Map<Integer, ItemStack> snapshot = HashMap.ofEntries(
                List.of(inventory.getContents())
                    .zipWithIndex()
                    .filter(tuple -> tuple._1 != null)
                    .map(tuple -> io.vavr.Tuple.of(tuple._2, tuple._1.clone()))
            );

            int toExtract = (int) maxAmount;
            int extracted = 0;

            for (int i = 0; i < inventory.getSize() && toExtract > 0; i++) {
                ItemStack s = inventory.getItem(i);
                if (s != null && s.isSimilar(stack)) {
                    int take = Math.min(toExtract, s.getAmount());
                    s.setAmount(s.getAmount() - take);
                    if (s.getAmount() <= 0) inventory.setItem(i, null);
                    extracted += take;
                    toExtract -= take;
                }
            }

            // Register rollback
            tx.addRollback(() -> snapshot.forEach((i, s) -> inventory.setItem(i, s)));

            if (extracted > 0) tx.commit();
            return extracted;
        }
    }

    /**
     * Extract any available ItemStack up to maxAmount.
     */
    public ItemStack tryExtractAny(long maxAmount, TransactionContext ctx) {
        try (Transaction tx = ctx.openTransaction()) {
            Map<Integer, ItemStack> snapshot = HashMap.ofEntries(
                List.of(inventory.getContents())
                    .zipWithIndex()
                    .filter(tuple -> tuple._1 != null)
                    .map(tuple -> io.vavr.Tuple.of(tuple._2, tuple._1.clone()))
            );

            ItemStack extracted = null;

            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack s = inventory.getItem(i);
                if (s != null) {
                    int take = (int) Math.min(maxAmount, s.getAmount());
                    extracted = s.clone();
                    extracted.setAmount(take);

                    s.setAmount(s.getAmount() - take);
                    if (s.getAmount() <= 0) inventory.setItem(i, null);

                    break;
                }
            }

            tx.addRollback(() -> snapshot.forEach((i, s) -> inventory.setItem(i, s)));
            if (extracted != null) tx.commit();
            return extracted;
        }
    }

    /**
     * Peek at any available ItemStack without removing it.
     */
    public ItemStack peekAny() {
        for (ItemStack s : inventory.getContents()) {
            if (s != null) return s.clone();
        }
        return null;
    }

    @Override
    public List<ItemStack> getResources() {
        return List.of(inventory.getContents()).filter(s -> s != null);
    }
}