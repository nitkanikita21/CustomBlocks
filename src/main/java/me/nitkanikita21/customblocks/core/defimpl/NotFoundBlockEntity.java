package me.nitkanikita21.customblocks.core.defimpl;

import io.papermc.paper.math.BlockPosition;
import me.nitkanikita21.customblocks.core.Vec3iUtils;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntity;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.registry.BlockEntityTypes;
import org.bukkit.Location;
import org.joml.Vector3i;

import static me.nitkanikita21.customblocks.core.defimpl.NotFoundBlock.FLIPPED;

public class NotFoundBlockEntity extends BlockEntity {

    private long tick = 0;

    public NotFoundBlockEntity(Vector3i pos, BlockState state) {
        super(BlockEntityTypes.NOT_FOUND, pos, state);
    }


    public static void tick(WorldAccessor world, Vector3i pos, BlockState state, NotFoundBlockEntity blockEntity) {
//        state.setProperty(FLIPPED, !state.getProperty(FLIPPED).getOrElse(false));

//        if(blockEntity.tick == 0) {
//            world.getManager().setBlockState(
//                pos,
//                state.setProperty(FLIPPED, !state.getProperty(FLIPPED).getOrElse(false))
//            );
//        }
//        if(blockEntity.tick % 10 == 0) {
//            world.getManager().setBlockState(
//                pos,
//                state.setProperty(FLIPPED, !state.getProperty(FLIPPED).getOrElse(false))
//            );
//        }

        boolean isNear = world.getWorld().getPlayers()
            .stream().anyMatch(
                (p) -> p.getLocation().distanceSquared(
                    Vec3iUtils.toLocation(world.getWorld(), pos)
                ) <= 15
            );


        Boolean isFlipped = state.getProperty(FLIPPED).getOrElse(false);

        if(isNear) {
            world.getManager().setBlockState(pos, state.setProperty(FLIPPED, true));
        } else {
            world.getManager().setBlockState(pos, state.setProperty(FLIPPED, false));
        }
        blockEntity.tick++;
    }
}