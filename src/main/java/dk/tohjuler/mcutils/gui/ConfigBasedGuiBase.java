package dk.tohjuler.mcutils.gui;

import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.config.ConfigurationFile;
import dk.tohjuler.mcutils.enums.FillType;
import dk.tohjuler.mcutils.gui.items.Item;
import dk.tohjuler.mcutils.gui.items.StaticItem;
import dk.tohjuler.mcutils.gui.utils.Replacer;
import dk.tohjuler.mcutils.gui.utils.Storage;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.items.YamlItem;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public abstract class ConfigBasedGuiBase<T extends BaseGui> {
    private final String id;
    private final @Nullable String category;
    @Setter
    private @NotNull String title;
    @Setter
    private int rows;
    @Setter
    private @NotNull FillType fillType;
    @Setter
    private ItemBuilder fillItem;

    @Setter
    private Replacer titleReplacer;

    private final Storage storage = new Storage();

    private final List<Item<T>> items = new ArrayList<>();

    public ConfigBasedGuiBase(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem, String category) {
        this.id = id;
        this.title = title;
        this.rows = rows;
        this.fillType = fillType;
        this.fillItem = fillItem;
        this.category = category;
    }

    public ConfigBasedGuiBase(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem) {
        this.id = id;
        this.title = title;
        this.rows = rows;
        this.fillType = fillType;
        this.fillItem = fillItem;
        this.category = null;
    }

    /**
     * Load the gui from a file.
     * This will clear the item and load the item from the config.
     * <p>
     *
     * @param folder The folder to load the gui from
     * @since 1.5
     */
    public void load(File folder) {
        if (category != null) folder = new File(folder, category);
        File file = new File(folder, id + ".yml");
        if (!file.exists()) {
            save(folder);
            return;
        }
        ConfigurationFile cf = new ConfigurationFile(file);

        title = cf.cf().getString("title");
        rows = cf.cf().getInt("rows");
        fillType = FillType.valueOf(cf.cf().getString("fillType"));
        fillItem = YamlItem.loadItem(cf, "fillItem");

        List<String> keys = new ArrayList<>();
        cf.cf().getConfigurationSection("items").getKeys(false).forEach(key -> {
            keys.add(key);
            ItemBuilder item = YamlItem.loadItem(cf, "items." + key);
            int slot = cf.cf().getInt("items." + key + ".slot");
            String mat = cf.cf().getString("items." + key + ".material");

            Item<T> i = items.stream().filter(i2 -> i2.getId().equals(key)).findFirst().orElse(null);
            if (i != null) {
                if (mat.startsWith("adv:")) i.setStringMaterial(mat.substring(4));
                i.setItem(item);
                i.setSlot(slot);

                if (cf.cf().isSet("items." + key + ".fallback"))
                    i.setFallbackItem(YamlItem.loadItem(cf, "items." + key + ".fallback"));
            } else
                item(key, slot, item)
                        .stringMaterial(mat.startsWith("adv:") ? mat.substring(4) : null)
                        .add();
        });

        cf.cf().getConfigurationSection("noSlot-items").getKeys(false).forEach(key -> {
            if (keys.contains(key)) return;
            ItemBuilder item = YamlItem.loadItem(cf, "noSlot-items." + key);
            String mat = cf.cf().getString("noSlot-items." + key + ".material");

            Item<T> i = items.stream().filter(i2 -> i2.getId().equals(key)).findFirst().orElse(null);
            if (i != null) {
                if (mat.startsWith("adv:")) i.setStringMaterial(mat.substring(4));
                i.setItem(item);
            } else
                item(key, item)
                        .stringMaterial(mat.startsWith("adv:") ? mat.substring(4) : null)
                        .add();
        });

        // Remove any items that are not in the config
        items.removeIf(i -> !keys.contains(i.getId()));

        storage.load(cf, "vars");
    }

    /**
     * Save the gui to a file.
     * <p>
     *
     * @param folder The folder to save the gui to
     * @since 1.5
     */
    public void save(File folder) {
        if (category != null) folder = new File(folder, category);
        File file = new File(folder, id + ".yml");
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException ex) {
            new RuntimeException("Could not create file: " + file.getAbsolutePath(), ex).printStackTrace();
        }
        ConfigurationFile cf = new ConfigurationFile(file);

        items.clear();
        init();

        cf.cf().set("title", title);
        cf.cf().set("rows", rows);
        cf.cf().set("fillType", fillType.name());
        if (fillItem != null)
            YamlItem.saveItem(cf, fillItem, "fillItem");

        for (Item<T> item : items) {
            // Don't save static items
            if (item instanceof StaticItem) continue;

            String path = "items." + item.getId();
            if (item.getSlot() == -1 || item.getAsList() != null)
                path = "noSlot-items." + item.getId();
            else
                cf.cf().set(path + ".slot", item.getSlot());

            YamlItem.saveItem(cf, item.getItem(), path);
            if (item.getStringMaterial() != null && !item.getStringMaterial().isEmpty())
                cf.cf().set(path + ".material", "adv:" + item.getStringMaterial());
            if (item.getItem().getHeadBase64() != null && !item.getItem().getHeadBase64().isEmpty())
                cf.cf().set(path + ".material", "adv:" + item.getItem().getHeadBase64());
            if (item.getAsList() != null)
                cf.cf().set(path + ".Note", "This item is a listed item.");
            if (item.getFallbackItem() != null)
                YamlItem.saveItem(cf, item.getFallbackItem(), path + ".fallback");
        }

        storage.save(cf, "vars");
        cf.save();
    }

    /**
     * Initialize the default items.
     * Will be overwritten by the config later.
     *
     * @since 1.5
     */
    public abstract void init();

    /**
     * What do you think this does?
     * <p>
     *
     * @param p Yes, I use p for player
     * @since 1.5
     */
    public void open(Player p) {
        open(p, storage1 -> {});
    }

    /**
     * What do you think this does?
     * <p>
     *
     * @param p Yes, I use p for player
     * @param initStorage A callback to set up the local storage
     * @since 1.11.0
     */
    public void open(Player p, Consumer<Storage> initStorage) {
        Storage localStorage = new Storage(storage);
        initStorage.accept(localStorage);
        T gui = createGui(p);

        fillGui(gui);
        gui.setDefaultClickAction(e -> e.setCancelled(true));

        items.forEach(item -> {
            if (item.getShow() == null || item.getShow().test(p)) {
                // Static items
                if (item instanceof StaticItem) {
                    if (item.getSlot() == -1)
                        gui.addItem(((StaticItem<T>) item).getItem(p).buildAsGuiItem(
                                e -> item.call(p, gui, e)
                        ));
                    else
                        gui.setItem(item.getSlot(), ((StaticItem<T>) item).getItem(p).buildAsGuiItem(
                                e -> item.call(p, gui, e)
                        ));
                    return;
                }

                // Normal items

                if (item.getAsList() != null) {
                    List<Replacer> replacers = item.getAsList().call(p);
                    for (Replacer replacer : replacers)
                        gui.addItem(
                                item.build(
                                        localStorage,
                                        replacer.getPlayer() != null
                                                ? replacer.getPlayer()
                                                : p,
                                        e -> item.call(p, gui, e),
                                        replacer,
                                        false
                                ));
                } else if (item.getSlot() == -1)
                    gui.addItem(item.build(localStorage, p,
                            e -> item.call(p, gui, e)
                    ));
                else
                    gui.setItem(item.getSlot(), item.build(localStorage, p,
                            e -> item.call(p, gui, e)
                    ));
            } else if (item.getFallbackItem() != null) // Fallback items
                if (item.getSlot() == -1)
                    gui.addItem(item.buildFallback(localStorage, p,
                            e -> item.call(p, gui, e)
                    ));
                else
                    gui.setItem(item.getSlot(), item.buildFallback(localStorage, p,
                            e -> item.call(p, gui, e)
                    ));

        });

        gui.open(p);
    }

    private String applyPlaceholder(Player p, String s) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return s;
        return PlaceholderAPI.setPlaceholders(p, s);
    }

    /**
     * Set an item in the gui.
     * If the item has a -1 slot, it will be added to the gui without a slot.
     * Pls, use the {@link #item(String, int, ItemBuilder)} method to create the item.
     * <p>
     *
     * @param item The item to set
     * @since 1.5
     */
    public void setItem(Item<T> item) {
        if (item.getSlot() != -1 && items.stream().anyMatch(i -> i.getSlot() == item.getSlot()))
            items.removeIf(i -> i.getSlot() == item.getSlot());

        items.add(item);
    }

    /**
     * Create a new item.
     * <p>
     *
     * @param id   The id of the item
     * @param slot The slot of the item
     * @param item The item
     * @return The item
     * @since 1.5
     */
    protected Item<T> item(String id, int slot, ItemBuilder item) {
        return new Item<>(this, id, slot, item);
    }

    /**
     * Create a new item, with no slot.
     * <p>
     *
     * @param id   The id of the item
     * @param item The item
     * @return The item
     * @since 1.5
     */
    protected Item<T> item(String id, ItemBuilder item) {
        return new Item<>(this, id, -1, item);
    }

    /**
     * Create a new static item.
     * Use -1 as the slot to add the item to the gui without a slot.
     * <p>
     *
     * @param slot The slot of the item
     * @param func The function to get the item
     * @return The item
     * @since 1.5
     */
    protected StaticItem<T> staticItem(int slot, Function<Player, ItemBuilder> func) {
        return new StaticItem<>(this, slot, func);
    }

    /**
     * Create the base gui.
     * <p>
     *
     * @param p The player to create the gui for
     * @return The base gui
     * @since 1.5
     */
    protected abstract T createGui(Player p);

    /**
     * Get the title of the gui.
     * <p>
     *
     * @param p The player to get the title for
     * @return The title
     * @since 1.10.0
     */
    protected String getTitle(Player p) {
        return titleReplacer != null ? titleReplacer.replaceCall(storage, title, p) : title;
    }

    private void fillGui(BaseGui gui) {
        switch (fillType) {
            case ALL:
                gui.getFiller().fill(fillItem.buildAsGuiItem());
                break;
            case TOP:
                gui.getFiller().fillTop(fillItem.buildAsGuiItem());
                break;
            case BOTTOM:
                gui.getFiller().fillBottom(fillItem.buildAsGuiItem());
                break;
            case SIDES:
                gui.getFiller().fillBetweenPoints(0, 0, 5, 0, fillItem.buildAsGuiItem());
                gui.getFiller().fillBetweenPoints(0, 8, 5, 8, fillItem.buildAsGuiItem());
            case AROUND:
                gui.getFiller().fillBorder(fillItem.buildAsGuiItem());
                break;
            case TOP_BOTTOM:
                gui.getFiller().fillTop(fillItem.buildAsGuiItem());
                gui.getFiller().fillBottom(fillItem.buildAsGuiItem());
                break;
            case NONE:
                break;
        }
    }
}
