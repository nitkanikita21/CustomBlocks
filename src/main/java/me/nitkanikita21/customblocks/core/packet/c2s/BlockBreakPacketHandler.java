package me.nitkanikita21.customblocks.core.packet.c2s;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation;
import io.vavr.control.Option;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.BlockManager;
import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.breaking.BlockBreakSpeedUtil;
import me.nitkanikita21.customblocks.core.breaking.BlockProgress;
import me.nitkanikita21.customblocks.core.breaking.BreakingManager;
import me.nitkanikita21.customblocks.core.breaking.BreakingTask;
import me.nitkanikita21.customblocks.core.packet.AbstractPacketHandler;
import me.nitkanikita21.customblocks.core.util.Vector3iUtils;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.joml.Vector3i;

public class BlockBreakPacketHandler extends AbstractPacketHandler<PacketReceiveEvent, WrapperPlayClientPlayerDigging> {
    private final ServerBlockManager serverBlockManager;
    private final BukkitTaskScheduler scheduler;

    public BlockBreakPacketHandler(ServerBlockManager serverBlockManager, BukkitTaskScheduler scheduler) {
        super(PacketType.Play.Client.PLAYER_DIGGING, WrapperPlayClientPlayerDigging::new);
        this.serverBlockManager = serverBlockManager;
        this.scheduler = scheduler;
    }

    @Override
    public void handle(PacketReceiveEvent event, WrapperPlayClientPlayerDigging wrapper) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Vector3i blockPos = new Vector3i(
            wrapper.getBlockPosition().x,
            wrapper.getBlockPosition().y,
            wrapper.getBlockPosition().z
        );

        BlockManager manager = serverBlockManager.getManager(world);
        BreakingManager breakingManager = manager.getBlockBreakingManager();
        WorldAccessor accessor = manager.toAccessor();

        DiggingAction action = wrapper.getAction();

        if (!manager.isCustomBlock(blockPos)) return;
        Option<BlockState> optState = manager.tryGetBlockState(blockPos);
        if (optState.isEmpty()) return;
        BlockState blockState = optState.get();

        switch (action) {
            case START_DIGGING -> {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    completeBreak(blockPos, player);
                    return;
                }

                if (!breakingManager.isBreaking(blockPos, player)) {
                    double hardness = blockState.getOwner().getProperties().getHardness();
                    BlockProgress progress = breakingManager.getOrCreateProgress(blockPos, hardness, world);

                    BreakingTask task = new BreakingTask(
                        player,
                        progress,
                        scheduler,
                        this::showBreakAnimation,
                        this::removeBreakAnimation,
                        this::completeBreak
                    );

                    breakingManager.startBreaking(blockPos, player, hardness, world, task);
                    BlockBreakSpeedUtil.setBlockBreakSpeed(player, -1);
                }
            }

            case CANCELLED_DIGGING -> {
                breakingManager.stopBreaking(blockPos, player);
            }

            // FINISHED_DIGGING — більше не обробляємо, бо прогрес іде через BreakingTask
        }
    }

    private void showBreakAnimation(Vector3i blockPos, World world, byte crackStage) {
        WrapperPlayServerBlockBreakAnimation packet = new WrapperPlayServerBlockBreakAnimation(
            blockPos.hashCode(),
            new com.github.retrooper.packetevents.util.Vector3i(
                blockPos.x, blockPos.y, blockPos.z
            ),
            crackStage
        );

        scheduler.runTask(() -> {
            for (Player nearby : world.getNearbyPlayers(Vector3iUtils.toLocation(world, blockPos), 32)) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(nearby, packet);
            }
        });
    }

    private void removeBreakAnimation(Vector3i blockPos, World world) {
        showBreakAnimation(blockPos, world, (byte)-1);
    }

    private void completeBreak(Vector3i blockPos, Player player) {
        BlockManager manager = serverBlockManager.getManager(player.getWorld());
        BreakingManager breakingManager = manager.getBlockBreakingManager();

        scheduler.runTask(() -> {
            breakingManager.stopBreaking(blockPos, player);
            removeBreakAnimation(blockPos, player.getWorld());

            Option<BlockState> blockStateOption = manager.tryGetBlockState(blockPos);
            if (blockStateOption.isEmpty()) return;

            blockStateOption.peek(blockState -> {
                BlockBreakSpeedUtil.resetBlockBreakSpeed(player);

                boolean allowBreak = blockState.getOwner().onBreak(
                    blockState,
                    manager.toAccessor(),
                    blockPos,
                    player,
                    org.bukkit.event.block.Action.LEFT_CLICK_BLOCK,
                    org.bukkit.block.BlockFace.UP // або з PacketFace
                );

                if (allowBreak) {
                    manager.destroyBlock(blockPos);
                }
            });
        });


    }
}
