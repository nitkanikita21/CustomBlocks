package me.nitkanikita21.customblocks.examples.factoryblock;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.block.ActionResult;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.block.BlockProperties;
import me.nitkanikita21.customblocks.core.block.BlockWithEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityType;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.registry.BlockEntityTypes;
import me.nitkanikita21.customblocks.util.PlayerProfileUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.UUID;

public class StorageBlock extends BlockWithEntity {

    private static final String MISSING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M2MDNjNzk1NjAzMTk5OTZkNjM5NDEyOGI0OWZlYzc2NTBjZjg2N2ExZTQ4ZmI4MGM2MDQzZTc3MGRkNzFiZCJ9fX0=";
    private static UUID STATIC_UUID;

    public StorageBlock() {
        super(
            BlockProperties.builder()
                .name(
                    MiniMessage.miniMessage()
                        .deserialize("<i:false><dark_purple>Storage block")
                )
                .build()
        );
    }

    @Override
    public WrappedBlockState getClientBlock(BlockState state, WorldAccessor world, Vector3i pos) {
        return SpigotConversionUtil.fromBukkitBlockData(Material.NOTE_BLOCK.createBlockData());
    }

    @Override
    public ActionResult onInteract(BlockState state, WorldAccessor world, Vector3i pos, Player player, Action action, BlockFace face) {

        if (player.getGameMode() == GameMode.CREATIVE || action == Action.LEFT_CLICK_BLOCK) {
            return ActionResult.PASS;
        }

        return world.getManager().<StorageBlockEntity>getBlockEntityCast(pos)
            .map(be -> {
                be.openInventory(player);
                return ActionResult.SUCCESS;
            }).getOrElse(ActionResult.PASS);
    }

    @Override
    protected ItemStack buildItemStack() {
        ItemStack head = new ItemStack(Material.BUCKET);


        return head;
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntityTypes.STORAGE;
    }
}
