package dk.tohjuler.mcutils.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

/**
 * A class to handle configuration files.
 */
public class ConfigurationFile {

    private final YamlConfiguration yamlConfiguration;
    @Getter
    private final File file;

    /**
     * Create a new ConfigurationFile
     *
     * @param plugin The plugin which should own this file.
     * @param name   The name (without extension) of the file
     */
    @SneakyThrows
    public ConfigurationFile(Plugin plugin, String name) {

        if (!name.contains(".yml"))
            name = name + ".yml";

        this.file = new File(plugin.getDataFolder(), name);
        this.yamlConfiguration = new YamlConfiguration();
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            //this.file.createNewFile();
            plugin.saveResource(name, false);
        }
        this.yamlConfiguration.load(this.file);
    }

    /**
     * Create a new ConfigurationFile
     * <br/>
     *
     * @param plugin The plugin which should own this file.
     * @param name   The name of the file
     * @param noSave If the file should not be saved
     */
    @SneakyThrows
    public ConfigurationFile(Plugin plugin, String name, boolean noSave) {

        if (!name.contains(".yml"))
            name = name + ".yml";

        this.file = new File(plugin.getDataFolder(), name);
        this.yamlConfiguration = new YamlConfiguration();
        if (!this.file.exists() && !noSave) {
            this.file.getParentFile().mkdirs();
            this.file.createNewFile();
        } else return;
        this.yamlConfiguration.load(this.file);
    }

    /**
     * Create a new ConfigurationFile
     * <br/>
     *
     * @param path The path to the folder
     * @param name The name of the file
     */
    @SneakyThrows
    public ConfigurationFile(String path, String name) {
        this.file = new File(path, name);
        this.yamlConfiguration = new YamlConfiguration();
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            this.file.createNewFile();
        }
        this.yamlConfiguration.load(this.file);
    }

    /**
     * Create a new ConfigurationFile
     * <br/>
     *
     * @param path        The path to the folder
     * @param name        The name of the file
     * @param classLoader The class loader
     * @param defaultFile The default file
     */
    @SneakyThrows
    public ConfigurationFile(String path, String name, ClassLoader classLoader, String defaultFile) {
        this.file = new File(path, name);
        this.yamlConfiguration = new YamlConfiguration();
        if (!this.file.exists()) {
            saveResource(defaultFile, false, new File(path), classLoader);
        }
        this.yamlConfiguration.load(this.file);
    }

    /**
     * Create a new ConfigurationFile
     * <br/>
     *
     * @param file The file
     */
    @SneakyThrows
    public ConfigurationFile(File file) {
        this.file = file;
        this.yamlConfiguration = new YamlConfiguration();
        this.yamlConfiguration.load(file);
    }

    /**
     * Create a new ConfigurationFile
     * <br/>
     *
     * @param stream The input stream
     */
    @SneakyThrows
    public ConfigurationFile(InputStream stream) {
        this.file = null;
        this.yamlConfiguration = new YamlConfiguration();
        this.yamlConfiguration.load(new InputStreamReader(stream));
    }

    /**
     * Save a resource to a file
     * <br/>
     *
     * @param resourcePath The path to the resource
     * @param replace      If the file should be replaced
     * @param dataFolder   The data folder
     * @param cl           The class loader
     */
    public void saveResource(String resourcePath, boolean replace, File dataFolder, ClassLoader cl) {
        if (resourcePath != null && !resourcePath.isEmpty()) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = this.getResource(resourcePath, cl);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + this.file);
            } else {
                File outFile = new File(dataFolder, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        throw new RuntimeException("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = Files.newOutputStream(outFile.toPath());
                        byte[] buf = new byte[1024];

                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException e) {
                    new RuntimeException("Could not save " + outFile.getName() + " to " + outFile, e).printStackTrace();
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

    /**
     * Get a resource as an input stream
     * <br/>
     *
     * @param filename The name of the file
     * @param cl       The class loader
     * @return The input stream
     */
    public InputStream getResource(String filename, ClassLoader cl) {
        try {
            URL url = cl.getResource(filename);
            if (url == null) {
                return null;
            } else {
                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                return connection.getInputStream();
            }
        } catch (IOException e) {

            return null;
        }
    }

    /**
     * Load the configuration file
     */
    @SneakyThrows
    public void load() {
        this.yamlConfiguration.load(this.file);
    }

    /**
     * Get the YamlConfiguration
     * <br/>
     *
     * @return The YamlConfiguration
     */
    public YamlConfiguration cf() {
        return yamlConfiguration;
    }

    /**
     * Save the configuration file
     */
    @SneakyThrows
    public void save() {
        yamlConfiguration.save(file);
    }

}
