package me.nitkanikita21.customblocks.common;

import lombok.experimental.UtilityClass;
import me.nitkanikita21.registry.Identifier;
import net.kyori.adventure.key.Key;

@UtilityClass
public class IdUtils {
    Key toKey(Identifier id) {
        return Key.key(id.getNamespace(), id.getPath());
    }
    Identifier toId(Key key) {
        return new Identifier(key.namespace(), key.value());
    }
}
