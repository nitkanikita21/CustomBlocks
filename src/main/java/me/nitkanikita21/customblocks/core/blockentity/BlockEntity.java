package me.nitkanikita21.customblocks.core.blockentity;

import lombok.Getter;
import lombok.Setter;
import me.nitkanikita21.customblocks.core.WorldAccessor;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.joml.Vector3i;

public abstract class BlockEntity {

    @Getter
    private final BlockEntityType<?> type;
    @Getter
    private final Vector3i pos;
    @Getter
    @Setter
    private BlockState cachedState;

    public BlockEntity(BlockEntityType<?> type, Vector3i pos, BlockState state) {
        this.type = type;
        this.pos = pos;
        this.cachedState = state;
    }

    public CompoundBinaryTag loadData(CompoundBinaryTag compound) {
        return CompoundBinaryTag.empty();
    }

    public CompoundBinaryTag saveData(CompoundBinaryTag compound) {
        return CompoundBinaryTag.empty();
    }
}
