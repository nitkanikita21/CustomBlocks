package me.nitkanikita21.customblocks.core.block;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import de.tr7zw.nbtapi.NBT;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.math.BlockPosition;
import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.registry.Blocks;
import me.nitkanikita21.customblocks.core.registry.Registries;
import me.nitkanikita21.registry.Identifier;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class Block {
    public static String BLOCK_ITEM_TAG = "BlockItem";
    protected final BlockProperties properties;

    public @NotNull BlockState getDefaultState() {
        return new BlockState(this, HashMap.empty());
    }

    public void onPlace(BlockState state, WorldAccessor world, Vector3i pos, Player player) {
        onPlace(state, world, pos);
    }
    public void onPlace(BlockState state, WorldAccessor world, Vector3i pos) {}

    public boolean onBreak(BlockState state, WorldAccessor world, Vector3i pos, Player player) {return true;}
    public void onRemove(BlockState state, WorldAccessor world, Vector3i pos) {}

    public void onInteract(
        BlockState state,
        WorldAccessor world,
        Vector3i pos,
        Player player,
        Action action,
        BlockFace face
    ) {}

    public ItemStack getItemStack(Player player) {
        return getItemStack();
    }

    public WrappedBlockState getClientBlock(BlockState state, WorldAccessor world, Vector3i pos) {
        return StateTypes.STONE.createBlockState();
    }

    public WrappedBlockState getClientBlock(BlockState state, WorldAccessor world, Vector3i pos, Player player) {
        return getClientBlock(state, world, pos);
    }

    protected ItemStack buildItemStack() {
        ItemStack itemStack = new ItemStack(Material.NOTE_BLOCK);
        itemStack.editMeta(meta -> {
            meta.displayName(properties.getName());
        });
        return itemStack;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = buildItemStack();
        NBT.modify(itemStack, nbt -> {
            nbt.setString(
                BLOCK_ITEM_TAG,
                Registries.getIdentifier(Registries.BLOCKS, this)
                    .getOrElseThrow(() -> new RuntimeException("Cannot find id for this block"))
                    .toString()
            );
        });
        return itemStack;
    }

    public void sendBlockPacket(Player player, WorldAccessor worldAccessor, Vector3i pos) {
        WrappedBlockState clientBlock = getClientBlock(worldAccessor.getManager().getBlockState(pos), worldAccessor, pos, player);

        ClientVersion clientVersion = PacketEvents.getAPI().getPlayerManager().getClientVersion(player);

        WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(
            new com.github.retrooper.packetevents.util.Vector3i(
                pos.x,
                pos.y,
                pos.z
            ),
            clientBlock.getType().getMapped().getId(clientVersion)
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
    public Identifier getIdentifier() {
        return Registries.getIdentifier(Registries.BLOCKS, this).get();
    }
}
