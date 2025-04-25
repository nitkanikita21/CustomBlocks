package me.nitkanikita21.customblocks.examples;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.Getter;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntity;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.registry.BlockEntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.joml.Vector3i;

public class EnderChestBlockEntity extends BlockEntity {
    private static Map<Material, Inventory> channelsInventory = HashMap.empty();
    @Getter
    private Material channel = Material.STONE;

    public EnderChestBlockEntity(Vector3i pos, BlockState state) {
        super(BlockEntityTypes.ENDER_CHEST, pos, state);
    }

    public static void tick(WorldAccessor world, Vector3i pos, BlockState state, EnderChestBlockEntity blockEntity) {
        world.getManager().setBlockState(
            pos, state.setProperty(
                EnderChestBlock.IS_OPEN,
                channelsInventory.get(blockEntity.channel)
                    .map(i -> !i.getViewers().isEmpty())
                    .getOrElse(false)
            )
        );
    }

    public void setChannel(Material channel) {
        channelsInventory.get(this.channel).peek(i -> {
            List<HumanEntity> viewers = List.ofAll(i.getViewers());
            this.channel = channel;
            viewers.forEach(this::openInventory);
        });
    }

    public void openInventory(HumanEntity player) {
        Tuple2<Inventory, ? extends Map<Material, Inventory>> t = channelsInventory.computeIfAbsent(channel, (k) -> Bukkit.createInventory(null, InventoryType.DISPENSER));
        channelsInventory = t._2;
        player.openInventory(t._1);
    }

}
