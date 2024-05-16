package dk.tohjuler.mcutils.gui.utils;

import dk.tohjuler.mcutils.config.ConfigurationFile;

public interface IStorage {
    void save(ConfigurationFile cf, String path);
    void load(ConfigurationFile cf, String path);
}
