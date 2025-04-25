package me.nitkanikita21.customblocks.core;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import me.nitkanikita21.customblocks.common.EventRegister;
import me.nitkanikita21.customblocks.common.scheduler.BukkitTaskScheduler;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityProvider;
import me.nitkanikita21.customblocks.core.blockentity.BlockTickingRunnable;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.breaking.BreakingManager;
import me.nitkanikita21.customblocks.core.listener.BlockEventsListener;
import me.nitkanikita21.customblocks.core.snapshot.WorldSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.stream.IntStream;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class BlockManager {
    final World world;
    final BukkitTaskScheduler scheduler;
    final EventRegister eventRegister;
    Map<Vector3i, BlockState> blockStates = HashMap.empty();
    Map<Vector3i, BlockEntity> blockEntities = HashMap.empty();
    Map<Vector3i, BukkitTask> tickers = HashMap.empty();
    @Getter
    BreakingManager blockBreakingManager = new BreakingManager();

    public BlockManager initialize() {
        eventRegister.register(new BlockEventsListener(toAccessor()));
        return this;
    }

    public WorldSnapshot getSnapshot() {
        return new WorldSnapshot(blockStates, blockEntities);
    }

    public void applySnapshot(WorldSnapshot snapshot) {
        blockStates = snapshot.getBlockStates();
        blockEntities = snapshot.getBlockEntities();

        blockStates.forEach((pos, state) -> {
            if (state.getOwner() instanceof BlockEntityProvider provider) {
                tickers = tickers.put(
                    pos,
                    scheduler.runTaskTimer(
                        new BlockTickingRunnable<>(pos, toAccessor(), provider),
                        0, 0
                    )
                );
            }
        });
    }

    public BlockState initializeBlockState(Vector3i pos, @NotNull Block block) {
        BlockState defaultState = block.getDefaultState();
        blockStates = blockStates.put(pos, defaultState);

        Chest b = null;

        return defaultState;
    }

    public void setBlockState(Vector3i pos, BlockState state) {
        blockStates = blockStates.put(pos, state);

        updateBlockForPlayers(pos, state);
    }

    public void updateBlockForPlayers(Vector3i pos, BlockState state) {
        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(new Location(world, pos.x, pos.y, pos.z)) < 64 * 64) {
                state.getOwner().sendBlockPacket(
                    player.getPlayer(),
                    toAccessor(),
                    pos
                );
            }
        }
    }
    public void updateBlockForPlayers(Vector3i pos) {
        tryGetBlockState(pos).peek(bs -> updateBlockForPlayers(pos, bs));
    }
    public void updateBlocksForPlayersAround(Vector3i pos) {
        IntStream.rangeClosed(-1, 1).forEach(dx ->
            IntStream.rangeClosed(-1, 1).forEach(dy ->
                IntStream.rangeClosed(-1, 1).forEach(dz -> {
                    Vector3i newPos = pos.add(dx, dy, dz);
                    tryGetBlockState(newPos).peek(bs ->
                        updateBlockForPlayers(newPos, bs)
                    );
                })
            )
        );
    }

    public BlockState getBlockState(Vector3i pos) {
        return blockStates.get(pos)
            .getOrElseThrow(() -> new IllegalStateException(
                String.format("BlockState not found for position %s in world %s", pos, world)
            ));
    }

    public void destroyBlock(Vector3i pos) {
        tryGetBlockState(pos).peek(b -> {
            blockStates = blockStates.remove(pos);
            blockEntities = blockEntities.remove(pos);
//             tickers = tickers.remove(pos);
            tickers.get(pos).peek(BukkitTask::cancel);
            tickers = tickers.remove(pos);

            world.setType(pos.x, pos.y, pos.z, Material.AIR);
        });
    }

    public Option<BlockState> tryGetBlockState(Vector3i pos) {
        return blockStates.get(pos);
    }

    public Option<BlockEntity> getBlockEntity(Vector3i pos) {
        return blockEntities.get(pos);
    }

    public void placeBlock(Vector3i pos, @NotNull Block block, @Nullable Player player,
                           @Nullable Action action,
                           @Nullable BlockFace face
    ) {
        placeBlock(pos, block, block.getDefaultState(), player, action, face);
    }

    public void placeBlock(Vector3i pos, @NotNull Block block) {
        placeBlock(pos, block, block.getDefaultState(), null, null, null);
    }

    public void placeBlock(Vector3i pos, @NotNull Block block, @NotNull BlockState state) {
        placeBlock(pos, block, state, null, null, null);
    }

    public void placeBlock(
        Vector3i pos,
        @NotNull Block block,
        @NotNull BlockState state,
        @Nullable Player player,
        @Nullable Action action,
        @Nullable BlockFace face
    ) {
        blockStates = blockStates.put(pos, state);
        org.bukkit.block.Block bukkitBlock = world.getBlockAt(new Location(world, pos.x, pos.y, pos.z));
        bukkitBlock.setType(Material.STONE);

        updateBlocksForPlayersAround(pos);

        if (block instanceof BlockEntityProvider provider && !tickers.containsKey(pos)) {
            tickers = tickers.put(
                pos,
                scheduler.runTaskTimer(
                    new BlockTickingRunnable<>(pos, toAccessor(), provider),
                    0, 0
                )
            );
            blockEntities = blockEntities.put(
                pos,
                ((BlockEntityProvider) block).createBlockEntity(
                    pos,
                    state
                )
            );
        }
        if(player != null && action != null && face != null) {
            block.onPlace(state, toAccessor(), pos, player, action, face);
        } else {
            block.onPlace(state, toAccessor(), pos);
        }
    }

    public WorldAccessor toAccessor() {
        return new WorldAccessor(world, this);
    }

    public boolean isCustomBlock(Vector3i pos) {
        return blockStates.containsKey(pos);
    }

}
