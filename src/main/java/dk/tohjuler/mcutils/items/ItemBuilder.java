package dk.tohjuler.mcutils.items;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.components.util.ItemNbt;
import dev.triumphteam.gui.guis.GuiItem;
import dk.tohjuler.mcutils.gui.utils.Replacer;
import dk.tohjuler.mcutils.placeholder.PlaceholderHandler;
import dk.tohjuler.mcutils.strings.ColorUtils;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
     * Create a new ItemBuilder, from a head value
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
     * Default amount is 1
     *
     * @param material the material
     */
    public ItemBuilder(Material material) {
        this(material, 1);
    }

    /**
     * Create a new ItemBuilder, from a {@link Material}, an amount and durability
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
     * <br/>
     *
     * @param item       the item
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
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
            return this;
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
     * <br/>
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
    @SuppressWarnings("UnusedReturnValue")
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
        if (itemMeta == null) return "THE ITEM IS AIR!";
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
     * <br/>
     *
     * @param regex the regex to replace
     * @param func  the function to replace with
     * @return the itembuilder
     */
    public ItemBuilder replaceAllFromGui(String regex, Function<String, String> func) {
        if (getDisplayName() != null)
            setDisplayName(Replacer.replaceInString(getDisplayName(), regex, func));
        if (getLore() != null)
            setLore(
                    getLore().stream()
                            .map(s -> Replacer.replaceInString(s, regex, func))
                            .filter(s -> !s.startsWith("/**")) // Ignore lines starting with /**
                            .flatMap(s -> Arrays.stream(s.split("\\n")))
                            .flatMap(s -> Arrays.stream(s.split("%nl%")))
                            .collect(Collectors.toList())
            );

        return this;
    }

    /**
     * Apply a placeholder handler to the item.
     * This will apply the placeholder handler to the display name and lore.
     * <br/>
     *
     * @param handler the placeholder handler
     * @param player  the player to apply the placeholders for
     * @return the itembuilder
     * @since 1.23.0
     */
    public ItemBuilder applyPlaceholderHandler(PlaceholderHandler handler, @Nullable OfflinePlayer player) {
        if (getDisplayName() != null)
            setDisplayName(handler.apply(getDisplayName(), player));
        if (getLore() != null)
            setLore(
                    getLore().stream()
                            .map(s -> handler.apply(s, player))
                            .filter(s -> !s.startsWith("/**")) // Ignore lines starting with /**
                            .flatMap(s -> Arrays.stream(s.split("\\n")))
                            .flatMap(s -> Arrays.stream(s.split("%nl%")))
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
        if (itemMeta == null) return this;
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
        if (itemMeta == null) return this;
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
        if (itemMeta == null) return Collections.emptyList();
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
        if (itemMeta == null) return this;

        // Allows for using \n and %nl% in lore to create new lines
        itemMeta.setLore(
                lore.stream()
                        .map(this::colorize)
                        .map(str -> str.split("\n|%nl%"))
                        .flatMap(Arrays::stream)
                        .collect(Collectors.toList())
        );
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
        if (itemMeta == null) return this;
        // Allows for using \n and %nl% in lore to create new lines
        itemMeta.setLore(
                Arrays.stream(lore)
                        .map(this::colorize)
                        .map(str -> str.split("\n|%nl%"))
                        .flatMap(Arrays::stream).collect(Collectors.toList())
        );
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
        if (itemMeta == null) return this;
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
        if (itemMeta == null) return this;
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
        if (itemMeta == null) return this;
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
        return addLore(lines.toArray(new String[0]));
    }

    /**
     * Add a line to the lore
     *
     * @param lore the line to add
     * @return the itembuilder
     */
    public ItemBuilder addLore(String... lore) {
        ItemMeta itemMeta = this.item.getItemMeta();
        if (itemMeta == null) return this;
        if (itemMeta.getLore() == null)
            itemMeta.setLore(new ArrayList<>());
        List<String> list = itemMeta.getLore();
        if (list == null) list = new ArrayList<>();
        list.addAll(
                Arrays.stream(lore).map(this::colorize).collect(Collectors.toList())
        );
        // Allows for using \n and %nl% in lore to create new lines
        itemMeta.setLore(
                list.stream().map(str -> str.split("\n|%nl%")).flatMap(Arrays::stream).collect(Collectors.toList())
        );
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
     * Make the item unstackable
     *
     * @return the itembuilder
     */
    public ItemBuilder unstackable() {
        return nonStackable();
    }

    /**
     * Make the item glow
     * This is a visual effect
     * <br/>
     *
     * @return the itembuilder
     */
    public ItemBuilder glow() {
        return addEnchantment(Enchantment.LUCK, 1).hideEnchantment();
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
     * Serialize the item to a JSON string
     *
     * @return the JSON string
     */
    public String toJson() {
        return JsonItemStack.toJson(this.item);
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

    /**
     * Deserialize an item from a JSON string
     * The headBase64 will not be set, from this method, even if the item is a skull.
     *
     * @param json the JSON string
     * @return the itembuilder
     */
    public static ItemBuilder fromJson(String json) {
        return new ItemBuilder(JsonItemStack.fromJson(json));
    }

    /**
     * Create a new ItemBuilder from a string.
     * Valid types:
     * - Material name
     * - Base64 skull value
     * - UUID of a player
     * <br/>
     *
     * @param mat the string
     * @return the itembuilder
     * @since 1.17.0
     */
    public static ItemBuilder fromString(String mat) {
        if (mat == null || mat.isEmpty()) return new ItemBuilder("");
        if (mat.startsWith("adv:")) mat = mat.substring(4);
        try {
            return new ItemBuilder(UUID.fromString(mat));
        } catch (IllegalArgumentException e) {
            if (mat.length() > 100) // Head
                return new ItemBuilder(mat);
            else
                return new ItemBuilder(XMaterial.matchXMaterial(mat).orElse(XMaterial.STONE).parseItem());
        }
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
