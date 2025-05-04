package dk.tohjuler.mcutils.placeholder.impl;

import dk.tohjuler.mcutils.placeholder.IClassPlaceholder;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder implementation that allows the use of fields from an object as placeholders.
 */
public class PHObjectRef implements IClassPlaceholder<Object> {

    @Override
    public String apply(Object obj, String input, @Nullable OfflinePlayer player) {
        // pattern: %<instance simple classname>.<field>%
        String res = input;
        for (Map.Entry<String, String> entry : generateFieldsMap(obj).entrySet()) {
            String placeholder = "%" + obj.getClass().getSimpleName() + "." + entry.getKey() + "%";
            res = res.replace(placeholder, entry.getValue());
        }

        return res;
    }

    private Map<String, String> generateFieldsMap(Object obj) {
        Map<String, String> fields = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                fields.put(field.getName(), field.get(obj).toString());
            } catch (IllegalAccessException ignored) {
            }
        }

        return fields;
    }
}
