package me.nitkanikita21.customblocks.core.blockstate;

import io.vavr.collection.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import me.nitkanikita21.customblocks.core.blockstate.property.EnumStateProperty;
import org.bukkit.Axis;
import org.bukkit.block.BlockFace;

@UtilityClass
public class DefaultStateProperties {
    public final EnumStateProperty<BlockFace> FACING = new EnumStateProperty<>("facing", BlockFace.class);
    public final EnumStateProperty<Axis> AXIS = new EnumStateProperty<>("axis", Axis.class);
    public final EnumStateProperty<BlockFace> HORIZONTAL_FACING = new EnumStateProperty<>(
        "axis", BlockFace.class,
        List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)
    );
}
