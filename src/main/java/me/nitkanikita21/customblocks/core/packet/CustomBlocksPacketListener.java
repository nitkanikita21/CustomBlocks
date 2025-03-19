package me.nitkanikita21.customblocks.core.packet;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.math.BlockPosition;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.listener.ItemBlockListener;
import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomBlocksPacketListener implements PacketListener {
    final ServerBlockManager manager;
    final BukkitTaskScheduler scheduler;


    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.BLOCK_CHANGE) {
            return;
        }
        onBlockChange(event, new WrapperPlayServerBlockChange(event));
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            onBlockBreak(event, new WrapperPlayClientPlayerDigging(event));
        }
    }

    private void onBlockBreak(PacketReceiveEvent event, WrapperPlayClientPlayerDigging wrapper) {
        Player player = event.getPlayer();

        if(
            wrapper.getAction() != DiggingAction.FINISHED_DIGGING &&
            !(wrapper.getAction() == DiggingAction.START_DIGGING && player.getGameMode() == GameMode.CREATIVE)
        )return;

        var vector3iPE = wrapper.getBlockPosition().toVector3d().toVector3i();
        Vector3i pos = new Vector3i(vector3iPE.x, vector3iPE.y, vector3iPE.z);
        World world = player.getWorld();
        WorldAccessor accessor = manager.getAccessor(world);

        Option<BlockState> blockStateOption = accessor.getManager().tryGetBlockState(pos);

        if(blockStateOption.isEmpty())return;

        blockStateOption.peek(blockState -> {
            boolean allowBreak = blockState.getOwner().onBreak(blockState, accessor, pos, player);

            if(allowBreak) {
                scheduler.runTask(() -> accessor.getManager().destroyBlock(pos));
            }
        });
    }

    private void onBlockChange(PacketSendEvent event, WrapperPlayServerBlockChange wrapper) {
        BlockData bukkitBlockData = SpigotConversionUtil.toBukkitBlockData(wrapper.getBlockState());
        if (bukkitBlockData.getMaterial() != Material.NOTE_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        World world = player.getWorld();
        WorldAccessor accessor = manager.getAccessor(world);
        Vector3i pos = new Vector3i(wrapper.getBlockPosition().x, wrapper.getBlockPosition().y, wrapper.getBlockPosition().z);

//            BlockState blockState = manager.getManager(world).getBlockState(pos);
        Option<BlockState> blockStateOption = manager.getManager(world).tryGetBlockState(pos);
        if(blockStateOption.isEmpty())return;
        BlockState blockState = blockStateOption.get();
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
