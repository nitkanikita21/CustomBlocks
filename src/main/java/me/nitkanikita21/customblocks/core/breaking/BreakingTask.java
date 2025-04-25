package me.nitkanikita21.customblocks.core.breaking;

import lombok.RequiredArgsConstructor;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.util.TriConsumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.joml.Vector3i;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class BreakingTask implements Runnable {
    private final Player player;
    private final BlockProgress sharedProgress;
    private final BukkitTaskScheduler scheduler;

    private final TriConsumer<Vector3i, World, Byte> showAnimation;
    private final BiConsumer<Vector3i, World> removeAnimation;
    private final BiConsumer<Vector3i, Player> completeBreak;

    private BukkitTask taskId;
    private boolean cancelled = false;

    public void start() {
        taskId = scheduler.runTaskTimer(this, 0, 2); // кожні 2 тики
    }

    public void cancel() {
        if (cancelled) return;
        cancelled = true;

        scheduler.cancelTask(taskId);
        removeAnimation.accept(sharedProgress.getPos(), sharedProgress.getWorld());
        BlockBreakSpeedUtil.resetBlockBreakSpeed(player);
    }

    @Override
    public void run() {
        if (cancelled || player.getGameMode() == GameMode.CREATIVE || !isStillTargeting()) {
            cancel();
            return;
        }

        double damagePerTick = 0.2; // або зроби формулу
        sharedProgress.addDamage(player, damagePerTick);

        int crackStage = (int) (sharedProgress.getProgress() * 10);
        showAnimation.accept(sharedProgress.getPos(), sharedProgress.getWorld(), (byte) Math.min(crackStage, 9));

        int percent = (int) (sharedProgress.getProgress() * 100);
        player.sendActionBar(Component.text("Progress: " + Math.min(percent, 100) + "%", NamedTextColor.YELLOW));

        if (sharedProgress.isComplete()) {
            completeBreak.accept(sharedProgress.getPos(), player); // останній хто дійшов
            cancel();
        }
    }

    private boolean isStillTargeting() {
        var block = player.getTargetBlockExact(6);
        return block != null &&
               block.getLocation().toVector().toVector3i().equals(sharedProgress.getPos());
    }
}
