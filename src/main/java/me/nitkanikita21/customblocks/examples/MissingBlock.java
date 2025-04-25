package me.nitkanikita21.customblocks.examples;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.block.BlockProperties;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.UUID;

public class MissingBlock extends Block {

    private static final String MISSING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M2MDNjNzk1NjAzMTk5OTZkNjM5NDEyOGI0OWZlYzc2NTBjZjg2N2ExZTQ4ZmI4MGM2MDQzZTc3MGRkNzFiZCJ9fX0=";

    public MissingBlock() {
        super(
            BlockProperties.builder()
                .name(
                    MiniMessage.miniMessage()
                        .deserialize("<i:false><dark_purple>Block not found</dark_purple>")
                )
                .build()
        );
    }

    @Override
    public @NotNull BlockState getDefaultState() {
        BlockState state = super.getDefaultState();
        return state;
    }

    @Override
    public WrappedBlockState getClientBlock(BlockState state, WorldAccessor world, Vector3i pos) {
        return SpigotConversionUtil.fromBukkitBlockData(Material.NOTE_BLOCK.createBlockData());
    }

    @Override
    protected ItemStack buildItemStack() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        head.editMeta(meta -> {
            meta.displayName(properties.getName());
            SkullMeta skullMeta = (SkullMeta) meta;

            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", MISSING_TEXTURE));
            skullMeta.setPlayerProfile(profile);
            head.setItemMeta(meta);
        });

        return head;
    }
}
