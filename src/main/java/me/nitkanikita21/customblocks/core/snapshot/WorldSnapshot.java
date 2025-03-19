package me.nitkanikita21.customblocks.core.snapshot;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nitkanikita21.customblocks.core.block.Block;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntity;
import me.nitkanikita21.customblocks.core.blockentity.BlockEntityProvider;
import me.nitkanikita21.customblocks.core.blockstate.BlockState;
import me.nitkanikita21.customblocks.core.blockstate.property.BlockStateProperty;
import me.nitkanikita21.customblocks.core.registry.Registries;
import me.nitkanikita21.customblocks.core.util.Vector3iNbtUtils;
import me.nitkanikita21.registry.Identifier;
import net.kyori.adventure.nbt.*;
import org.joml.Vector3i;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorldSnapshot {
    public static final int CURRENT_VERSION = 2;

    Map<Vector3i, BlockState> blockStates;
    Map<Vector3i, BlockEntity> blockEntities;
    int version;


    public WorldSnapshot() {
        this(HashMap.empty(), HashMap.empty(), CURRENT_VERSION);
    }

    public WorldSnapshot(Map<Vector3i, BlockState> blockStates, Map<Vector3i, BlockEntity> blockEntities) {
        this.blockStates = blockStates;
        this.blockEntities = blockEntities;
        this.version = CURRENT_VERSION;
    }

    public void load(CompoundBinaryTag compound) {
        version = compound.getInt("Version");
        if(version != CURRENT_VERSION) {
            throw new RuntimeException("Invalid world version");
        }
        loadStates(compound.getList("BlockState"));
        loadBlockEntities(compound.getList("BlockEntity"));
    }

    public CompoundBinaryTag save(CompoundBinaryTag compound) {
        ListBinaryTag blockState = saveStates();
        ListBinaryTag blockEntity = saveBlockEntities();
        compound = compound.put("BlockState", blockState);
        compound = compound.put("BlockEntity", blockEntity);
        compound = compound.putInt("Version", CURRENT_VERSION);

        return compound;
    }

    private void loadStates(ListBinaryTag list) {
        for (BinaryTag binaryTag : list) {
            System.out.println("Loading state");
            CompoundBinaryTag compound = (CompoundBinaryTag) binaryTag;
            Identifier blockId = new Identifier(compound.getString("Id"));
            Vector3i pos = Vector3iNbtUtils.deserialize(compound.getCompound("Pos"));
            Block block = Registries.BLOCKS.get(blockId).getOrElseThrow(RuntimeException::new);

            CompoundBinaryTag propertiesCompound = compound.getCompound("Properties");
            BlockState state = new BlockState(
                block, block.getDefaultState().getProperties().toMap(k -> k, k ->
                    k.load(propertiesCompound)
                )
            );

            blockStates = blockStates.put(pos, state);
        }
    }

    private void loadBlockEntities(ListBinaryTag list) {
        for (BinaryTag binaryTag : list) {
            System.out.println("Loading blockentity");
            CompoundBinaryTag compound = (CompoundBinaryTag) binaryTag;
            if(compound.keySet().isEmpty())continue;
            Vector3i pos = Vector3iNbtUtils.deserialize(compound.getCompound("Pos"));
            Block block = blockStates.get(pos).get().getOwner();

            if(block instanceof BlockEntityProvider provider) {
                CompoundBinaryTag dataCompound = compound.getCompound("Data");

                BlockEntity be = provider.getBlockEntityType().create(pos, blockStates.get(pos).get());
                be.loadData(dataCompound);

                blockEntities = blockEntities.put(pos, be);
            }
        }
    }


    private ListBinaryTag saveStates() {
        return ListBinaryTag.from(
            blockStates.map(t -> {
                CompoundBinaryTag compound = CompoundBinaryTag.empty();
                Vector3i pos = t._1;
                BlockState blockState = t._2;

                compound = compound.putString("Id", blockState.getOwner().getIdentifier().toString());

                CompoundBinaryTag posCompound = CompoundBinaryTag.empty();
                posCompound = Vector3iNbtUtils.serialize(posCompound, pos);
                compound = compound.put("Pos", posCompound);

                CompoundBinaryTag propertiesCompound = CompoundBinaryTag.empty();

                for (BlockStateProperty<?> property : blockState.getProperties()) {
                    propertiesCompound = property.save(propertiesCompound, blockState.getProperty(property).get());
                }

                compound = compound.put("Properties", propertiesCompound);

                return compound;
            })
        );
    }

    private ListBinaryTag saveBlockEntities() {
        return ListBinaryTag.from(
            blockEntities.map(t -> {
                BlockEntity blockEntity = t._2;
                Vector3i pos = t._1;

                CompoundBinaryTag compound = CompoundBinaryTag.empty();
                CompoundBinaryTag dataCompound = blockEntity.saveData(CompoundBinaryTag.empty());

                CompoundBinaryTag posCompound = CompoundBinaryTag.empty();
                posCompound = Vector3iNbtUtils.serialize(posCompound, pos);
                compound = compound.put("Pos", posCompound);
                compound = compound.put("Data", dataCompound);
                return compound;
            })
        );
    }
}
