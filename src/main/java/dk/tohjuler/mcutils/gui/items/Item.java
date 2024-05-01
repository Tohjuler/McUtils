package dk.tohjuler.mcutils.gui.items;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.gui.utils.AsList;
import dk.tohjuler.mcutils.gui.utils.Replacer;
import dk.tohjuler.mcutils.gui.utils.SlotParser;
import dk.tohjuler.mcutils.gui.utils.Storage;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.items.SkullCreator;
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
public class Item<T extends BaseGui> {
    private final ConfigBasedGuiBase<T> gui;
    private final String id;

    @Setter
    private String slot;
    @Setter
    private ItemBuilder item;
    @Setter
    private @Nullable ItemBuilder fallbackItem;
    private Predicate<Player> show;
    private BiPredicate<Player, Storage> showWithStorage;
    private AsList<?, T> asList;

    @Setter
    private String stringMaterial;

    private BiConsumer<Player, WrappedInventoryClickEvent<T>> clickAction;
    private Replacer replacer;

    public Item(ConfigBasedGuiBase<T> gui, String id, int slot, ItemBuilder item) {
        this.gui = gui;
        this.id = id;
        this.slot = slot + "";
        this.item = item;
    }

    public Item(ConfigBasedGuiBase<T> gui, String id, String slot, ItemBuilder item) {
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
    public Item<T> stringMaterial(String stringMaterial) {
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
    public Item<T> show(Predicate<Player> show) {
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
    public Item<T> show(BiPredicate<Player, Storage> show) {
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
    public Item<T> asList(AsList<?, T> asList) {
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
    public Item<T> clickAction(BiConsumer<Player, WrappedInventoryClickEvent<T>> clickAction) {
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
    public Item<T> onClick(BiConsumer<Player, WrappedInventoryClickEvent<T>> clickAction) {
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
    public Item<T> replacer(Replacer replacer) {
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
    public Item<T> fallbackItem(ItemBuilder fallbackItem) {
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
    public void call(Player player, T gui, InventoryClickEvent event, Storage localStorage) {
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
     * @param fallback If the fallback item should be used
     * @return The item as a GuiItem
     * @since 1.5.1
     */
    public GuiItem build(Storage storage, Player player, GuiAction<InventoryClickEvent> call, Replacer replacer, boolean fallback) {
        ItemBuilder newItem = fallback && fallbackItem != null
                ? fallbackItem.clone()
                : item.clone();
        newItem = newItem.applyPlaceholder(player);
        if (replacer != null)
            newItem = replacer.replaceCall(storage, newItem, player);

        if (stringMaterial == null) return newItem.buildAsGuiItem(call);

        String mat = replacer != null ? replacer.replaceCall(storage, stringMaterial, player) : stringMaterial;
        try {
            UUID uuid = UUID.fromString(mat);
            return newItem.applyType(SkullCreator.skullFromUuid(uuid)).buildAsGuiItem(call);
        } catch (IllegalArgumentException ignore) {

            Optional<XMaterial> xMat = XMaterial.matchXMaterial(mat);
            if (xMat.isPresent()) return newItem.applyType(xMat.get().parseItem()).buildAsGuiItem(call);

            return newItem.applyType(SkullCreator.skullFromBase64(mat)).buildAsGuiItem(call);
        }
    }

    /**
     * Build the item as a GuiItem.
     * <p>
     *
     * @param storage The storage to use
     * @param player  The player to use
     * @param call    the callback to run when the item is clicked
     * @return The item as a GuiItem
     * @since 1.5.1
     */
    public GuiItem build(Storage storage, Player player, GuiAction<InventoryClickEvent> call) {
        return build(storage, player, call, replacer, false);
    }

    /**
     * Build the fallback item as a GuiItem.
     * <p>
     *
     * @param storage The storage to use
     * @param player  The player to use
     * @param call    the callback to run when the item is clicked
     * @return The item as a GuiItem
     * @since 1.10.0
     */
    public GuiItem buildFallback(Storage storage, Player player, GuiAction<InventoryClickEvent> call) {
        return build(storage, player, call, replacer, true);
    }

    /**
     * Check if the item should be shown.
     * <p>
     * @param player The player to check
     * @param storage The storage to check
     * @return If the item should be shown
     * @since 1.16.2
     */
    public boolean checkShow(Player player, Storage storage) {
        if (show == null && showWithStorage == null) return true;
        if (show != null && showWithStorage == null) return show.test(player);
        if (show == null) return showWithStorage.test(player, storage);
        return show.test(player) && showWithStorage.test(player, storage);
    }

    /**
     * Wrapped event for the InventoryClickEvent.
     * Adding access to the gui, local storage and some utils.
     * <p>
     *
     * @param <T> The type of the gui
     * @since 1.5
     */
    public static class WrappedInventoryClickEvent<T extends BaseGui> {
        private final Item<T> item;
        @Getter
        private final T gui;
        @Getter
        private final InventoryClickEvent event;
        @Getter
        private final Storage localStorage;

        private AsList.Holder<T> holder;

        public WrappedInventoryClickEvent(T gui, InventoryClickEvent event, Item<T> item, Storage localStorage) {
            this.gui = gui;
            this.event = event;
            this.localStorage = localStorage;
            this.item = item;
        }

        public WrappedInventoryClickEvent(T gui, InventoryClickEvent event, Item<T> item, Storage localStorage, AsList.Holder<T> holder) {
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
                                            this
                                    );
                                else
                                    item.call((Player) e.getWhoClicked(), gui, e, localStorage);
                            },
                            holder != null
                                    ? holder.getReplacer()
                                    : item.replacer,
                            false
                    )
            );
        }
    }
}
