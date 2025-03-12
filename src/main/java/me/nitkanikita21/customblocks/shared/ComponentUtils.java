package me.nitkanikita21.customblocks.shared;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
@UtilityClass
public final class ComponentUtils {
    public static Component PLUGIN_CAPTION = MiniMessage.miniMessage().deserialize(
        "<dark_gray>[<gold><b>Commandor</b></gold>]"
    );
    public static MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component pluginMessage(Component component) {
        return PLUGIN_CAPTION
            .append(Component.space())
            .append(component);
    }

    public static Component pluginMessage(String text) {
        return PLUGIN_CAPTION
            .append(Component.space())
            .append(MiniMessage.miniMessage().deserialize(text));
    }

    public static Component parse(String text) {
        return miniMessage.deserialize(text);
    }
}
