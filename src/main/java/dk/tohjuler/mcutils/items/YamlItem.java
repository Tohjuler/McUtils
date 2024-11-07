package dk.tohjuler.mcutils.items;

import com.cryptomorin.xseries.XEnchantment;
import dk.tohjuler.mcutils.config.ConfigurationFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static dk.tohjuler.mcutils.data.ConfigUtils.*;

public class YamlItem {
    /**
     * Save an {@link ItemBuilder} to a yaml configuration file
     *
     * @param cf      The configuration file to save to
     * @param item    The item to save
     * @param baseKey The base key to save the item under
     */
    public static void saveItem(@NotNull ConfigurationFile cf, @NotNull ItemBuilder item, @NotNull String baseKey) {
        saveItem(cf.cf(), item, baseKey);
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
     * Load an {@link ItemBuilder} from a yaml configuration file
     *
     * @param cf      The configuration file to load from
     * @param baseKey The base key to load the item from
     * @return The loaded item
     */
    public static ItemBuilder loadItem(@NotNull ConfigurationFile cf, @NotNull String baseKey) {
        return loadItem(cf.cf(), baseKey);
    }

    /**
     * Load an {@link ItemBuilder} from a yaml configuration section
     *
     * @param cf      The configuration section to load from
     * @param baseKey The base key to load the item from
     * @return The loaded item
     */
    public static ItemBuilder loadItem(@NotNull ConfigurationSection cf, @NotNull String baseKey) {
        if (!cf.isSet(baseKey)) return null;
        ItemBuilder item = ItemBuilder.fromString(
                get(cf, "", "STONE", "material", "type", "mat")
        );

        item.setAmount(get(cf, "", 1, "amount", "count"));
        ifPresent(cf, "", item::setDisplayName, "name", "displayname", "display-name");
        ifPresent(cf, "", (Consumer<List<String>>) item::setLore, "lore");
        if (cf.contains(baseKey + ".enchantments"))
            cf.getConfigurationSection(baseKey + ".enchantments").getKeys(false)
                    .forEach(
                            enchantment ->
                                    item.addEnchantment(
                                            XEnchantment.matchXEnchantment(enchantment)
                                                    .orElse(XEnchantment.AQUA_AFFINITY)
                                                    .getEnchant()
                                            ,
                                            cf.getInt(baseKey + ".enchantments." + enchantment)
                                    )
                    );
        ifPresentList(cf, "", (Consumer<List<String>>) flags -> item.addItemFlags(
                flags.stream().map(ItemFlag::valueOf).toArray(ItemFlag[]::new)
        ), "flags", "itemflags", "item-flags");
        ifPresent(cf, "", item::setDurability, "data", "durability");
        return item;
    }
}
