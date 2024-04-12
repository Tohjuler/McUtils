package dk.tohjuler.mcutils.gui;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.config.ConfigurationFile;
import dk.tohjuler.mcutils.enums.FillType;
import dk.tohjuler.mcutils.gui.utils.Replacer;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.items.YamlItem;
import dk.tohjuler.mcutils.strings.ColorUtils;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
public abstract class Gui {
    private final String id;
    @Setter
    private @NotNull String title;
    @Setter
    private int rows;
    @Setter
    private @NotNull FillType fillType;
    @Setter
    private ItemBuilder fillItem;

    private final Storage storage = new Storage();

    private final List<Item> items = new ArrayList<>();

    public Gui(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem) {
        this.id = id;
        this.title = title;
        this.rows = rows;
        this.fillType = fillType;
        this.fillItem = fillItem;
        init();
    }

    /**
     * Load the gui from a file.
     * This will clear the item and load the item from the config.
     * <p>
     *
     * @param folder The folder to load the gui from
     */
    public void load(File folder) {
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

        cf.cf().getConfigurationSection("items").getKeys(false).forEach(key -> {
            ItemBuilder item = YamlItem.loadItem(cf, "items." + key);
            int slot = cf.cf().getInt("items." + key + ".slot");
            String mat = cf.cf().getString("items." + key + ".material");

            Item i = items.stream().findFirst().filter(i2 -> i2.getId().equals(key)).orElse(null);
            if (i != null) {
                if (mat.startsWith("adv:")) i.setStringMaterial(mat.substring(4));
                i.setItem(item);
                i.setSlot(slot);
            } else
                item(key, slot, item)
                        .stringMaterial(mat.startsWith("adv:") ? mat.substring(4) : null)
                        .add();
        });

        cf.cf().getConfigurationSection("noSlot-items").getKeys(false).forEach(key -> {
            ItemBuilder item = YamlItem.loadItem(cf, "noSlot-items." + key);
            String mat = cf.cf().getString("noSlot-items." + key + ".material");

            Item i = items.stream().findFirst().filter(i2 -> i2.getId().equals(key)).orElse(null);
            if (i != null) {
                if (mat.startsWith("adv:")) i.setStringMaterial(mat.substring(4));
                i.setItem(item);
            } else
                item(key, item)
                        .stringMaterial(mat.startsWith("adv:") ? mat.substring(4) : null)
                        .add();
        });

        storage.load(cf, "vars");
    }

    /**
     * Save the gui to a file.
     * <p>
     *
     * @param folder The folder to save the gui to
     */
    public void save(File folder) {
        File file = new File(folder, id + ".yml");
        ConfigurationFile cf = new ConfigurationFile(file);

        cf.cf().set("title", title);
        cf.cf().set("rows", rows);
        cf.cf().set("fillType", fillType.name());
        if (fillItem != null)
            YamlItem.saveItem(cf, fillItem, "fillItem");

        if (items.isEmpty()) init();

        for (Item item : items) {
            if (item.isStaticItem()) continue;
            String path = "items." + item.getId();
            if (item.getSlot() == -1)
                path = "noSlot-items." + item.getId();
            else
                cf.cf().set(path + ".slot", item.getSlot());

            YamlItem.saveItem(cf, item.getItem(), path);
            if (item.stringMaterial != null)
                cf.cf().set(path + ".material", "adv:" + item.stringMaterial);
        }

        storage.save(cf, "vars");
        cf.save();
    }

    /**
     * Initialize the default items.
     * Will be overwritten by the config later.
     */
    public abstract void init();

    /**
     * What do you think this does?
     * <p>
     *
     * @param p Yes, I use p for player
     */
    public void open(Player p) {
        dev.triumphteam.gui.guis.Gui gui = dev.triumphteam.gui.guis.Gui
                .gui()
                .title(Component.text(ColorUtils.colorize(title)))
                .rows(rows)
                .create();

        fillGui(gui);
        gui.setDefaultClickAction(e -> e.setCancelled(true));

        items.forEach(item -> {
            if (item.getShow() == null || item.getShow().test(p)) {
                ItemBuilder ib = item.getItem().clone();
                ib = ib.applyPlaceholder(p);
                if (item.getStringMaterial() != null)
                    ib.applyType(
                            XMaterial.matchXMaterial(
                                    applyPlaceholder(p, item.getStringMaterial())
                            ).orElse(XMaterial.STONE).parseItem()
                    );
                if (item.getReplacer() != null)
                    item.getReplacer().replaceCall(ib, p);

                if (item.getSlot() == -1)
                    gui.addItem(ib.buildAsGuiItem());
                else
                    gui.setItem(item.getSlot(), ib.buildAsGuiItem());
            }
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
    protected void setItem(Item item) {
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
    protected Item item(String id, int slot, ItemBuilder item) {
        return new Item(this, id, slot, item);
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
    protected Item item(String id, ItemBuilder item) {
        return new Item(this, id, -1, item);
    }

    @Getter
    public static class Item {
        private final Gui gui;
        private final String id;

        @Setter
        private int slot;
        @Setter
        private ItemBuilder item;
        private Predicate<Player> show;
        private boolean staticItem;

        @Setter
        private String stringMaterial;

        private Consumer<InventoryClickEvent> clickAction;
        private Replacer replacer;

        public Item(Gui gui, String id, int slot, ItemBuilder item) {
            this.gui = gui;
            this.id = id;
            this.slot = slot;
            this.item = item;
        }

        /**
         * Use a string as a material.
         * Placeholders from the replacer or PlaceholderAPI will be applied.
         * The string material will override the item material.
         * Example: "%player_uuid%" can be used to get the player's skull as the item.
         * <p>
         *
         * @param stringMaterial The string material
         * @return The item
         * @since 1.5
         */
        public Item stringMaterial(String stringMaterial) {
            this.stringMaterial = stringMaterial;
            return this;
        }

        /**
         * Set a condition for the item to be shown.
         * <p>
         *
         * @param show The condition for the item to be shown
         * @return The item
         * @since 1.5
         */
        public Item show(Predicate<Player> show) {
            this.show = show;
            return this;
        }

        /**
         * Make the item static.
         * Meaning it won't be saved to the config.
         * <p>
         *
         * @return The item
         * @since 1.5
         */
        public Item staticItem() {
            this.staticItem = true;
            return this;
        }

        /**
         * Set the click action for the item.
         * <p>
         *
         * @param clickAction The action to run when the item is clicked
         * @return The item
         * @since 1.5
         */
        public Item clickAction(Consumer<InventoryClickEvent> clickAction) {
            this.clickAction = clickAction;
            return this;
        }

        /**
         * Alias for {@link #clickAction(Consumer)}
         * <p>
         *
         * @param clickAction The action to run when the item is clicked
         * @return The item
         * @since 1.5
         */
        public Item onClick(Consumer<InventoryClickEvent> clickAction) {
            this.clickAction = clickAction;
            return this;
        }

        /**
         * Add a replacer to the item.
         * <p>
         *
         * @param replacer The replacer to add
         * @return The item
         * @since 1.5
         */
        public Item replacer(Replacer replacer) {
            this.replacer = replacer;
            return this;
        }

        /**
         * Add the item to the gui.
         *
         * @since 1.5
         */
        public void add() {
            gui.setItem(this);
        }
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
