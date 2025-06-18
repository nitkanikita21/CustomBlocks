package me.nitkanikita21.customblocks.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.UUID;

@UtilityClass
public class PlayerProfileUtils {
    private HashMap<UUID, PlayerProfile> MAP = HashMap.empty();

    public static PlayerProfile getOrCreateProfile(UUID staticUUID) {
        Tuple2<PlayerProfile, HashMap<UUID, PlayerProfile>> t = MAP.computeIfAbsent(staticUUID, Bukkit::createProfile);
        MAP = t._2;
        return t._1;
    }
}
