package me.nitkanikita21.customblocks.core.blockentity;

import me.nitkanikita21.customblocks.core.transfer.Storage;

public interface BlockStorageProvider<T extends Storage<?>> {
    T getStorage();
}
