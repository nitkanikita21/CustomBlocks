package me.nitkanikita21.customblocks.core.breaking;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.joml.Vector3i;

import java.util.UUID;

public class BreakingManager {
    private Map<Vector3i, BlockProgress> blockProgressMap = HashMap.empty();
    private Map<Tuple2<Vector3i, UUID>, BreakingTask> taskMap = HashMap.empty();

    public void startBreaking(Vector3i pos, Player player, double hardness, World world,
                              BreakingTask task) {
        // Створити або використати існуючий прогрес
        blockProgressMap = blockProgressMap.put(pos, blockProgressMap
            .get(pos)
            .getOrElse(() -> new BlockProgress(pos, hardness, world)));

        taskMap = taskMap.put(new Tuple2<>(pos, player.getUniqueId()), task);
        task.start();
    }

    public void stopBreaking(Vector3i pos, Player player) {
        Tuple2<Vector3i, UUID> key = new Tuple2<>(pos, player.getUniqueId());
        Option<BreakingTask> taskOpt = taskMap.get(key);
        taskOpt.peek(BreakingTask::cancel);
        taskMap = taskMap.remove(key);

        Option<BlockProgress> progressOpt = blockProgressMap.get(pos);
        progressOpt.peek(progress -> {
            progress.getParticipants().remove(player.getUniqueId());
            if (progress.getParticipants().isEmpty()) {
                blockProgressMap = blockProgressMap.remove(pos);
            }
        });
    }

    public boolean isBreaking(Vector3i pos, Player player) {
        return taskMap.containsKey(new Tuple2<>(pos, player.getUniqueId()));
    }

    public BlockProgress getOrCreateProgress(Vector3i pos, double hardness, World world) {
        return blockProgressMap.get(pos).getOrElse(() -> {
            BlockProgress progress = new BlockProgress(pos, hardness, world);
            blockProgressMap = blockProgressMap.put(pos, progress);
            return progress;
        });
    }
}