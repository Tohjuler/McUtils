package dk.tohjuler.mcutils.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

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

    @SneakyThrows
    public ConfigurationFile(String path, String name, ClassLoader classLoader, String defaultFile) {
        this.file = new File(path, name);
        this.yamlConfiguration = new YamlConfiguration();
        if (!this.file.exists()) {
            saveResource(defaultFile, false, new File(path), classLoader);
        }
        this.yamlConfiguration.load(this.file);
    }

    public void saveResource(String resourcePath, boolean replace, File dataFolder, ClassLoader cl) {
        if (resourcePath != null && !resourcePath.equals("")) {
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

    @SneakyThrows
    public void load() {
        this.yamlConfiguration.load(this.file);
    }

    @SneakyThrows
    public ConfigurationFile(File file) {
        this.file = null;
        this.yamlConfiguration = new YamlConfiguration();
        this.yamlConfiguration.load(file);
    }

    @SneakyThrows
    public ConfigurationFile(InputStream stream) {
        this.file = null;
        this.yamlConfiguration = new YamlConfiguration();
        this.yamlConfiguration.load(new InputStreamReader(stream));
    }

    public YamlConfiguration cf() {
        return yamlConfiguration;
    }

    @SneakyThrows
    public void save() {
        yamlConfiguration.save(file);
    }

}
