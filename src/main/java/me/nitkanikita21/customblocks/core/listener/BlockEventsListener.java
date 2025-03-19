package me.nitkanikita21.customblocks.core.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.joml.Vector3i;

@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlockEventsListener implements Listener {
    final WorldAccessor accessor;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if(action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK)return;
        var bukkitClickedBlock = event.getClickedBlock();
        Vector3i pos = bukkitClickedBlock.getLocation().toVector().toVector3i();
        Option<BlockState> blockStateOption = accessor.getManager().tryGetBlockState(pos);
        if(blockStateOption.isEmpty())return;

        event.setCancelled(true);
        BlockState blockState = blockStateOption.get();

        blockState.getOwner().onInteract(
            blockState,
            accessor,
            pos,
            event.getPlayer(),
            action,
            event.getBlockFace()
        );
    }


    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
       /* Vector3i pos = event.getBlock().getLocation().toVector().toVector3i();

        Option<BlockState> blockStateOption = accessor.getManager().tryGetBlockState(pos);
        Player player = event.getPlayer();

        if(blockStateOption.isEmpty() || player.getGameMode() != GameMode.CREATIVE)return;
        event.setCancelled(true);

        blockStateOption.peek(blockState -> {
            boolean allowBreak = blockState.getOwner().onBreak(blockState, accessor, pos, player);

            accessor.getManager().destroyBlock(pos);
        });*/
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDestroy(BlockDestroyEvent event) {
        Vector3i pos = event.getBlock().getLocation().toVector().toVector3i();
        Option<BlockState> blockStateOption = accessor.getManager().tryGetBlockState(pos);
        if(blockStateOption.isEmpty())return;

        event.setCancelled(true);
        BlockState blockState = blockStateOption.get();

        blockState.getOwner().onRemove(
            blockState,
            accessor,
            pos
        );
    }


}
