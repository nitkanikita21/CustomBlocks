package me.nitkanikita21.customblocks.core.blockentity;

import lombok.Getter;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.joml.Vector3i;

public abstract class BlockEntity {

    @Getter
    private final BlockEntityType<?> type;
    @Getter
    private final Vector3i pos;
    @Getter
    private BlockState cachedState;

    public void loadData(CompoundBinaryTag compound) {}
    public void saveData(CompoundBinaryTag compound) {}

    public BlockEntity(BlockEntityType<?> type, Vector3i pos, BlockState state) {
        this.type = type;
        this.pos = pos;
        this.cachedState = state;
    }
}
