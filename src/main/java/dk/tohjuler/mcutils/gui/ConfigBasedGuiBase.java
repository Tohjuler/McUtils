package dk.tohjuler.mcutils.gui;

import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.config.ConfigurationFile;
import dk.tohjuler.mcutils.enums.FillType;
import dk.tohjuler.mcutils.gui.handler.GuiEventHandler;
import dk.tohjuler.mcutils.gui.items.AsyncItem;
import dk.tohjuler.mcutils.gui.items.Item;
import dk.tohjuler.mcutils.gui.items.StaticItem;
import dk.tohjuler.mcutils.gui.utils.IStorage;
import dk.tohjuler.mcutils.gui.utils.Replacer;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.items.YamlItem;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ConfigBasedGuiBase<T extends BaseGui, S extends IStorage> {
    @Getter
    private final String id;
    @Getter
    private final @Nullable String category;

    @Setter
    protected @NotNull String title;
    @Setter
    protected int rows;
    @Setter
    protected @NotNull FillType fillType;
    @Setter
    protected ItemBuilder fillItem;

    @Setter
    private Replacer<S> titleReplacer;

    private final S storage = createStorage(null);
    @Getter
    private final GuiEventHandler<T, S> guiEventHandler = new GuiEventHandler<>();

    @Getter
    private final List<Item<T, S>> items = new ArrayList<>();

    /**
     * Create a new ConfigBasedGui.
     * <br/>
     *
     * @param id       The id of the gui.
     * @param title    The title of the gui.
     * @param rows     The amount of rows in the gui.
     * @param fillType The fill type of the gui.
     * @param fillItem The fill item of the gui.
     * @param category The category of the gui.
     */
    public ConfigBasedGuiBase(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem, String category) {
        this.id = id;
        this.title = title;
        this.rows = rows;
        this.fillType = fillType;
        this.fillItem = fillItem;
        this.category = category;
        init();
    }

    /**
     * Create a new ConfigBasedGui.
     * <br/>
     *
     * @param id       The id of the gui.
     * @param title    The title of the gui.
     * @param rows     The amount of rows in the gui.
     * @param fillType The fill type of the gui.
     * @param fillItem The fill item of the gui.
     */
    public ConfigBasedGuiBase(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem) {
        this.id = id;
        this.title = title;
        this.rows = rows;
        this.fillType = fillType;
        this.fillItem = fillItem;
        this.category = null;
        init();
    }

    /**
     * Used to set up the storage.
     * ONLY call this in the constructor.
     * <br/>
     *
     * @param initStorage The callback to set up the storage
     * @since 1.16.0
     */
    protected void setupStorage(Consumer<S> initStorage) {
        initStorage.accept(storage);
    }

    /**
     * Load the gui from a file.
     * This will clear the item and load the item from the config.
     * <br/>
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

        try {
            title = cf.cf().getString("title");
            rows = cf.cf().getInt("rows");
            fillType = FillType.valueOf(cf.cf().getString("fillType", "NONE"));
            fillItem = YamlItem.loadItem(cf, "fillItem");
        } catch (Exception ex) {
            new RuntimeException("Could not load gui info: " + id, ex).printStackTrace();
        }

        List<String> keys = new ArrayList<>();
        if (cf.cf().isSet("items"))
            cf.cf().getConfigurationSection("items").getKeys(false).forEach(key -> {
                try {
                    keys.add(key);
                    ItemBuilder item = YamlItem.loadItem(cf, "items." + key);
                    String slot = cf.cf().getString("items." + key + ".slot");
                    String mat = cf.cf().getString("items." + key + ".material");

                    Item<T, S> i = items.stream().filter(i2 -> i2.getId().equals(key)).findFirst().orElse(null);
                    if (i != null) {
                        if (mat.startsWith("adv:")) i.setStringMaterial(mat.substring(4));
                        i.setItem(item);
                        i.setSlot(slot);

                        i.loadExtra(cf, "items." + key);
                    } else
                        item(key, slot, item)
                                .stringMaterial(mat.startsWith("adv:") ? mat.substring(4) : null)
                                .add();
                } catch (Exception ex) {
                    new RuntimeException("Could not load item: " + key, ex).printStackTrace();
                }
            });

        if (cf.cf().isSet("noSlot-items"))
            cf.cf().getConfigurationSection("noSlot-items").getKeys(false).forEach(key -> {
                try {
                    keys.add(key);
                    ItemBuilder item = YamlItem.loadItem(cf, "noSlot-items." + key);
                    String mat = cf.cf().getString("noSlot-items." + key + ".material");

                    Item<T, S> i = items.stream().filter(i2 -> i2.getId().equals(key)).findFirst().orElse(null);
                    if (i != null) {
                        if (mat.startsWith("adv:")) i.setStringMaterial(mat.substring(4));
                        i.setItem(item);
                    } else
                        item(key, item)
                                .stringMaterial(mat.startsWith("adv:") ? mat.substring(4) : null)
                                .add();
                } catch (Exception ex) {
                    new RuntimeException("Could not load item: " + key, ex).printStackTrace();
                }
            });

        // Remove any items that are not in the config
        items.removeIf(i -> !keys.contains(i.getId()));

        storage.load(cf, "vars");
    }

    /**
     * Save the gui to a file.
     * Will overwrite the file if it already exists.
     * <br/>
     *
     * @param folder The folder to save the gui to
     * @since 1.5
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save(File folder) {
        File file = new File(folder, id + ".yml");
        try {
            file.getParentFile().mkdirs();
            if (file.exists()) file.delete();
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

        for (Item<T, S> item : items)
            item.save(cf);

        storage.save(cf, "vars");
        cf.save();
    }

    /**
     * Create a new storage.
     * Default vars can be set here, {@link #setupStorage(Consumer)} can also be used in constructor.
     * <br/>
     *
     * @param parent The parent storage, called when a local storage is created.
     * @return The storage
     * @since 1.18.0
     */
    protected abstract S createStorage(@Nullable S parent);

    /**
     * Initialize the default items.
     * Will be overwritten by the config later.
     *
     * @since 1.5
     */
    public abstract void init();

    /**
     * What do you think this does?
     * <br/>
     *
     * @param p Yes, I use p for player
     * @since 1.5
     */
    public void open(Player p) {
        open(p, storage1 -> {
        });
    }

    /**
     * What do you think this does?
     * <br/>
     *
     * @param p           Yes, I use p for player
     * @param initStorage A callback to set up the local storage
     * @since 1.11.0
     */
    public void open(Player p, Consumer<S> initStorage) {
        S localStorage = createStorage(storage);
        initStorage.accept(localStorage);
        T gui = createGui(p, localStorage);

        fillGui(gui);
        gui.setCloseGuiAction(e -> {
            onClose(p, gui, localStorage);
            guiEventHandler.callOnClose(p, gui, localStorage);
        });
        gui.setDefaultClickAction(e -> {
            defaultClick(p, gui, e, localStorage);
            guiEventHandler.callDefaultClick(p, gui, e, localStorage);
        });

        items.forEach(item -> item.setupGui(gui, p, localStorage));

        onCreate(p, gui, localStorage);
        guiEventHandler.callOnCreate(p, gui, localStorage);
        gui.open(p);
    }

    /**
     * Opens the gui with a storage initializer with a IStorage.
     * <br/>
     *
     * @param p           Yes, I use p for player
     * @param initStorage A callback to set up the local storage
     * @since 1.18.0
     */
    public void openByIStorage(Player p, Consumer<IStorage> initStorage) {
        @SuppressWarnings("unchecked")
        Consumer<S> call = (Consumer<S>) initStorage;
        open(p, call);
    }

    private String applyPlaceholder(Player p, String s) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return s;
        return PlaceholderAPI.setPlaceholders(p, s);
    }

    /**
     * Set an item in the gui.
     * If the item has a -1 slot, it will be added to the gui without a slot.
     * Pls, use the {@link #item(String, int, ItemBuilder)} method to create the item.
     * <br/>
     *
     * @param item The item to set
     * @since 1.5
     */
    public void setItem(Item<T, S> item) {
        items.add(item);
    }

    /**
     * Create a new item.
     * <br/>
     *
     * @param id   The id of the item
     * @param slot The slot of the item
     * @param item The item
     * @return The item
     * @since 1.5
     */
    protected Item<T, S> item(String id, int slot, ItemBuilder item) {
        return new Item<>(this, id, slot, item);
    }

    /**
     * Create a new item.
     * <br/>
     *
     * @param id   The id of the item
     * @param slot The slot of the item
     * @param item The item
     * @return The item
     * @since 1.15.0
     */
    protected Item<T, S> item(String id, String slot, ItemBuilder item) {
        return new Item<>(this, id, slot, item);
    }

    /**
     * Create a new item, with no slot.
     * <br/>
     *
     * @param id   The id of the item
     * @param item The item
     * @return The item
     * @since 1.5
     */
    protected Item<T, S> item(String id, ItemBuilder item) {
        return new Item<>(this, id, -1, item);
    }

    /**
     * Create a new static item.
     * Use -1 as the slot to add the item to the gui without a slot.
     * <br/>
     *
     * @param slot The slot of the item
     * @param func The function to get the item
     * @return The item
     * @since 1.5
     */
    protected StaticItem<T, S> staticItem(int slot, Function<Player, ItemBuilder> func) {
        return new StaticItem<>(this, slot, func);
    }

    /**
     * Create a new async item.
     * Use -1 as the slot to add the item to the gui without a slot.
     * <br/>
     *
     * @param id   The id of the item
     * @param slot The slot of the item
     * @param item The item
     * @return The item
     * @since 1.18.0
     */
    protected AsyncItem<T, S> asyncItem(String id, int slot, ItemBuilder item) {
        return new AsyncItem<>(this, id, slot, item);
    }

    /**
     * Create a new async item, from a string slot.
     * Use -1 as the slot to add the item to the gui without a slot.
     * <br/>
     *
     * @param id   The id of the item
     * @param slot The slot of the item
     * @param item The item
     * @return The item
     * @since 1.18.0
     */
    protected AsyncItem<T, S> asyncItem(String id, String slot, ItemBuilder item) {
        return new AsyncItem<>(this, id, slot, item);
    }

    /**
     * Create the base gui.
     * <br/>
     *
     * @param p       The player to create the gui for
     * @param storage The storage to use
     * @return The base gui
     * @since 1.20.0
     */
    protected abstract T createGui(Player p, S storage);

    /**
     * Get the title of the gui.
     * <br/>
     *
     * @param p       The player to get the title for
     * @param storage The storage to use
     * @return The title
     * @since 1.20.4
     */
    protected String getTitle(Player p, S storage) {
        return titleReplacer != null ? titleReplacer.replaceCall(storage, title, p) : title;
    }

    // Events
    // ---

    /**
     * Called right before opening the gui.
     * <br/>
     *
     * @param player       The player that is opening the gui.
     * @param gui          The gui that is being opened.
     * @param localStorage The storage that is being used.
     * @since 1.17.0
     */
    public void onCreate(Player player, T gui, S localStorage) {
    }

    /**
     * Called when the gui is closed.
     * <br/>
     *
     * @param player       The player that is closing the gui.
     * @param gui          The gui that is being closed.
     * @param localStorage The storage that is being used.
     * @since 1.17.0
     */
    public void onClose(Player player, T gui, S localStorage) {
    }

    /**
     * Called when a player clicks in the gui.
     * Remember if there is a click action on the item, then it will also be called.
     * <br/>
     *
     * @param player       The player that clicked.
     * @param gui          The gui that was clicked in.
     * @param event        The event that was triggered.
     * @param localStorage The storage that is being used.
     * @since 1.17.0
     */
    public void defaultClick(Player player, T gui, InventoryClickEvent event, S localStorage) {
        event.setCancelled(true);
    }

    // Internal Utils

    protected void fillGui(BaseGui gui) {
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
