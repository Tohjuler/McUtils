package dk.tohjuler.mcutils.gui.utils;

import dk.tohjuler.mcutils.config.ConfigurationFile;

/**
 * An interface for storage.
 */
public interface IStorage {
    /**
     * Save the storage to a file
     * <br/>
     *
     * @param cf   The config file
     * @param path The path to save the storage to
     * @since 1.18.0
     */
    void save(ConfigurationFile cf, String path);

    /**
     * Load the storage from a file
     * <br/>
     *
     * @param cf   The config file
     * @param path The path to load the storage from
     * @since 1.18.0
     */
    void load(ConfigurationFile cf, String path);

    /**
     * Transfer the persisted fields from the current storage to the storage.
     * <br/>
     *
     * @param storage The storage to transfer the fields to.
     */
    default void transferPersistedFields(IStorage storage) {
        StoragePersistenceUtils.setPersistentFields(storage, StoragePersistenceUtils.getPersistentFields(this));
    }
}
