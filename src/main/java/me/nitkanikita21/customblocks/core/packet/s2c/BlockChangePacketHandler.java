package me.nitkanikita21.customblocks.core.packet.s2c;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import io.vavr.control.Option;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.packet.AbstractPacketHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.joml.Vector3i;

import java.util.function.Function;

public class BlockChangePacketHandler extends AbstractPacketHandler<PacketSendEvent, WrapperPlayServerBlockChange> {
    final ServerBlockManager manager;
    final BukkitTaskScheduler scheduler;

    public BlockChangePacketHandler(ServerBlockManager manager, BukkitTaskScheduler scheduler) {
        super(PacketType.Play.Server.BLOCK_CHANGE, WrapperPlayServerBlockChange::new);
        this.manager = manager;
        this.scheduler = scheduler;
    }

    @Override
    public void handle(PacketSendEvent event, WrapperPlayServerBlockChange wrapper) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        WorldAccessor accessor = manager.getAccessor(world);
        Vector3i pos = new Vector3i(wrapper.getBlockPosition().x, wrapper.getBlockPosition().y, wrapper.getBlockPosition().z);

//            BlockState blockState = manager.getManager(world).getBlockState(pos);
        Option<BlockState> blockStateOption = manager.getManager(world).tryGetBlockState(pos);
        if (blockStateOption.isEmpty()) return;
        BlockState blockState = blockStateOption.get();
        Block block = blockState.getOwner();

        WrappedBlockState clientBlock = block.getClientBlock(blockState, accessor, pos, player);

        wrapper.setBlockState(clientBlock);

//        scheduler.runTask(() -> block.onPlace(
//            blockState,
//            accessor,
//            pos,
//            player
//        ));

        event.setByteBuf(wrapper.getBuffer());
    }
}
