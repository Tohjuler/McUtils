package dk.tohjuler.mcutils.gui.utils;

import com.google.gson.*;
import dk.tohjuler.mcutils.gui.items.Item;
import dk.tohjuler.mcutils.items.ItemBuilder;

import java.lang.reflect.Type;

public class ItemAdapter implements JsonSerializer<Item<?, ?>>, JsonDeserializer<Item<?, ?>> {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(ItemBuilder.class, new ItemBuilderAdapter()).create();

    @Override
    public JsonElement serialize(Item<?, ?> item, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject ob = new JsonObject();
        ob.addProperty("id", item.getId());
        ob.addProperty("slot", item.getSlot());
        if (item.getStringMaterial() != null)
            ob.addProperty("stringMaterial", item.getStringMaterial());
        ob.add("item", gson.toJsonTree(item.getItem()));
        if (item.getFallbackItem() != null)
            ob.add("fallbackItem", gson.toJsonTree(item.getFallbackItem()));

        return ob;
    }

    @Override
    public Item<?, ?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject ob = json.getAsJsonObject();
        Item<?, ?> item = new Item<>(null, ob.get("id").getAsString(), ob.get("slot").getAsInt(), gson.fromJson(ob.get("item"), ItemBuilder.class));
        if (ob.has("stringMaterial"))
            item.setStringMaterial(ob.get("stringMaterial").getAsString());
        if (ob.has("fallbackItem"))
            item.setFallbackItem(gson.fromJson(ob.get("fallbackItem"), ItemBuilder.class));

        return item;
    }
}
