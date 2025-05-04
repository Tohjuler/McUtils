package dk.tohjuler.mcutils.kami.errors;

import dk.tohjuler.mcutils.kami.KamiExp;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a panic error.
 * A panic error is critical and means that the parsing was canceled.
 * <br>
 * There is no code difference between a panic error and a regular error.
 * There is only a class difference to make it easier to identify the error.
 */
public class KamiPanicError extends KamiError {
    public KamiPanicError(@Nullable KamiExp exp, String message, Exception exception) {
        super(exp, message, exception);
    }

    public KamiPanicError(String message, Exception exception) {
        super(message, exception);
    }

    public KamiPanicError(String message) {
        super(message);
    }

    public static KamiPanicError from(KamiError error) {
        return new KamiPanicError(error.getExp(), error.getMessage(), error.getException());
    }
}
