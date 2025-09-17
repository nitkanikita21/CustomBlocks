package me.nitkanikita21.customblocks.core.transfer;

import io.vavr.collection.List;

/**
 * Represents a transactional storage for a generic resource type T.
 * Can be used for items, fluids, energy, or any custom resource.
 */
public interface Storage<T> {

    /**
     * Returns all current resources in the storage.
     *
     * @return List of resources.
     */
    List<T> getResources();
}