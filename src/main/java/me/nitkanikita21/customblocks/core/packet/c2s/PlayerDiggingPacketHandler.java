package me.nitkanikita21.customblocks.core.packet.c2s;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import io.vavr.control.Option;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.packet.AbstractPacketHandler;
import me.nitkanikita21.customblocks.core.packet.PacketUtils;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.joml.Vector3i;

@Deprecated
public class PlayerDiggingPacketHandler extends AbstractPacketHandler<PacketReceiveEvent, WrapperPlayClientPlayerDigging> {
    final ServerBlockManager manager;
    final BukkitTaskScheduler scheduler;

    public PlayerDiggingPacketHandler(ServerBlockManager manager, BukkitTaskScheduler scheduler) {
        super(PacketType.Play.Client.PLAYER_DIGGING, WrapperPlayClientPlayerDigging::new);
        this.manager = manager;
        this.scheduler = scheduler;
    }

    @Override
    public void handle(PacketReceiveEvent event, WrapperPlayClientPlayerDigging wrapper) {
        Player player = event.getPlayer();

        if (wrapper.getAction() != DiggingAction.FINISHED_DIGGING && !(wrapper.getAction() == DiggingAction.START_DIGGING && player.getGameMode() == GameMode.CREATIVE))
            return;

        var vector3iPE = wrapper.getBlockPosition().toVector3d().toVector3i();
        Vector3i pos = new Vector3i(vector3iPE.x, vector3iPE.y, vector3iPE.z);
        World world = player.getWorld();
        WorldAccessor accessor = manager.getAccessor(world);

        Option<BlockState> blockStateOption = accessor.getManager().tryGetBlockState(pos);

        if (blockStateOption.isEmpty()) return;

        blockStateOption.peek(blockState -> {

            scheduler.runTask(() -> {

                boolean allowBreak = blockState.getOwner().onBreak(
                    blockState,
                    accessor,
                    pos,
                    player,
                    Action.LEFT_CLICK_BLOCK,
                    PacketUtils.toBukkitFace(wrapper.getBlockFace())
                );

                if (allowBreak) {
                    accessor.getManager().destroyBlock(pos);
                }
            });

        });
    }
}
