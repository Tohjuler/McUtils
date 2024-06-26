package dk.tohjuler.mcutils.items;

import dk.tohjuler.mcutils.config.ConfigurationFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class YamlItem {
    /**
     * Save an {@link ItemBuilder} to a yaml configuration file
     *
     * @param cf      The configuration file to save to
     * @param item    The item to save
     * @param baseKey The base key to save the item under
     */
    public static void saveItem(@NotNull ConfigurationFile cf, @NotNull ItemBuilder item, @NotNull String baseKey) {
        cf.cf().set(baseKey + ".material", item.getMaterial().name());
        cf.cf().set(baseKey + ".amount", item.getAmount());
        if (item.getDisplayName() != null)
            cf.cf().set(baseKey + ".name", item.getDisplayName());
        if (item.getLore() != null && !item.getLore().isEmpty())
            cf.cf().set(baseKey + ".lore", item.getLore());
        if (item.getEnchantments() != null && !item.getEnchantments().isEmpty())
            cf.cf().set(baseKey + ".enchantments", item.getEnchantments());
        if (item.getItemFlags() != null && !item.getItemFlags().isEmpty())
            cf.cf().set(baseKey + ".flags", item.getItemFlags().stream().map(ItemFlag::name).collect(Collectors.toList()));
        if (item.getData() != 0)
            cf.cf().set(baseKey + ".data", item.getData());
    }

    /**
     * Load an {@link ItemBuilder} from a yaml configuration file
     *
     * @param cf      The configuration file to load from
     * @param baseKey The base key to load the item from
     * @return The loaded item
     */
    public static ItemBuilder loadItem(@NotNull ConfigurationFile cf, @NotNull String baseKey) {
        if (!cf.cf().isSet(baseKey)) return null;
        ItemBuilder item = ItemBuilder.fromString(cf.cf().getString(baseKey + ".material"));

        item.setAmount(cf.cf().getInt(baseKey + ".amount"));
        if (cf.cf().contains(baseKey + ".name"))
            item.setDisplayName(cf.cf().getString(baseKey + ".name"));
        if (cf.cf().contains(baseKey + ".lore"))
            item.setLore(cf.cf().getStringList(baseKey + ".lore"));
        if (cf.cf().contains(baseKey + ".enchantments"))
            cf.cf().getConfigurationSection(baseKey + ".enchantments").getKeys(false)
                    .forEach(
                            enchantment ->
                                    item.addEnchantment(
                                            Enchantment.getByName(enchantment),
                                            cf.cf().getInt(baseKey + ".enchantments." + enchantment)
                                    )
                    );
        if (cf.cf().contains(baseKey + ".flags"))
            item.addItemFlags(
                    cf.cf().getStringList(baseKey + ".flags").stream().map(ItemFlag::valueOf).toArray(ItemFlag[]::new)
            );
        if (cf.cf().contains(baseKey + ".data"))
            item.setDurability((byte) cf.cf().getInt(baseKey + ".data"));
        return item;
    }

    /**
     * Save an {@link ItemBuilder} to a yaml configuration section
     *
     * @param cf      The configuration section to save to
     * @param item    The item to save
     * @param baseKey The base key to save the item under
     * @since 1.19.0
     */
    public static void saveItem(@NotNull ConfigurationSection cf, @NotNull ItemBuilder item, @NotNull String baseKey) {
        cf.set(baseKey + ".material", item.getMaterial().name());
        cf.set(baseKey + ".amount", item.getAmount());
        if (item.getDisplayName() != null)
            cf.set(baseKey + ".name", item.getDisplayName());
        if (item.getLore() != null && !item.getLore().isEmpty())
            cf.set(baseKey + ".lore", item.getLore());
        if (item.getEnchantments() != null && !item.getEnchantments().isEmpty())
            cf.set(baseKey + ".enchantments", item.getEnchantments());
        if (item.getItemFlags() != null && !item.getItemFlags().isEmpty())
            cf.set(baseKey + ".flags", item.getItemFlags().stream().map(ItemFlag::name).collect(Collectors.toList()));
        if (item.getData() != 0)
            cf.set(baseKey + ".data", item.getData());
    }

    /**
     * Load an {@link ItemBuilder} from a yaml configuration section
     *
     * @param cf      The configuration section to load from
     * @param baseKey The base key to load the item from
     * @return The loaded item
     * @since 1.19.0
     */
    public static ItemBuilder loadItem(@NotNull ConfigurationSection cf, @NotNull String baseKey) {
        if (!cf.isSet(baseKey)) return null;
        ItemBuilder item = ItemBuilder.fromString(cf.getString(baseKey + ".material"));

        item.setAmount(cf.getInt(baseKey + ".amount"));
        if (cf.contains(baseKey + ".name"))
            item.setDisplayName(cf.getString(baseKey + ".name"));
        if (cf.contains(baseKey + ".lore"))
            item.setLore(cf.getStringList(baseKey + ".lore"));
        if (cf.contains(baseKey + ".enchantments"))
            cf.getConfigurationSection(baseKey + ".enchantments").getKeys(false)
                    .forEach(
                            enchantment ->
                                    item.addEnchantment(
                                            Enchantment.getByName(enchantment),
                                            cf.getInt(baseKey + ".enchantments." + enchantment)
                                    )
                    );
        if (cf.contains(baseKey + ".flags"))
            item.addItemFlags(
                    cf.getStringList(baseKey + ".flags").stream().map(ItemFlag::valueOf).toArray(ItemFlag[]::new)
            );
        if (cf.contains(baseKey + ".data"))
            item.setDurability((byte) cf.getInt(baseKey + ".data"));
        return item;
    }
}
