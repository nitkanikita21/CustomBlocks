package me.nitkanikita21.customblocks.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nitkanikita21.customblocks.core.breaking.BreakingManager;
import org.bukkit.World;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorldAccessor {
    final World world;
    final BlockManager manager;
    public BreakingManager getBreakingManager() {
        return manager.getBlockBreakingManager();
    }
}
