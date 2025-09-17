package me.nitkanikita21.customblocks.examples.factoryblock;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.Getter;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockStorageProvider;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.registry.BlockEntityTypes;
import me.nitkanikita21.customblocks.core.transfer.InventoryStorage;
import me.nitkanikita21.customblocks.examples.EnderChestBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.joml.Vector3i;

public class StorageBlockEntity extends BlockEntity implements BlockStorageProvider<InventoryStorage> {
    private final InventoryStorage storage = new InventoryStorage(Bukkit.createInventory(null, 9));

    public StorageBlockEntity(Vector3i pos, BlockState state) {
        super(BlockEntityTypes.ENDER_CHEST, pos, state);
    }

    public static void tick(WorldAccessor world, Vector3i pos, BlockState state, StorageBlockEntity blockEntity) {

    }

    public void openInventory(HumanEntity player) {
        player.openInventory(storage.getInventory());
    }

    @Override
    public InventoryStorage getStorage() {
        return storage;
    }
}
