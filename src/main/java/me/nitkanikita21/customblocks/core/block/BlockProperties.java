package me.nitkanikita21.customblocks.core.block;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlockProperties {
    Component name;
    @Builder.Default
    float hardness = 1f;
}
