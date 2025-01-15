package dk.tohjuler.mcutils.kami;

import dk.tohjuler.mcutils.kami.errors.KamiError;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

/**
 * Represents the result of a Kami expression.
 */
public class KamiResult {
    private final KamiState currentState;

    /**
     * Holds the old expression.
     * Used for debugging.
     */
    @Getter
    private final String oldExp;

    @Getter
    private @Nullable KamiError panicError = null;

    @Getter
    private boolean blocking = false;
    @Getter
    private boolean success = false;

    public KamiResult(KamiState currentState) {
        this.currentState = currentState;
        oldExp = currentState.getCurrentStr();
    }

    // Results
    // ---

    /**
     * Marks the current expression as blocking.
     * This will stop the parsing after the current expression.
     * <br>
     *
     * @return The current result.
     */
    public KamiResult blocking() {
        blocking = true;
        return this;
    }

    // Successes

    /**
     * Marks the current expression as successful.
     * This will remove the current expression from the input.
     * <br>
     *
     * @return The current result.
     */
    public KamiResult success() {
        success = true;
        // Default result handler is boolean
        currentState.setCurrentStr(
                currentState.getCurrentStr().replaceAll(currentState.getCurrentExp().getPattern().pattern(), "")
        );
        return this;
    }

    /**
     * Marks the current expression as successful.
     * This will replace the current expression with the provided result.
     * This is needed for expressions that wrap around other expressions.
     * <br>
     *
     * @param result The result to replace the current expression with.
     * @return The current result.
     */
    public KamiResult success(String result) {
        success = true;
        currentState.setCurrentStr(
                currentState.getCurrentStr().replaceAll(currentState.getCurrentExp().getPattern().pattern(), result)
        );
        return this;
    }

    /**
     * Marks the current expression as successful.
     * This will replace the current expression with the result of the provided function.
     * <br>
     *
     * @param replace The function to replace the current expression with. The input is the current expression.
     * @return The current result.
     */
    public KamiResult success(Function<String, String> replace) {
        success = true;
        currentState.setCurrentStr(
                currentState.getCurrentStr().replaceAll(currentState.getCurrentExp().getPattern().pattern(), replace.apply(currentState.getCurrentStr()))
        );
        return this;
    }

    /**
     * Marks the current expression as successful and alters the current expression.
     * This will set the current expression to the result of the provided function.
     * <br>
     *
     * @param alter The function to set the current expression to.
     * @return The current result.
     */
    public KamiResult successAndAlter(Function<String, String> alter) {
        currentState.setCurrentStr(alter.apply(currentState.getCurrentStr()));
        return success();
    }

    /**
     * Marks the current expression as successful with an object.
     * The object will be stored in the object storage, and the current expression will be replaced with a reference to the object.
     * <br>
     *
     * @param obj The object to store.
     * @return The current result.
     */
    public KamiResult successWithObj(Object obj) {
        UUID uuid = UUID.randomUUID();
        currentState.getObjectStorage().put(uuid, obj);
        return success("obj:{" + uuid + "}");
    }

    // Failures

    /**
     * Skips the current expression.
     * Meaning the current expression will be ignored.
     * This is considered a failure, as the pattern matched, but the expression was skipped.
     * <br>
     * Please only use this if no action was taken.
     * <br>
     *
     * @return The current result.
     */
    public KamiResult skip() {
        return this;
    }

    /**
     * Marks the current expression as failed.
     * This will replace the current expression with the provided result.
     * <br>
     *
     * @param result The result to replace the current expression with.
     * @return The current result.
     */
    public KamiResult failed(String result) {
        // Default result handler is boolean
        currentState.setCurrentStr(
                currentState.getCurrentStr().replaceAll(currentState.getCurrentExp().getPattern().pattern(), result)
        );
        return this;
    }

    /**
     * Marks the current expression as failed.
     * This will replace the current expression with the result of the provided function.
     * <br>
     *
     * @param replace The function to replace the current expression with. The input is the current expression.
     * @return The current result.
     */
    public KamiResult failed(Function<String, String> replace) {
        // Default result handler is boolean
        currentState.setCurrentStr(
                currentState.getCurrentStr().replaceAll(currentState.getCurrentExp().getPattern().pattern(), replace.apply(currentState.getCurrentStr()))
        );
        return this;
    }

    /**
     * Marks the current expression as failed with an error.
     * Alias for {@link KamiResult#error(KamiError)}.
     * <br>
     *
     * @param error The error to fail with.
     * @return The current result.
     */
    public KamiResult failedWithErr(KamiError error) {
        return error(error);
    }

    /**
     * Marks the current expression as failed with an error.
     * <br>
     *
     * @param error The error to fail with.
     * @return The current result.
     */
    public KamiResult error(KamiError error) {
        currentState.error(error);
        return failed("!ERROR!");
    }

    /**
     * Marks the current expression as failed with a message.
     * The message will be wrapped in a new error.
     * <br>
     *
     * @param message The message to fail with.
     * @return The current result.
     */
    public KamiResult panic(String message) {
        panicError = new KamiError(currentState.getCurrentExp(), message);
        return this;
    }

    /**
     * Panic the current expression with an error.
     * This will stop the parsing and return the error.
     * Should only be used for critical errors.
     * <br>
     *
     * @param error The error to panic with.
     * @return The current result.
     */
    public KamiResult panic(KamiError error) {
        panicError = error;
        return this;
    }

    // Utils
    // ---

    /**
     * Checks if the current result is a panic.
     * <br>
     *
     * @return If the current result is a panic.
     */
    public boolean isPanic() {
        return panicError != null;
    }

}
