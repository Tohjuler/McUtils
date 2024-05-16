package dk.tohjuler.mcutils.gui.items;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import dk.tohjuler.mcutils.config.ConfigurationFile;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.gui.handler.ItemEventHandler;
import dk.tohjuler.mcutils.gui.utils.*;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.items.SkullCreator;
import dk.tohjuler.mcutils.items.YamlItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Getter
public class Item<T extends BaseGui, S extends IStorage> implements IItem<T, S> {
    private final ConfigBasedGuiBase<T, S> gui;
    private final String id;

    @Setter
    private String slot;
    @Setter
    private ItemBuilder item;
    @Setter
    private @Nullable ItemBuilder fallbackItem;
    protected Predicate<Player> show;
    protected BiPredicate<Player, S> showWithStorage;
    protected AsList<?, T, S> asList;

    @Setter
    private String stringMaterial;

    protected BiConsumer<Player, WrappedInventoryClickEvent<T, S>> clickAction;
    protected Replacer<S> replacer;
    protected ItemEventHandler<T, S> eventHandler;

    public Item(ConfigBasedGuiBase<T, S> gui, String id, int slot, ItemBuilder item) {
        this.gui = gui;
        this.id = id;
        this.slot = slot + "";
        this.item = item;
    }

    public Item(ConfigBasedGuiBase<T, S> gui, String id, String slot, ItemBuilder item) {
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
    public Item<T, S> stringMaterial(String stringMaterial) {
        this.stringMaterial = stringMaterial;
        return this;
    }

    /**
     * Set the event handler for the item.
     * <p>
     *
     * @param eventHandler The event handler to use
     * @return The item
     * @since 1.17.0
     */
    public Item<T, S> eventHandler(ItemEventHandler<T, S> eventHandler) {
        this.eventHandler = eventHandler;
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
    public Item<T, S> show(Predicate<Player> show) {
        this.show = show;
        return this;
    }

    /**
     * Set a condition for the item to be shown.
     * <p>
     *
     * @param show The condition for the item to be shown
     * @return The item
     * @since 1.16.2
     */
    public Item<T, S> show(BiPredicate<Player, S> show) {
        this.showWithStorage = show;
        return this;
    }

    /**
     * Set the item as a list.
     * <p>
     *
     * @param asList The list to use
     * @return The item
     */
    public Item<T, S> asList(AsList<?, T, S> asList) {
        this.asList = asList;
        return this;
    }

    /**
     * Set the click action for the item.
     * If a {@link AsList} is in use, then the callback in the asList will be used instead.
     * <p>
     *
     * @param clickAction The action to run when the item is clicked
     * @return The item
     * @since 1.5
     */
    public Item<T, S> clickAction(BiConsumer<Player, WrappedInventoryClickEvent<T, S>> clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    /**
     * Alias for {@link #clickAction(BiConsumer)}
     * <p>
     *
     * @param clickAction The action to run when the item is clicked
     * @return The item
     * @since 1.5
     */
    public Item<T, S> onClick(BiConsumer<Player, WrappedInventoryClickEvent<T, S>> clickAction) {
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
    public Item<T, S> replacer(Replacer<S> replacer) {
        this.replacer = replacer;
        return this;
    }

    /**
     * Set the fallback item for the item.
     * The fallback item will be used if the item is not shown.
     * A fallback item DOES NOT support asList or when the item is static.
     * <p>
     *
     * @param fallbackItem The fallback item
     * @return The item
     * @since 1.10.0
     */
    public Item<T, S> fallbackItem(ItemBuilder fallbackItem) {
        this.fallbackItem = fallbackItem;
        return this;
    }

    /**
     * Parse the slot string to a list of slots.
     * <p>
     *
     * @return The list of slots
     * @since 1.15.0
     */
    public List<Integer> parseSlot() {
        return SlotParser.parseSlotString(slot);
    }

    /**
     * Parse the slot string to a single slot.
     * <p>
     *
     * @return The slot
     * @since 1.15.0
     */
    public int parseSlotFirst() {
        List<Integer> slots = parseSlot();
        if (slots.isEmpty()) {
            new RuntimeException("Invalid slot string: " + slot).printStackTrace();
            return 0;
        }
        return slots.get(0);
    }

    /**
     * Add the item to the gui.
     *
     * @since 1.5
     */
    public void add() {
        gui.setItem(this);
    }

    /**
     * Used to call the click action.
     * <p>
     *
     * @param player       The player who clicked the item
     * @param gui          The gui
     * @param event        The event
     * @param localStorage The local storage
     * @since 1.5
     */
    public void call(Player player, T gui, InventoryClickEvent event, S localStorage) {
        if (clickAction != null)
            clickAction.accept(player, new WrappedInventoryClickEvent<>(gui, event, this, localStorage));
    }

    /**
     * Build the item as a GuiItem.
     * <p>
     *
     * @param storage  The storage to use
     * @param player   The player to use
     * @param call     the callback to run when the item is clicked
     * @param replacer The replacer to use
     * @param gui      The gui to use
     * @param fallback If the fallback item should be used
     * @return The item as a GuiItem
     * @since 1.5.1
     */
    public GuiItem build(S storage, Player player, GuiAction<InventoryClickEvent> call, Replacer<S> replacer, T gui, boolean fallback) {
        GuiItem guiItem;
        ItemBuilder newItem = fallback && fallbackItem != null
                ? fallbackItem.clone()
                : item.clone();
        newItem = newItem.applyPlaceholder(player);
        if (replacer != null)
            newItem = replacer.replaceCall(storage, newItem, player);

        if (eventHandler != null)
            call = e -> eventHandler.onClick(player, new WrappedInventoryClickEvent<>(
                    gui,
                    e,
                    this,
                    storage
            ));

        if (stringMaterial == null)
            guiItem = newItem.buildAsGuiItem(call);
        else {
            String mat = replacer != null ? replacer.replaceCall(storage, stringMaterial, player) : stringMaterial;
            try {
                UUID uuid = UUID.fromString(mat);
                guiItem = newItem.applyType(SkullCreator.skullFromUuid(uuid)).buildAsGuiItem(call);
            } catch (IllegalArgumentException ignore) {

                Optional<XMaterial> xMat = XMaterial.matchXMaterial(mat);
                if (xMat.isPresent())
                    guiItem = newItem.applyType(xMat.get().parseItem()).buildAsGuiItem(call);
                else
                    guiItem = newItem.applyType(SkullCreator.skullFromBase64(mat)).buildAsGuiItem(call);
            }
        }

        if (eventHandler != null)
            eventHandler.onCreate(player, gui, guiItem.getItemStack());
        return guiItem;
    }

    /**
     * Build the item as a GuiItem.
     * <p>
     *
     * @param storage The storage to use
     * @param player  The player to use
     * @param call    the callback to run when the item is clicked
     * @param gui     The gui to use
     * @return The item as a GuiItem
     * @since 1.5.1
     */
    public GuiItem build(S storage, Player player, GuiAction<InventoryClickEvent> call, T gui) {
        return build(storage, player, call, replacer, gui, false);
    }

    /**
     * Build the fallback item as a GuiItem.
     * <p>
     *
     * @param storage The storage to use
     * @param player  The player to use
     * @param call    the callback to run when the item is clicked
     * @param gui     The gui to use
     * @return The item as a GuiItem
     * @since 1.10.0
     */
    public GuiItem buildFallback(S storage, Player player, GuiAction<InventoryClickEvent> call, T gui) {
        return build(storage, player, call, replacer, gui, true);
    }

    /**
     * Check if the item should be shown.
     * <p>
     *
     * @param player  The player to check
     * @param storage The storage to check
     * @return If the item should be shown
     * @since 1.16.2
     */
    public boolean checkShow(Player player, S storage) {
        if (show == null && showWithStorage == null) return true;
        if (show != null && showWithStorage == null) return show.test(player);
        if (show == null) return showWithStorage.test(player, storage);
        return show.test(player) && showWithStorage.test(player, storage);
    }

    protected void handleAsList(T gui, Player p, S localStorage) {
        List<AsList.Holder<T, S>> items = asList.call(p, localStorage);
        for (AsList.Holder<T, S> listItem : items)
            gui.addItem(
                    build(
                            localStorage,
                            listItem.getReplacer().getPlayer() != null
                                    ? listItem.getReplacer().getPlayer()
                                    : p,
                            e -> listItem.getCallback().accept(
                                    p,
                                    new Item.WrappedInventoryClickEvent<>(
                                            gui,
                                            e,
                                            this,
                                            localStorage,
                                            listItem
                                    )
                            ),
                            listItem.getReplacer(),
                            gui,
                            false
                    ));
    }

    /**
     * Used to set the item in the gui.
     * <p>
     *
     * @param gui          The gui to add the item to
     * @param p            The player
     * @param localStorage The local storage
     * @since 1.18.0
     */
    @Override
    public void setupGui(T gui, Player p, S localStorage) {
        if (checkShow(p, localStorage)) {
            if (asList != null) {
                handleAsList(gui, p, localStorage);
            } else if (parseSlotFirst() == -1)
                gui.addItem(build(localStorage, p,
                        e -> call(p, gui, e, localStorage),
                        gui
                ));
            else
                for (int slot : parseSlot())
                    gui.setItem(slot, build(localStorage, p,
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
                    gui.setItem(slot, buildFallback(localStorage, p,
                            e -> call(p, gui, e, localStorage),
                            gui
                    ));
    }

    @Override
    public void save(ConfigurationFile cf) {
        String path = "items." + getId();
        if (parseSlotFirst() == -1 || getAsList() != null)
            path = "noSlot-items." + getId();
        else
            cf.cf().set(path + ".slot", getSlot());

        YamlItem.saveItem(cf, getItem(), path);
        if (getStringMaterial() != null && !getStringMaterial().isEmpty())
            cf.cf().set(path + ".material", "adv:" + getStringMaterial());
        if (getItem().getHeadBase64() != null && !getItem().getHeadBase64().isEmpty())
            cf.cf().set(path + ".material", "adv:" + getItem().getHeadBase64());
        if (getAsList() != null)
            cf.cf().set(path + ".Note", "This item is a listed item.");
        if (getFallbackItem() != null)
            YamlItem.saveItem(cf, getFallbackItem(), path + ".fallback");
    }

    @Override
    public void loadExtra(ConfigurationFile cf, String basePath) {
        if (cf.cf().isSet(basePath + ".fallback"))
            setFallbackItem(YamlItem.loadItem(cf, basePath + ".fallback"));
    }

    /**
     * Wrapped event for the InventoryClickEvent.
     * Adding access to the gui, local storage and some utils.
     * <p>
     *
     * @param <T> The type of the gui
     * @since 1.5
     */
    public static class WrappedInventoryClickEvent<T extends BaseGui, S extends IStorage> {
        private final Item<T, S> item;
        @Getter
        private final T gui;
        @Getter
        private final InventoryClickEvent event;
        @Getter
        private final S localStorage;

        private AsList.Holder<T, S> holder;

        public WrappedInventoryClickEvent(T gui, InventoryClickEvent event, Item<T, S> item, S localStorage) {
            this.gui = gui;
            this.event = event;
            this.localStorage = localStorage;
            this.item = item;
        }

        public WrappedInventoryClickEvent(T gui, InventoryClickEvent event, Item<T, S> item, S localStorage, AsList.Holder<T, S> holder) {
            this.gui = gui;
            this.event = event;
            this.localStorage = localStorage;
            this.item = item;
            this.holder = holder;
        }

        // Utils

        /**
         * Refresh the clicked item in the gui.
         *
         * @since 1.13.0
         */
        public void refreshItem() {
            gui.updateItem(
                    event.getSlot(),
                    item.build(
                            localStorage,
                            (Player) event.getWhoClicked(),
                            e -> {
                                if (holder != null)
                                    holder.getCallback().accept(
                                            (Player) e.getWhoClicked(),
                                            new WrappedInventoryClickEvent<>(
                                                    gui,
                                                    e,
                                                    item,
                                                    localStorage,
                                                    holder
                                            )
                                    );
                                else
                                    item.call((Player) e.getWhoClicked(), gui, e, localStorage);
                            },
                            holder != null
                                    ? holder.getReplacer()
                                    : item.replacer,
                            gui,
                            false
                    )
            );
        }
    }
}
