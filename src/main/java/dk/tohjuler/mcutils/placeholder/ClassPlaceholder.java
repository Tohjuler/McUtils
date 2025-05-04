package dk.tohjuler.mcutils.placeholder;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A helper class for creating placeholders for a class.
 * <br/>
 *
 * @param <T> The class type of the placeholder.
 */
public class ClassPlaceholder<T> implements IClassPlaceholder<T> {
    private final Map<String, BiFunction<T, OfflinePlayer, String>> placeholders = new HashMap<>();

    @Override
    public String apply(T instance, String input, @Nullable OfflinePlayer player) {
        return placeholders.entrySet().stream()
                .reduce(input, (acc, entry) ->
                                acc.replaceAll(entry.getKey(), entry.getValue().apply(instance, player)),
                        String::concat
                );
    }

    // Register
    // ---

    /**
     * Register a placeholder with a function that takes the instance of the class and returns
     * a string.
     * <br/>
     *
     * @param placeholder The placeholder to register, can be a regex.
     * @param function    The function to apply to the instance of the class.
     * @return The ClassPlaceholder instance.
     */
    public ClassPlaceholder<T> register(String placeholder, Function<T, String> function) {
        placeholders.put(placeholder, (ins, op) -> function.apply(ins));
        return this;
    }

    /**
     * Register a placeholder with a function that takes the instance of the class and the player
     * and returns a string.
     * <br/>
     *
     * @param placeholder The placeholder to register, can be a regex.
     * @param function    The function to apply to the instance of the class and the player.
     * @return The ClassPlaceholder instance.
     */
    public ClassPlaceholder<T> register(String placeholder, BiFunction<T, OfflinePlayer, String> function) {
        placeholders.put(placeholder, function);
        return this;
    }
}
