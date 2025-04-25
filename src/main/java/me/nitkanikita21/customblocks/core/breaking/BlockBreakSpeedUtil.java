package me.nitkanikita21.customblocks.core.breaking;// Використовуємо Spigot/Paper API (1.20.3+), де є атрибут player.block_break_speed

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BlockBreakSpeedUtil {

    private static final double DEFAULT_BREAK_SPEED = 1.0;
    public static final Attribute ATTRIBUTE = Attribute.BLOCK_BREAK_SPEED;
    public static final NamespacedKey ATTRIBUTE_ID = new NamespacedKey("customblocks", "custom_block_block_break_speed");

    public static void setBlockBreakSpeed(Player player, double multiplier) {
        AttributeInstance attr = player.getAttribute(ATTRIBUTE);
        if (attr == null) return;

        attr.getModifiers().stream()
            .filter(mod -> mod.getKey().equals(ATTRIBUTE_ID))
            .forEach(attr::removeModifier);

        AttributeModifier modifier = new AttributeModifier(
            ATTRIBUTE_ID,
            multiplier,
            AttributeModifier.Operation.ADD_SCALAR
        );
        attr.addModifier(modifier);
    }

    public static void resetBlockBreakSpeed(Player player) {
        AttributeInstance attr = player.getAttribute(ATTRIBUTE);
        if (attr == null) return;

        attr.getModifiers().stream()
            .filter(mod -> mod.getKey().equals(ATTRIBUTE_ID))
            .forEach(attr::removeModifier);
    }

    public static double getBlockBreakSpeed(Player player) {
        AttributeInstance attr = player.getAttribute(ATTRIBUTE);
        if (attr == null) return DEFAULT_BREAK_SPEED;

        return attr.getModifiers().stream()
            .filter(mod -> mod.getKey().equals(ATTRIBUTE_ID))
            .findFirst()
            .map(AttributeModifier::getAmount)
            .orElse(DEFAULT_BREAK_SPEED);
    }
}
