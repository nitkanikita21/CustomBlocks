package me.nitkanikita21.customblocks.core.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.BlockManager;
import me.nitkanikita21.customblocks.core.ServerBlockManager;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.breaking.BlockBreakSpeedUtil;
import me.nitkanikita21.customblocks.core.breaking.BreakingManager;
import me.nitkanikita21.customblocks.core.packet.PacketUtils;
import me.nitkanikita21.customblocks.core.util.Vector3iUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.joml.Vector3i;

@Deprecated
@RequiredArgsConstructor
public class BlockBreakProgressListener implements Listener {
    final ServerBlockManager serverBlockManager;
    final BukkitTaskScheduler scheduler;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        /*Player player = event.getPlayer();
        BlockManager manager = serverBlockManager.getManager(event.getPlayer().getWorld());
        BreakingManager blockBreakingManager = manager.getBlockBreakingManager();
        Option<org.bukkit.block.Block> targetBlock = Option.of(player.getTargetBlockExact(6));


        Option<Vector3i> optionBlockPos = targetBlock
            .map(b -> b.getLocation().toVector().toVector3i());
        if (
            targetBlock.isEmpty()
        ) return;

        Vector3i blockPos = optionBlockPos.get();
        if (!manager.isCustomBlock(blockPos)) return;

        BlockState blockState = manager.getBlockState(blockPos);

        if(!blockBreakingManager.isBreaking(blockPos)){
            blockBreakingManager.startBreaking(blockPos, player);
            BlockBreakSpeedUtil.setBlockBreakSpeed(player, -1);
            return;
        }

        blockBreakingManager.getProgress(blockPos).peek(progress -> {

            if (!progress.getPlayer().equals(player)) return;

            double blockHardness = blockState.getOwner().getProperties().getHardness(); // приклад
            double damage = 1.0; // розрахунок шкоди

            blockBreakingManager.updateDamage(blockPos, damage);

            int crackStage = (int) ((progress.getDamage() / blockHardness) * 10);
            showBreakAnimation(blockPos, player.getWorld(), (byte) Math.min(crackStage, 9));

            if (progress.getDamage() >= blockHardness) {
                completeBreak(blockPos, player);
            }
        });*/
    }


    private void showBreakAnimation(Vector3i blockPos, World world, byte crackStage ) {
        WrapperPlayServerBlockBreakAnimation packet = new WrapperPlayServerBlockBreakAnimation(
            blockPos.hashCode(),
            new com.github.retrooper.packetevents.util.Vector3i(
                blockPos.x,
                blockPos.y,
                blockPos.z
            ),
            crackStage
        );

        for (Player nearby : world.getNearbyPlayers(Vector3iUtils.toLocation(world, blockPos), 32)) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(nearby, packet);
        }
    }

    private void removeBreakAnimation(Vector3i blockPos, World world) {
        showBreakAnimation(blockPos, world, (byte) -1);
    }

    private void completeBreak(Vector3i blockPos, Player player) {

        BlockManager manager = serverBlockManager.getManager(player.getWorld());
        BreakingManager breakingManager = manager.getBlockBreakingManager();

        breakingManager.stopBreaking(blockPos, player);
        removeBreakAnimation(blockPos, player.getWorld());

        Option<BlockState> blockStateOption = manager.tryGetBlockState(blockPos);

        if (blockStateOption.isEmpty()) return;

        blockStateOption.peek(blockState -> {

            scheduler.runTask(() -> {

                BlockBreakSpeedUtil.resetBlockBreakSpeed(player);

                boolean allowBreak = blockState.getOwner().onBreak(
                    blockState,
                    manager.toAccessor(),
                    blockPos,
                    player,
                    Action.LEFT_CLICK_BLOCK,
                    player.getTargetBlockFace(6)
                );

                if (allowBreak) {
                    manager.destroyBlock(blockPos);
                }
            });

        });
    }
}
