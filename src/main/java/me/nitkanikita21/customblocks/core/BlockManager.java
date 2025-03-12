package me.nitkanikita21.customblocks.core;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityProvider;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityTicker;
import me.nitkanikita21.customblocks.core.blockentity.BlockTickingRunnable;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.World;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class BlockManager {
    final World world;
    final BukkitTaskScheduler scheduler;
    Map<Vector3i, BlockState> blockStates = HashMap.empty();
    Map<Vector3i, BlockEntity> blockEntities = HashMap.empty();
    Map<Vector3i, BukkitTask> tickers = HashMap.empty();


    public BlockState initializeBlockState(Vector3i pos, @NotNull Block block) {
        BlockState defaultState = block.getDefaultState();
        blockStates = blockStates.put(pos, defaultState);
        return defaultState;
    }

    public void setBlockState(Vector3i pos, BlockState state) {
        blockStates = blockStates.put(pos, state);

        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(new Location(world, pos.x, pos.y, pos.z)) < 64 * 64) { // 64 - це радіус завантаження чанків
                state.getOwner().sendBlockPacket(
                    player.getPlayer(),
                    accessor(),
                    pos
                );
            }
        }
    }

    public BlockState getBlockState(Vector3i pos) {
        return blockStates.get(pos)
            .getOrElseThrow(() -> new RuntimeException(
                String.format("BlockState not found for position %s in world %s", pos, world)
            ));
    }

    public Option<BlockEntity> getBlockEntity(Vector3i pos) {
        return blockEntities.get(pos);
    }

    public void placeBlock(Vector3i pos, @NotNull Block block, @Nullable Player player) {
        placeBlock(pos, block, block.getDefaultState(), player);
    }

    public void placeBlock(Vector3i pos, @NotNull Block block) {
        placeBlock(pos, block, block.getDefaultState(), null);
    }

    public void placeBlock(Vector3i pos, @NotNull Block block, @NotNull BlockState state) {
        placeBlock(pos, block, state, null);
    }

    public void placeBlock(Vector3i pos, @NotNull Block block, @NotNull BlockState state, @Nullable Player player) {
        blockStates = blockStates.put(pos, state);
        org.bukkit.block.Block bukkitBlock = world.getBlockAt(new Location(world, pos.x, pos.y, pos.z));
        bukkitBlock.setType(Material.NOTE_BLOCK);
        NoteBlock blockData = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
        bukkitBlock.setBlockData(blockData);
        Note note = new Note(1);

        blockData.setNote(note);

        if(player != null) {
            player.sendBlockChange(bukkitBlock.getLocation(), blockData);
        }

        if(block instanceof BlockEntityProvider provider && !tickers.containsKey(pos)) {
            tickers = tickers.put(
                pos,
                scheduler.runTaskTimer(
                    new BlockTickingRunnable<>(pos, accessor(), provider),
                    0, 0
                )
            );
            blockEntities = blockEntities.put(
                pos,
                ((BlockEntityProvider) block).createBlockEntity(
                    accessor(),
                    pos,
                    state
                )
            );
        }
    }

    private WorldAccessor accessor() {
        return new WorldAccessor(world, this);
    }

}
