package dk.tohjuler.mcutils.gui.items;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.config.ConfigurationFile;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.gui.utils.IStorage;
import dk.tohjuler.mcutils.gui.utils.SlotParser;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.items.YamlItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

/**
 * An async item is an item that gets loaded async.
 * <br/>
 *
 * @param <T> The type of the gui
 * @param <S> The type of the storage
 * @since 1.18.0
 */
@Getter
public class AsyncItem<T extends BaseGui, S extends IStorage> extends Item<T, S> {
    private @Nullable ItemBuilder loader = new ItemBuilder(XMaterial.BARRIER).setDisplayName("&cLoading...");
    @Setter
    private int fakeLoading = 0;

    /**
     * Create a new async item.
     * <br/>
     *
     * @param gui  The gui to add the item to.
     * @param id   The id of the item.
     * @param slot The slot of the item.
     * @param item The item to show.
     * @since 1.18.0
     */
    public AsyncItem(ConfigBasedGuiBase<T, S> gui, String id, int slot, ItemBuilder item) {
        super(gui, id, slot, item);
    }

    /**
     * Create a new async item.
     * <br/>
     *
     * @param gui  The gui to add the item to.
     * @param id   The id of the item.
     * @param slot The slot of the item.
     * @param item The item to show.
     * @since 1.18.0
     */
    public AsyncItem(ConfigBasedGuiBase<T, S> gui, String id, String slot, ItemBuilder item) {
        super(gui, id, slot, item);
    }

    /**
     * Set the loader item for this item.
     * The loader item is the item shown while the item is loading.
     * <br/>
     *
     * @param loader The loader item.
     * @return The item.
     * @since 1.18.0
     */
    // TODO: Possibility for loader in specific slot, when using asList
    public AsyncItem<T, S> loader(ItemBuilder loader) {
        this.loader = loader;
        return this;
    }

    private void asyncUpdateSlot(T gui, Player p, S localStorage) {
        if (checkShow(p, localStorage)) {
            if (asList != null) {
                handleAsList(gui, p, localStorage, true);
                gui.update();
            } else if (parseSlotFirst() == -1)
                gui.addItem(build(localStorage, p,
                        e -> call(p, gui, e, localStorage),
                        gui
                ));
            else
                for (int slot : parseSlot())
                    gui.updateItem(slot, build(localStorage, p,
                            e -> call(p, gui, e, localStorage),
                            gui
                    ));
        } else if (getFallbackItem() != null) // Fallback items
            if (parseSlotFirst() == -1)
                gui.addItem(buildFallback(localStorage, p,
                        e -> call(p, gui, e, localStorage),
                        gui
                ));
            else
                for (int slot : parseSlot())
                    gui.updateItem(slot, buildFallback(localStorage, p,
                            e -> call(p, gui, e, localStorage),
                            gui
                    ));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void setupGui(T gui, Player p, S localStorage) {
        if ((asList != null || parseSlotFirst() != -1) && loader != null)
            SlotParser.parseSlotString(getSlot(), this, () -> asList.getList(p, localStorage).size())
                    .forEach(slot -> {
                        if (slot == -1) return;
                        gui.setItem(slot, loader.buildAsGuiItem());
                    });

        Bukkit.getScheduler().runTaskLaterAsynchronously(
                JavaPlugin.getProvidingPlugin(getClass()),
                () -> asyncUpdateSlot(gui, p, localStorage),
                fakeLoading // Add a fake loading time
        );
    }

    @Override
    public void save(ConfigurationFile cf) {
        super.save(cf);
        if (loader == null) return;
        String path = "items." + getId();
        if (getAsList() != null || parseSlotFirst() == -1)
            path = "noSlot-items." + getId();
        else
            cf.cf().set(path + ".slot", getSlot());

        if (loader != null)
            YamlItem.saveItem(cf, loader, path + ".loader");
    }

    @Override
    public void load(ConfigurationFile cf, String basePath) {
        super.load(cf, basePath);

        if (cf.cf().isSet(basePath + ".loader"))
            loader = YamlItem.loadItem(cf, basePath + ".loader");
    }
}
