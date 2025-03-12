package me.nitkanikita21.customblocks.core.packet;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.math.BlockPosition;
import lombok.RequiredArgsConstructor;
import me.nitkanikita21.customblocks.core.ItemBlockListener;
import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;

import java.lang.reflect.Field;

@RequiredArgsConstructor
public class BlockPacketListener implements PacketListener {
    private final ServerBlockManager manager;

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            onBlockChange(event, new WrapperPlayServerBlockChange(event));
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {

        }
    }

    private void onBlockPlace(PacketReceiveEvent event, WrapperPlayClientPlayerBlockPlacement wrapper) {
        ItemStack bukkitItemStack = SpigotConversionUtil.toBukkitItemStack(wrapper.getItemStack().get());

        if(!ItemBlockListener.isBlockItem(bukkitItemStack))return;

        Player player = event.getPlayer();
        World world = player.getWorld();
        WorldAccessor accessor = manager.getAccessor(world);
        Vector3i pos = new Vector3i(wrapper.getBlockPosition().x, wrapper.getBlockPosition().y, wrapper.getBlockPosition().z);
        BlockPosition blockPos = BlockPosition.BLOCK_ZERO
            .offset(pos.x, pos.y, pos.z);

        accessor.getManager()
            .placeBlock(pos, ItemBlockListener.getBlock(bukkitItemStack));

    }

    private void onBlockChange(PacketSendEvent event, WrapperPlayServerBlockChange wrapper) {
        BlockData bukkitBlockData = SpigotConversionUtil.toBukkitBlockData(wrapper.getBlockState());
        if (bukkitBlockData.getMaterial() == Material.NOTE_BLOCK) {
            NoteBlock data = (NoteBlock) bukkitBlockData;
            Note note = data.getNote();
            byte noteByte = 0;
            try {
                Field f = Note.class.getDeclaredField("note");
                f.setAccessible(true);
                noteByte = (byte) f.get(note);
                f.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            Player player = event.getPlayer();
            World world = player.getWorld();
            WorldAccessor accessor = manager.getAccessor(world);
            Vector3i pos = new Vector3i(wrapper.getBlockPosition().x, wrapper.getBlockPosition().y, wrapper.getBlockPosition().z);

            BlockState blockState = manager.getManager(world).getBlockState(pos);
            Block block = blockState.getOwner();

            WrappedBlockState clientBlock = block.getClientBlock(
                blockState,
                accessor,
                pos,
                player
            );

            wrapper.setBlockState(clientBlock);

            block.onPlace(
                blockState,
                accessor,
                pos,
                player
            );

            event.setByteBuf(wrapper.getBuffer());
        }
    }
}
