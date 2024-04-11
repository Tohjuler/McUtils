package dk.tohjuler.mcutils.items;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.components.util.ItemNbt;
import dev.triumphteam.gui.guis.GuiItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemBuilder {
    private ItemStack item;

    /**
     * Create a new ItemBuilder, from a {@link ItemStack}
     *
     * @param itemStack the itemstack
     */
    public ItemBuilder(ItemStack itemStack) {
        if (itemStack == null) itemStack = new ItemStack(Material.STONE);
        this.item = itemStack.clone();
    }

    /**
     * Create a new ItemBuilder, from a base64 string
     *
     * @param value the base64 string
     */
    public ItemBuilder(@NotNull String value) {
        this.item = SkullCreator.skullFromBase64(value);
    }

    /**
     * Create a new ItemBuilder, from a {@link UUID}
     *
     * @param uuid the uuid
     */
    public ItemBuilder(@NotNull UUID uuid) {
        this.item = SkullCreator.skullFromUuid(uuid);
    }

    /**
     * Create a new ItemBuilder, from a {@link Material} and an amount
     *
     * @param material the material
     * @param amount   the amount
     */
    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material);
        this.item.setAmount(amount);
    }

    /**
     * Create a new ItemBuilder, from a {@link Material}
     *
     * @param material the material
     */
    public ItemBuilder(Material material) {
        this(material, material.getMaxStackSize());
    }

    /**
     * Create a new ItemBuilder, from a {@link Material}, an amount and a durability
     *
     * @param material   the material
     * @param amount     the amount
     * @param durability the durability
     */
    public ItemBuilder(Material material, int amount, short durability) {
        this.item = new ItemStack(material);
        this.item.setAmount(amount);
        this.item.setDurability(durability);
    }

    /**
     * Build the item, why do I even explain this?
     *
     * @return the built item
     */
    public ItemStack build() {
        return this.item;
    }

    /**
     * Build the item, but as a {@link GuiItem}
     * This is for Triumphteam's GUI
     *
     * @return the built item
     */
    public GuiItem buildAsGuiItem() {
        return dev.triumphteam.gui.builder.item.ItemBuilder.from(this.item).asGuiItem();
    }

    /**
     * Build the item, but as a {@link GuiItem}
     * This is for Triumphteam's GUI
     *
     * @param action the action to run when the item is clicked
     * @return the built item
     */
    public GuiItem buildAsGuiItem(GuiAction<InventoryClickEvent> action) {
        return dev.triumphteam.gui.builder.item.ItemBuilder.from(this.item).asGuiItem(action);
    }

    public ItemBuilder applyPlaceholder(Player p) {
        ItemBuilder ib = this;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Bukkit.getServer().getLogger().warning("PlaceholderAPI is not installed, placeholders will not work.");
            return ib;
        }
        ib = modifyMeta(meta -> {
            if (meta.hasDisplayName())
                meta.setDisplayName(PlaceholderAPI.setPlaceholders(p, meta.getDisplayName()));
            if (meta.hasLore())
                meta.setLore(meta.getLore().stream().map(s -> PlaceholderAPI.setPlaceholders(p, s)).collect(Collectors.toList()));
            return meta;
        });
        return ib;
    }

    /**
     * Set the material of the item
     *
     * @param material the material
     * @return the itembuilder
     */
    public ItemBuilder setType(Material material) {
        this.item.setType(material);
        return this;
    }

    /**
     * Get the item amount
     *
     * @return the item amount
     */
    public int getAmount() {
        return this.item.getAmount();
    }

    /**
     * Set the item amount
     *
     * @param amount the item amount
     * @return the itembuilder
     */
    public ItemBuilder setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    /**
     * Get the item durability
     *
     * @param durability the item durability
     * @return the itembuilder
     */
    public ItemBuilder setDurability(short durability) {
        this.item.setDurability(durability);
        return this;
    }

    /**
     * Get the item display name
     *
     * @return the item display name
     */
    public String getDisplayName() {
        ItemMeta itemMeta = this.item.getItemMeta();
        return itemMeta.getDisplayName();
    }

    /**
     * Replace some text in the lore and the display name
     *
     * @param replace     the text to replace
     * @param replaceWith the text to replace it with
     * @return the itembuilder
     */
    public ItemBuilder replaceLoreAndDisplayName(String replace, String replaceWith) {
        return replaceLore(replace, colorize(replaceWith)).replaceDisplayName(replace, colorize(replaceWith));
    }

    /**
     * Set the item display name
     *
     * @param name the item display name
     * @return the itembuilder
     */
    public ItemBuilder setDisplayName(String name) {
        ItemMeta itemMeta = this.item.getItemMeta();
        itemMeta.setDisplayName(colorize(name));
        this.item.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Replace text in the display name
     *
     * @param replace     the text to replace
     * @param replaceWith the text to replace it with
     * @return the itembuilder
     */
    public ItemBuilder replaceDisplayName(String replace, String replaceWith) {
        ItemMeta itemMeta = this.item.getItemMeta();
        if (itemMeta.hasDisplayName()) {
            itemMeta.setDisplayName(itemMeta.getDisplayName().replace(replace, colorize(replaceWith)));
            this.item.setItemMeta(itemMeta);
        }
        return this;
    }

    /**
     * Get the item lore
     *
     * @return the item lore
     */
    public List<String> getLore() {
        ItemMeta itemMeta = this.item.getItemMeta();
        return (itemMeta.hasLore() && itemMeta.getLore() != null) ? itemMeta.getLore() : new ArrayList<>();
    }

    /**
     * Set the item lore
     *
     * @param lore the item lore
     * @return the itembuilder
     */
    public ItemBuilder setLore(Collection<String> lore) {
        ItemMeta itemMeta = this.item.getItemMeta();
        ArrayList<String> arrayList = new ArrayList<>();
        for (String str : lore)
            arrayList.add(colorize(str));
        itemMeta.setLore(arrayList);
        this.item.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Set the item lore
     *
     * @param lore the item lore
     * @return the itembuilder
     */
    public ItemBuilder setLore(String... lore) {
        ItemMeta itemMeta = this.item.getItemMeta();
        ArrayList<String> arrayList = new ArrayList<>();
        byte b = 0;
        String[] arrayOfString;
        for (int i = (arrayOfString = lore).length; b < i; ) {
            String str = arrayOfString[b];
            arrayList.add(colorize(str));
            b = (byte) (b + 1);
        }
        itemMeta.setLore(arrayList);
        this.item.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Replace text in the lore
     *
     * @param replace     the text to replace
     * @param replaceWith the text to replace it with
     * @return the itembuilder
     */
    public ItemBuilder replaceLore(String replace, String replaceWith) {
        ItemMeta itemMeta = this.item.getItemMeta();
        if (itemMeta.hasLore()) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (String str : itemMeta.getLore()) {
                if (str == null) continue;
                arrayList.add(str.replace(replace, colorize(replaceWith)));
            }
            itemMeta.setLore(arrayList);
            this.item.setItemMeta(itemMeta);
        }
        return this;
    }

    /**
     * Replace text in the lore
     *
     * @param replace     the text to replace
     * @param replaceWith the text to replace it with, multiple lines
     * @return
     */
    public ItemBuilder replaceLore(String replace, List<String> replaceWith) {
        ItemMeta itemMeta = this.item.getItemMeta();
        if (itemMeta.hasLore()) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (String str : itemMeta.getLore()) {
                if (str == null) continue;
                if (str.contains(replace)) {
                    for (String str1 : replaceWith)
                        arrayList.add(colorize(str1));
                    continue;
                }
                arrayList.add(str);
            }
            itemMeta.setLore(arrayList);
            this.item.setItemMeta(itemMeta);
        }
        return this;
    }

    /**
     * Replace text in the lore
     *
     * @param replace     the text to replace
     * @param replaceWith the text to replace it with, multiple lines
     * @return
     */
    public ItemBuilder replaceLore(String replace, String... replaceWith) {
        ItemMeta itemMeta = this.item.getItemMeta();
        if (itemMeta.hasLore()) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (String str : itemMeta.getLore()) {
                if (str == null) continue;
                if (str.contains(replace)) {
                    byte b = 0;
                    String[] arrayOfString;
                    for (int i = (arrayOfString = replaceWith).length; b < i; ) {
                        String str1 = arrayOfString[b];
                        arrayList.add(colorize(str1));
                        b = (byte) (b + 1);
                    }
                    continue;
                }
                arrayList.add(str);
            }
            itemMeta.setLore(arrayList);
            this.item.setItemMeta(itemMeta);
        }
        return this;
    }

    /**
     * Add a line to the lore
     *
     * @param lines the line to add
     * @return the itembuilder
     */
    public ItemBuilder addLore(Collection<String> lines) {
        ItemMeta itemMeta = this.item.getItemMeta();
        if (itemMeta.getLore() == null)
            itemMeta.setLore(new ArrayList());
        List<String> list = itemMeta.getLore();
        for (String str : lines)
            list.add(colorize(str));
        itemMeta.setLore(list);
        this.item.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Add a line to the lore
     *
     * @param lore the line to add
     * @return the itembuilder
     */
    public ItemBuilder addLore(String... lore) {
        ItemMeta itemMeta = this.item.getItemMeta();
        if (itemMeta.getLore() == null)
            itemMeta.setLore(new ArrayList());
        List<String> list = itemMeta.getLore();
        if (list == null) list = new ArrayList<>();
        byte b = 0;
        String[] arrayOfString;
        for (int i = (arrayOfString = lore).length; b < i; ) {
            String str = arrayOfString[b];
            list.add(colorize(str));
            b = (byte) (b + 1);
        }
        itemMeta.setLore(list);
        this.item.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Add an enchantment to the item
     *
     * @param enchantment the enchantment
     * @param level       the level
     * @return the itembuilder
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Add itemFlags to the item
     *
     * @param flags the itemFlags
     * @return the itembuilder
     */
    public ItemBuilder addItemFlags(ItemFlag... flags) {
        ItemMeta m = this.item.getItemMeta();
        m.addItemFlags(flags);
        this.item.setItemMeta(m);
        return this;
    }

    /**
     * Hide the enchantments
     *
     * @return the itembuilder
     */
    public ItemBuilder hideEnchantment() {
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.item.setItemMeta(meta);
        return this;
    }

    /**
     * Make the item unstackable
     *
     * @return the itembuilder
     */
    public ItemBuilder nonStackable() {
        this.item = ItemNbt.setString(this.item, "unstackable", UUID.randomUUID().toString());
        return this;
    }

    /**
     * Modify the item, using a function
     * Allows for more advanced modifications
     *
     * @param func the function
     * @return the itembuilder
     */
    public ItemBuilder modify(Function<ItemStack, ItemStack> func) {
        this.item = func.apply(this.item);
        return this;
    }

    /**
     * Modify the item meta, using a function
     * Allows for more advanced modifications
     *
     * @param func the function
     * @return the itembuilder
     */
    public ItemBuilder modifyMeta(Function<ItemMeta, ItemMeta> func) {
        this.item.setItemMeta(func.apply(this.item.getItemMeta()));
        return this;
    }

    private String colorize(String paramString) {
        return ChatColor.translateAlternateColorCodes('&', paramString);
    }
}
