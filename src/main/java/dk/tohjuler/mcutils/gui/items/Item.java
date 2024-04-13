package dk.tohjuler.mcutils.gui.items;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.gui.utils.Storage;
import dk.tohjuler.mcutils.gui.utils.AsList;
import dk.tohjuler.mcutils.gui.utils.Replacer;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.items.SkullCreator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Getter
public class Item<T extends BaseGui> {
    private final ConfigBasedGuiBase<T> gui;
    private final String id;

    @Setter
    private int slot;
    @Setter
    private ItemBuilder item;
    private Predicate<Player> show;
    private AsList<?> asList;

    @Setter
    private String stringMaterial;

    private BiConsumer<Player, WrappedInventoryClickEvent<T>> clickAction;
    private Replacer replacer;

    public Item(ConfigBasedGuiBase<T> gui, String id, int slot, ItemBuilder item) {
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
     * Set the item as a list.
     * <p>
     *
     * @param asList The list to use
     * @return The item
     */
    public Item asList(AsList<?> asList) {
        this.asList = asList;
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
    public Item clickAction(BiConsumer<Player, WrappedInventoryClickEvent<T>> clickAction) {
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
    public Item onClick(BiConsumer<Player, WrappedInventoryClickEvent<T>> clickAction) {
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

    /**
     * Used to call the click action.
     * <p>
     *
     * @param player The player who clicked the item
     * @param event  The event
     * @since 1.5
     */
    public void call(Player player, T gui, InventoryClickEvent event) {
        if (clickAction != null)
            clickAction.accept(player, new WrappedInventoryClickEvent<>(gui, event));
    }

    /**
     * Build the item as a GuiItem.
     * <p>
     *
     * @param call The action to run when the item is clicked
     * @return The item as a GuiItem
     * @since 1.5.1
     */
    public GuiItem build(Storage storage, Player player, GuiAction<InventoryClickEvent> call) {
        ItemBuilder newItem = item.clone();
        newItem = newItem.applyPlaceholder(player);
        if (getReplacer() != null)
            getReplacer().replaceCall(storage, newItem, player);

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

    @Getter
    public static class WrappedInventoryClickEvent<T extends BaseGui> {
        private final T gui;
        private final InventoryClickEvent event;

        public WrappedInventoryClickEvent(T gui, InventoryClickEvent event) {
            this.gui = gui;
            this.event = event;
        }
    }
}
