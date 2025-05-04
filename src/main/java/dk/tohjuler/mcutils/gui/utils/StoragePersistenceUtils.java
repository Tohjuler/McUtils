package dk.tohjuler.mcutils.gui.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/// Utils for handling storage persistence, in IStorage implementations.
@SuppressWarnings({"DuplicatedCode", "CallToPrintStackTrace"})
public class StoragePersistenceUtils {

    /**
     * Get all fields with the StoragePersistent annotation.
     * The key is the field name or the target name if specified in the annotation.
     * The value is the field value.
     * <br>
     *
     * @param storage The storage to get the fields from.
     * @return The map of fields.
     */
    public static Map<String, Object> getPersistentFields(IStorage storage) {
        List<Field> fields = Arrays.stream(storage.getClass()
                        .getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(StoragePersistent.class))
                .collect(Collectors.toList());

        // Check parent class if not IStorage
        if (storage.getClass().getSuperclass() != IStorage.class)
            fields.addAll(Arrays.stream(storage.getClass()
                            .getSuperclass()
                            .getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(StoragePersistent.class))
                    .collect(Collectors.toList()));

        Map<String, Object> map = new HashMap<>();
        for (Field f : fields) {
            try {
                StoragePersistent sp = f.getAnnotation(StoragePersistent.class);

                f.setAccessible(true);
                assert sp != null : "StoragePersistent annotation is null";
                map.put(sp.target().isEmpty() ? f.getName() : sp.target(), f.get(storage));
            } catch (Exception e) {
                new RuntimeException("Failed to get field value", e).printStackTrace();
            }
        }

        return map;
    }

    /**
     * Set all fields with the StoragePersistent annotation to the values in the map.
     * <br>
     *
     * @param storage The storage to set the fields in.
     * @param map     The map of fields.
     */
    public static void setPersistentFields(IStorage storage, Map<String, Object> map) {
        List<Field> fields = Arrays.stream(storage.getClass()
                        .getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(StoragePersistent.class))
                .collect(Collectors.toList());

        // Check parent class if not IStorage
        if (storage.getClass().getSuperclass() != IStorage.class)
            fields.addAll(Arrays.stream(storage.getClass()
                            .getSuperclass()
                            .getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(StoragePersistent.class))
                    .collect(Collectors.toList()));

        for (Field f : fields) {
            try {
                StoragePersistent sp = f.getAnnotation(StoragePersistent.class);
                assert sp != null : "StoragePersistent annotation is null";
                String name = sp.target().isEmpty() ? f.getName() : sp.target();
                if (!map.containsKey(name)) {
                    continue;
                }

                f.setAccessible(true);
                f.set(storage, map.get(name));
            } catch (Exception e) {
                new RuntimeException("Failed to set field value", e).printStackTrace();
            }
        }
    }

}
