package dk.tohjuler.mcutils.placeholder;

import lombok.Getter;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A registry for class placeholders.
 */
public class PlaceholderRegistry {
    /// Global instance of the PlaceholderRegistry.
    private static final PlaceholderRegistry global = new PlaceholderRegistry();

    /**
     * Get the global instance of the PlaceholderRegistry.
     * <br/>
     *
     * @return The global instance of the PlaceholderRegistry.
     */
    public static PlaceholderRegistry global() {
        return global;
    }

    @Getter
    private final Map<Class<?>, IClassPlaceholder<?>> registry = new HashMap<>();

    /**
     * Register a placeholder for a class.
     * <br/>
     *
     * @param clazz       The class to register the placeholder for.
     * @param placeholder The placeholder to register.
     * @param <T>         The class type of the placeholder.
     */
    public <T> void registerPlaceholder(Class<T> clazz, IClassPlaceholder<T> placeholder) {
        registry.put(clazz, placeholder);
    }

    /**
     * Unregister a placeholder for a class.
     * <br/>
     *
     * @param clazz The class to unregister the placeholder for.
     */
    public void unregisterPlaceholder(Class<?> clazz) {
        registry.remove(clazz);
    }

    /**
     * Check if a class has a placeholder registered.
     * <br/>
     *
     * @param clazz The class to check.
     * @return True if the class has a placeholder registered, false otherwise.
     */
    public boolean hasPlaceholder(Class<?> clazz) {
        return registry.containsKey(clazz);
    }

    /**
     * Get a placeholder by its class.
     * <br/>
     *
     * @param clazz The class of the placeholder to get.
     * @return The placeholder, or null if it doesn't exist.
     */
    public <T> Optional<IClassPlaceholder<T>> getPlaceholder(Class<T> clazz) {
        try {
            //noinspection unchecked
            return Optional.ofNullable((IClassPlaceholder<T>) registry.get(clazz));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Apply placeholders from a class to an input string.
     * <br/>
     *
     * @param instance A class instance to apply placeholders from.
     * @param input    The input string to apply the placeholders to.
     * @param player   The player to apply the placeholders to, can be null.
     * @return The input string with the placeholders applied, or the input string if no placeholders were found.
     */
    public String applyFromClass(Object instance, String input, @Nullable OfflinePlayer player) {
        if (instance == null) return input;
        Optional<? extends IClassPlaceholder<?>> placeholder = getPlaceholder(instance.getClass());
        if (placeholder.isPresent())
            return placeholder.get().applyFromObj(instance, input, player);

        return input;
    }
}
