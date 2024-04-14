package dk.tohjuler.mcutils.items;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.components.util.ItemNbt;
import dev.triumphteam.gui.guis.GuiItem;
import dk.tohjuler.mcutils.gui.utils.Storage;
import dk.tohjuler.mcutils.gui.utils.Replacer;
import dk.tohjuler.mcutils.strings.ColorUtils;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemBuilder {
    private ItemStack item;
    @Getter
    private @Nullable String headBase64;

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
        this.headBase64 = value;
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
     * Create a new ItemBuilder, from a {@link XMaterial}
     *
     * @param xMaterial the xMaterial
     */
    public ItemBuilder(XMaterial xMaterial) {
        this.item = xMaterial.parseItem();
    }

    /**
     * Used to clone an itembuilder
     * <p>
     * @param item the item
     * @param headBase64 the head base64
     * @since 1.5.4
     */
    public ItemBuilder(ItemStack item, @Nullable String headBase64) {
        this.item = item;
        this.headBase64 = headBase64;
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

    /**
     * Apply placeholders to the item
     * This requires PlaceholderAPI to be installed
     * Applies to:
     * - Display name
     * - Lore
     *
     * @param p the player to apply the placeholders for
     * @return the itembuilder
     */
    public ItemBuilder applyPlaceholder(Player p) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Bukkit.getServer().getLogger().warning("PlaceholderAPI is not installed, placeholders will not work.");
            return this;
        }
        return modifyMeta(meta -> {
            if (meta.hasDisplayName())
                meta.setDisplayName(PlaceholderAPI.setPlaceholders(p, meta.getDisplayName()));
            if (meta.hasLore())
                meta.setLore(meta.getLore().stream().map(s -> PlaceholderAPI.setPlaceholders(p, s)).collect(Collectors.toList()));
            return meta;
        });
    }

    /**
     * Set the material of the item
     *
     * @param material the material
     * @return the itembuilder
     */
    public ItemBuilder setType(Material material) {
        this.item.setType(material);
        this.headBase64 = null;
        return this;
    }

    /**
     * Transform the item to a different type
     * This will keep the amount, display name, lore, enchantments, unstackable status and item flags
     * <p>
     *
     * @param item the item to transform
     * @return the itembuilder
     */
    public ItemBuilder applyType(ItemStack item) {
        ItemBuilder newItem = new ItemBuilder(item)
                .setAmount(this.getAmount())
                .setDisplayName(this.getDisplayName())
                .setLore(this.getLore())
                .addItemFlags(this.getItemFlags().toArray(new ItemFlag[0]));

        if (this.isUnstackable())
            newItem.nonStackable();
        if (getEnchantments() != null && !getEnchantments().isEmpty())
            getEnchantments().forEach(newItem::addEnchantment);

        this.item = newItem.build();
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
     * Replace placeholders from gui, in the lore and display name
     * This should only be used by the GUI API
     * <p>
     *
     * @param regex   the regex to replace
     * @param storage the storage to replace from
     * @param func    the function to replace with
     * @return the itembuilder
     */
    public ItemBuilder replaceAllFromGui(String regex, Storage storage, Function<Replacer.ReplaceEvent, String> func) {
        if (getDisplayName() != null)
            setDisplayName(Replacer.replaceInString(getDisplayName(), regex, storage, func));
        if (getLore() != null)
            setLore(
                    getLore().stream()
                            .map(s -> Replacer.replaceInString(s, regex, storage, func))
                            .flatMap(s -> Arrays.stream(s.split("\n")))
                            .collect(Collectors.toList())
            );

        return this;
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
            itemMeta.setDisplayName(itemMeta.getDisplayName().replaceAll(replace, colorize(replaceWith)));
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
                arrayList.add(str.replaceAll(replace, colorize(replaceWith)));
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
     * @return the itembuilder
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
     * @return the itembuilder
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
            itemMeta.setLore(new ArrayList<>());
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
            itemMeta.setLore(new ArrayList<>());
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
     * Set a NBT tag on the item
     *
     * @param keu   the key
     * @param value the value
     * @return the itembuilder
     */
    public ItemBuilder setTag(String keu, String value) {
        this.item = ItemNbt.setString(this.item, keu, value);
        return this;
    }

    /**
     * Get the tag of the item
     *
     * @param key the key
     * @return the value
     */
    public String getTag(String key) {
        return ItemNbt.getString(this.item, key);
    }

    /**
     * Remove a tag from the item
     *
     * @param key the key
     * @return the itembuilder
     */
    public ItemBuilder removeTag(String key) {
        this.item = ItemNbt.removeTag(this.item, key);
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

    private String colorize(String str) {
        return ColorUtils.colorize(str);
    }

    /**
     * Serialize the item to a base64 string
     *
     * @return the base64 string
     */
    public String serialize() {
        return ItemStackBase64.itemStackToBase64(this.item);
    }

    /**
     * Deserialize an item from a base64 itemstack
     * Skull values DOES NOT WORK, it needs to be serialized from {@link #serialize()} or {@link ItemStackBase64#itemStackToBase64(ItemStack)}
     *
     * @param base64 the base64 string
     * @return the itembuilder
     */
    public ItemBuilder deserialize(String base64) {
        item = ItemStackBase64.itemStackFromBase64(base64);

        return this;
    }

    /**
     * Create a new ItemBuilder from a base64 itemstack
     * Skull values DOES NOT WORK, it needs to be serialized from {@link #serialize()} or {@link ItemStackBase64#itemStackToBase64(ItemStack)}
     *
     * @param base64 the base64 string
     * @return the itembuilder
     */
    public static ItemBuilder fromBase64(String base64) {
        return new ItemBuilder(Material.AIR).deserialize(base64);
    }

    public ItemBuilder clone() {
        return new ItemBuilder(this.item.clone(), this.headBase64);
    }

    // Getters

    public Material getMaterial() {
        return item.getType();
    }

    public short getData() {
        return item.getDurability();
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return item.getEnchantments();
    }

    public List<ItemFlag> getItemFlags() {
        if (!item.hasItemMeta()) return Collections.emptyList();
        return new ArrayList<>(item.getItemMeta().getItemFlags());
    }

    public boolean isUnstackable() {
        return ItemNbt.getString(item, "unstackable") != null;
    }
}
