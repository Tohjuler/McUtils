package dk.tohjuler.mcutils.gui.utils;

import com.google.gson.*;
import dk.tohjuler.mcutils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ItemBuilderAdapter implements JsonSerializer<ItemBuilder>, JsonDeserializer<ItemBuilder> {
    @Override
    public JsonElement serialize(ItemBuilder itemBuilder, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject ob = new JsonObject();
        ob.addProperty("material", itemBuilder.getMaterial().name());
        ob.addProperty("amount", itemBuilder.getAmount());
        if (itemBuilder.getDisplayName() != null)
            ob.addProperty("name", itemBuilder.getDisplayName());
        if (itemBuilder.getLore() != null && !itemBuilder.getLore().isEmpty()) {
            JsonArray lore = new JsonArray();
            itemBuilder.getLore().forEach(l -> lore.add(new JsonPrimitive(l)));
            ob.add("lore", lore);
        }
        if (itemBuilder.getEnchantments() != null && !itemBuilder.getEnchantments().isEmpty()) {
            JsonObject enchantments = new JsonObject();
            itemBuilder.getEnchantments().forEach((enchantment, level) -> enchantments.addProperty(enchantment.getName(), level));
            ob.add("enchantments", enchantments);
        }
        if (itemBuilder.getItemFlags() != null && !itemBuilder.getItemFlags().isEmpty()) {
            JsonArray flags = new JsonArray();
            itemBuilder.getItemFlags().forEach(f -> flags.add(new JsonPrimitive(f.name())));
            ob.add("flags", flags);
        }
        if (itemBuilder.getData() != 0)
            ob.addProperty("data", itemBuilder.getData());

        return ob;
    }

    @Override
    public ItemBuilder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject ob = json.getAsJsonObject();

        Material material = Material.getMaterial(ob.get("material").getAsString());
        ItemBuilder item = new ItemBuilder(material, ob.get("amount").getAsInt());
        if (ob.has("name"))
            item.setDisplayName(ob.get("name").getAsString());
        if (ob.has("lore")) {
            JsonArray lore = ob.get("lore").getAsJsonArray();
            List<String> loreList = new ArrayList<>();
            lore.forEach(l -> loreList.add(l.getAsString()));
            item.setLore(loreList);
        }
        if (ob.has("enchantments")) {
            JsonObject enchantments = ob.get("enchantments").getAsJsonObject();
            enchantments.entrySet().forEach(e -> item.addEnchantment(Enchantment.getByName(e.getKey()), e.getValue().getAsInt()));
        }
        if (ob.has("flags")) {
            JsonArray flags = ob.get("flags").getAsJsonArray();
            List<ItemFlag> flagList = new ArrayList<>();
            flags.forEach(f -> flagList.add(ItemFlag.valueOf(f.getAsString())));
            item.addItemFlags(flagList.toArray(new ItemFlag[0]));
        }
        if (ob.has("data"))
            item.setDurability(ob.get("data").getAsShort());

        return item;
    }
}
