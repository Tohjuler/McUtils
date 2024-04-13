package dk.tohjuler.mcutils.gui.items;

import dk.tohjuler.mcutils.gui.Gui;
import dk.tohjuler.mcutils.gui.utils.AsList;
import dk.tohjuler.mcutils.gui.utils.Replacer;
import dk.tohjuler.mcutils.items.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Getter
public class Item {
    private final Gui gui;
    private final String id;

    @Setter
    private int slot;
    @Setter
    private ItemBuilder item;
    private Predicate<Player> show;
    private AsList<?> asList;

    @Setter
    private String stringMaterial;

    private BiConsumer<Player, InventoryClickEvent> clickAction;
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
    public Item clickAction(BiConsumer<Player, InventoryClickEvent> clickAction) {
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
    public Item onClick(BiConsumer<Player, InventoryClickEvent> clickAction) {
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
     * @since 1.5
     */
    public void add() {
        gui.setItem(this);
    }

    /**
     * Used to call the click action.
     * <p>
     * @param player The player who clicked the item
     * @param event The event
     * @since 1.5
     */
    public void call(Player player, InventoryClickEvent event) {
        if (clickAction != null)
            clickAction.accept(player, event);
    }
}
