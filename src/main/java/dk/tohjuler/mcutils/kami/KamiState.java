package dk.tohjuler.mcutils.kami;

import dk.tohjuler.mcutils.kami.enums.LogLevel;
import dk.tohjuler.mcutils.kami.errors.KamiError;
import dk.tohjuler.mcutils.kami.handlers.IGlobalStorage;
import dk.tohjuler.mcutils.kami.handlers.IHandler;
import dk.tohjuler.mcutils.kami.storage.KamiStorage;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;

/**
 * A holder for the current step in the execution of the Kami expression.
 */
@Getter
public class KamiState {
    private final KamiParser parser;
    private final @Nullable Player player;

    @Setter
    private KamiExp currentExp;
    @Setter
    private String currentStr;

    private final IGlobalStorage globalStorage;

    /**
     * Local storage for the current state.
     * Used for storing values between expressions.
     * <br>
     * Default keys:
     * - "input" - The input string.
     */
    private final KamiStorage<String> localStorage = new KamiStorage<>();
    private final KamiStorage<UUID> objectStorage = new KamiStorage<>();

    public KamiState(KamiParser parser, @Nullable Player player) {
        this.parser = parser;
        this.player = player;

        this.globalStorage = parser.getGlobalStorage();
    }

    // Output
    // ---

    /**
     * Writes to output handler.
     * If error is not null, it will be written to the error channel.
     * else it will be written to the out channel.
     * <br>
     *
     * @param level   The level of the message.
     * @param message The message to log.
     * @param error   The error to log.
     */
    public void write(LogLevel level, String message, @Nullable KamiError error) {
        LogLevel currentLevel = LogLevel.valueOf(localStorage.get("logLevel").asString("ERROR"));

        if (!level.isAtLeast(currentLevel)) return;

        if (error != null) {
            if (currentExp != null) error.setExp(currentExp);
            parser.getOutputHandler().err(error, player);
            return;
        }

        parser.getOutputHandler().out(level.name() + ": " + message, player);
    }

    /**
     * Writes a message to the output handler.
     * <br>
     *
     * @param messages The messages to write.
     */
    public void write(String... messages) {
        for (String str : messages)
            parser.getOutputHandler().out(str, player);
    }

    /**
     * Write an info message to out channel.
     * Level filter is applied, use {@link #write(String...)} to bypass.
     * <br>
     *
     * @param message The message to log.
     */
    public void writeInfo(String message) {
        write(LogLevel.INFO, message, null);
    }

    /**
     * Write a warning message to out channel.
     * Level filter is applied, use {@link #write(String...)} to bypass.
     * <br>
     *
     * @param message The message to log.
     */
    public void writeWarning(String message) {
        write(LogLevel.WARNING, message, null);
    }

    /**
     * Write a debug message to out channel.
     * Level filter is applied, use {@link #write(String...)} to bypass.
     * <br>
     *
     * @param message The message to log.
     */
    public void writeDebug(String message) {
        write(LogLevel.DEBUG, message, null);
    }

    /**
     * Write an error message to err channel.
     * <br>
     *
     * @param error The error to log.
     */
    public void writeError(KamiError error) {
        write(LogLevel.ERROR, null, error);
    }

    /**
     * Write an error message to err channel.
     * Alias for {@link #writeError(KamiError)}.
     * <br>
     *
     * @param error The error to log.
     */
    public void error(KamiError error) {
        writeError(error);
    }

    // Utils
    // ---

    /**
     * Check if there is an object reference in the string, and return the object if found.
     * <br>
     *
     * @param check The string to check.
     * @return The object if found, else null.
     */
    public @Nullable Object dejectObj(String check) {
        Matcher matcher = KamiUtils.OBJECT_REF_PATTERN.matcher(check);

        if (!matcher.matches()) return null;

        return getObjFromRef(matcher.group());
    }

    /**
     * Get an object from a reference.
     * Reference format: obj:{UUID}
     * <br>
     *
     * @param ref The reference to get the object from.
     * @return The object if found, else null.
     */
    public @Nullable Object getObjFromRef(String ref) {
        UUID uuid = UUID.fromString(ref.substring(5, ref.length() - 1));
        return objectStorage.get(uuid).getPlane();
    }

    /**
     * Get a handler from the parser.
     * <br>
     *
     * @param clazz The class of the handler.
     * @param <T>   The type of the handler.
     * @return The handler if found, else null.
     */
    public @Nullable <T extends IHandler> T getHandler(Class<T> clazz) {
        return parser.getHandlers().stream().filter(clazz::isInstance).map(clazz::cast).findFirst().orElse(null);
    }

    /**
     * Get a handler from the parser.
     * <br>
     *
     * @param clazz    The class of the handler.
     * @param supplier The supplier to create the handler if not found.
     * @param <T>      The type of the handler.
     * @return The handler if found, else null.
     */
    public <T extends IHandler> T getHandler(Class<T> clazz, Supplier<T> supplier) {
        return parser.getHandlers().stream().filter(clazz::isInstance).map(clazz::cast).findFirst().orElse(supplier.get());
    }

    /**
     * Parse a string into an object.
     * Supports:
     * - Object references: obj:{UUID}
     * - Global variables: #{name}
     * - Local variables: _{name}
     * - Types from {@link KamiUtils#parseObject(String)}.
     * If none of the above, it will check for a global variable.
     * <br>
     *
     * @param input The input to parse.
     * @return The parsed object.
     */
    public @Nullable Object parseObject(String input) {
        if (KamiUtils.OBJECT_REF_PATTERN.matcher(input).matches()) return getObjFromRef(input);
        if (input.startsWith("#") && getGlobalStorage().get("var:" + input.substring(1)).isPresent())
            getGlobalStorage().get("var:" + input.substring(1)).get();
        if (input.startsWith("_") && localStorage.get(input.substring(1)).isPresent())
            localStorage.get(input.substring(1)).get();
        Object res = KamiUtils.parseObject(input);
        if (res != null) return res;

        if (getGlobalStorage().get("var:" + input).isPresent()) return getGlobalStorage().get("var:" + input).get();
        return null;
    }

}
