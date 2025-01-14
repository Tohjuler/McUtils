package dk.tohjuler.mcutils.kami.errors;

import dk.tohjuler.mcutils.kami.KamiExp;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Kami error.
 */
@Getter
public class KamiError {
    /**
     * The expression that caused the error.
     */
    @Setter
    private @Nullable KamiExp exp;
    /**
     * The message of the error.
     * Should be user-friendly, keep technical details in debug details or exception.
     */
    private final String message;

    /**
     * Debug details.
     */
    private final Map<String, String> debugDetails = new HashMap<>();

    /**
     * The exception that caused the error.
     * This should be used for technical details.
     * If not provided, a new exception will be created.
     * Used for stack trace.
     */
    private final @Nullable Exception exception;

    /**
     * Creates a new Kami error.
     * <br>
     *
     * @param exp       The expression that caused the error.
     * @param message   The message of the error.
     * @param exception The exception that caused the error.
     */
    public KamiError(@Nullable KamiExp exp, String message, @Nullable Exception exception) {
        this.exp = exp;
        this.message = message;
        this.exception = exception;
    }

    /**
     * Creates a new Kami error.
     * <br>
     *
     * @param exp     The expression that caused the error.
     * @param message The message of the error.
     */
    public KamiError(@Nullable KamiExp exp, String message) {
        this(exp, message, new Exception());
    }

    /**
     * Creates a new Kami error.
     * <br>
     *
     * @param message   The message of the error.
     * @param exception The exception that caused the error.
     */
    public KamiError(String message, @Nullable Exception exception) {
        this(null, message, exception);
    }

    /**
     * Creates a new Kami error.
     * <br>
     *
     * @param message The message of the error.
     */
    public KamiError(String message) {
        this(null, message, new Exception());
    }

    /**
     * Adds a debug detail.
     * <br>
     *
     * @param key   The key of the detail.
     * @param value The value of the detail.
     * @return The error.
     */
    public KamiError addDebugDetail(String key, String value) {
        debugDetails.put(key, value);
        return this;
    }

    /**
     * Adds debug details.
     * <br>
     *
     * @param details The details to add.
     * @return The error.
     */
    public KamiError addDebugDetail(String... details) {
        if (details.length % 2 != 0) throw new IllegalArgumentException("Details must be in pairs.");

        for (int i = 0; i < details.length; i += 2)
            debugDetails.put(details[i], details[i + 1]);
        return this;
    }

    // ---

    public void printToConsole() {
        Logger logger = Bukkit.getLogger();

        logger.log(Level.SEVERE, "Kami error: " + message);
        if (exp != null) logger.log(Level.SEVERE, "Expression: " + exp.getName());
        if (!debugDetails.isEmpty()) {
            logger.log(Level.SEVERE, "Debug details:");
            debugDetails.forEach((key, value) -> logger.log(Level.SEVERE, key + ": " + value));
        }
        if (exception != null) {
            logger.log(Level.SEVERE, "Exception:");
            exception.printStackTrace();
        }
    }
}
