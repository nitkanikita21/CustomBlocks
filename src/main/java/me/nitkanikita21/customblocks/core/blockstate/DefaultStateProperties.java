package me.nitkanikita21.customblocks.core.blockstate;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import me.nitkanikita21.customblocks.core.blockstate.property.EnumStateProperty;
import org.bukkit.block.BlockFace;

@UtilityClass
@FieldDefaults(level = AccessLevel.PUBLIC)
public class DefaultStateProperties {
    EnumStateProperty<BlockFace> DIRECTION = new EnumStateProperty<>("Direction", BlockFace.class);
}
