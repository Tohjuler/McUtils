package dk.tohjuler.mcutils.placeholder;

import dk.tohjuler.mcutils.placeholder.impl.PHObjectRef;
import dk.tohjuler.mcutils.placeholder.impl.PlaceholderAPIHelper;
import dk.tohjuler.mcutils.placeholder.impl.SimplePlaceholder;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PlaceholderHandler {
    private final List<IPlaceholder> placeholders;
    private PlaceholderRegistry registry = PlaceholderRegistry.global();

    /**
     * Create a new PlaceholderHandler, with a list of placeholders.
     * <br/>
     *
     * @param placeholders The list of placeholders to add.
     */
    public PlaceholderHandler(List<IPlaceholder> placeholders) {
        this.placeholders = new ArrayList<>(placeholders);
    }

    /**
     * Create a new PlaceholderHandler.
     */
    public PlaceholderHandler() {
        this.placeholders = new ArrayList<>();
    }

    /**
     * Apply the placeholders to the input string.
     * <br/>
     *
     * @param input              The input string to apply the placeholders to.
     * @param placeholderObjects Object to apply placeholders from the class must be a subclass of IPlaceholder or be registered in the registry.
     * @return The input string with the placeholders applied.
     */
    public String apply(String input, Object... placeholderObjects) {
        return apply(input, null, placeholderObjects);
    }

    /**
     * Apply the placeholders to the input string.
     * <br/>
     *
     * @param input              The input string to apply the placeholders to.
     * @param player             The player to apply the placeholders to.
     * @param placeholderObjects Object to apply placeholders from the class must be a subclass of IPlaceholder or be registered in the registry.
     * @return The input string with the placeholders applied.
     */
    public String apply(String input, @Nullable OfflinePlayer player, Object... placeholderObjects) {
        String result = input;
        for (IPlaceholder placeholder : placeholders) {
            try {
                result = placeholder.apply(result, player);
            } catch (Exception e) {
                throw new RuntimeException("Failed to apply placeholder from class " + placeholder.getClass().getSimpleName(), e);
            }
        }

        // Apply class placeholders
        for (Object placeholderObject : placeholderObjects) {
            try {
                if (placeholderObject instanceof IPlaceholder) {
                    IPlaceholder placeholder = (IPlaceholder) placeholderObject;
                    result = placeholder.apply(result, player);
                } else if (registry.hasPlaceholder(placeholderObject.getClass())) {
                    result = registry.applyFromClass(placeholderObject, result, player);
                } else {
                    // No placeholder found, handle as object ref
                    result = new PHObjectRef().apply(placeholderObject, result, player);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to apply placeholder object from class " + placeholderObject.getClass().getSimpleName(), e);
            }
        }

        return result;
    }

    // Placeholder registration methods
    // ---

    /**
     * Register a new placeholder.
     * <br/>
     *
     * @param placeholder The placeholder to register.
     * @return The PlaceholderHandler instance.
     */
    public PlaceholderHandler withPlaceholder(IPlaceholder placeholder) {
        placeholders.add(placeholder);
        return this;
    }

    /**
     * Register a simple placeholder, with a string and a value.
     * <br/>
     *
     * @param placeholder The placeholder to register, can be a regex.
     * @param value       The value to replace the placeholder with.
     * @return The PlaceholderHandler instance.
     */
    public PlaceholderHandler replace(String placeholder, String value) {
        return withPlaceholder(new SimplePlaceholder(placeholder, value));
    }

    /**
     * Register the placeholderAPI helper, which allows the use of PlaceholderAPI placeholders.
     *
     * @return The PlaceholderHandler instance.
     */
    public PlaceholderHandler usePlaceholderAPI() {
        placeholders.add(new PlaceholderAPIHelper());
        return this;
    }

    /**
     * Change the placeholder registry to use a custom one.
     * Default is the global registry.
     * <br/>
     *
     * @param registry The registry to use.
     * @return The PlaceholderHandler instance.
     */
    public PlaceholderHandler usePlaceholderRegistry(@NotNull PlaceholderRegistry registry) {
        this.registry = registry;
        return this;
    }

    // Static methods

    /**
     * Apply the placeholders to the input string, with a list of objects.
     * <br/>
     *
     * @param input              The input string to apply the placeholders to.
     * @param placeholderObjects Object to apply placeholders from the class must be a subclass of IPlaceholder or be registered in the registry.
     * @return The input string with the placeholders applied.
     */
    public static String applyFromObjs(String input, Object... placeholderObjects) {
        return new PlaceholderHandler().apply(input, null, placeholderObjects);
    }

    /**
     * Apply the placeholders to the input string, with a list of objects.
     * <br/>
     *
     * @param input              The input string to apply the placeholders to.
     * @param player             The player to apply the placeholders to.
     * @param placeholderObjects Object to apply placeholders from the class must be a subclass of IPlaceholder or be registered in the registry.
     * @return The input string with the placeholders applied.
     */
    public static String applyFromObjs(String input, OfflinePlayer player, Object... placeholderObjects) {
        return new PlaceholderHandler().apply(input, player, placeholderObjects);
    }
}
