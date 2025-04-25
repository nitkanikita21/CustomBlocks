package me.nitkanikita21.customblocks.core.breaking;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import lombok.Data;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.joml.Vector3i;

import java.util.UUID;

@Getter
public class BlockProgress {
    private double damage = 0.0;
    private final double hardness;
    private final Vector3i pos;
    private final World world;
    private final Set<UUID> participants = HashSet.empty();

    public BlockProgress(Vector3i pos, double hardness, World world) {
        this.pos = pos;
        this.hardness = hardness;
        this.world = world;
    }

    public synchronized void addDamage(Player player, double amount) {
        this.damage += amount;
        this.participants.add(player.getUniqueId());
    }

    public synchronized double getProgress() {
        return damage / hardness;
    }

    public boolean isComplete() {
        return damage >= hardness;
    }
}