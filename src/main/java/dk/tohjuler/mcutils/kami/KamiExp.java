package dk.tohjuler.mcutils.kami;

import dk.tohjuler.mcutils.kami.errors.KamiPanicError;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a Kami expression.
 */
@Getter
@ToString
public abstract class KamiExp {
    /**
     * The pattern to detect this expression.
     */
    private final Pattern pattern;

    /**
     * The expression in a readable format.
     * Used for documentation.
     */
    private final String expression;
    /**
     * The name of the expression.
     * Used for documentation and identification.
     */
    private final String name;
    /**
     * The description of the expression.
     */
    private final String description;

    private Priority priority = Priority.MEDIUM;

    public KamiExp(Pattern pattern, String expression, String name, String description) {
        this.pattern = pattern;
        this.expression = expression;
        this.name = name;
        this.description = description;
    }

    public KamiExp(Pattern pattern, String expression, String name, String description, Priority priority) {
        this.pattern = pattern;
        this.expression = expression;
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public KamiExp(Pattern pattern, String expression, String name, Priority priority, String... description) {
        this.pattern = pattern;
        this.expression = expression;
        this.name = name;
        this.priority = priority;
        this.description = String.join("\n", description);
    }

    /**
     * Executes the expression.
     * <br>
     *
     * @param state   The current state.
     * @param result  The result handler, used to return results.
     * @param matcher The matcher.
     * @return The result.
     */
    public abstract @NotNull KamiResult execute(KamiState state, KamiResult result, Matcher matcher);

    /**
     * Matches the input to the pattern and executes the expression.
     * <br>
     *
     * @param state The current state.
     * @return The panic error, if any.
     */
    @SuppressWarnings("ConstantConditions")
    public @Nullable KamiResult match(KamiState state) {
        state.writeDebug("Matching expression: " + name);
        Matcher matcher = pattern.matcher(state.getCurrentStr());
        if (!matcher.find()) return null;
        state.writeDebug("Matched expression: " + name);

        state.setCurrentExp(this);
        KamiResult res;
        try {
            res = execute(state, new KamiResult(state), matcher);
        } catch (Exception e) {
            state.writeDebug("An error occurred while executing the expression: " + name);
            return new KamiResult(state)
                    .panic(new KamiPanicError(
                            "An error occurred while executing the expression: " + name,
                            e
                    ));
        }
        if (res == null)
            state.writeDebug("Result is null for expression: " + name);
        else
            state.writeDebug("Executed expression: " + name + " with result success:" + res.isSuccess() + ", panic:" + res.isPanic());

        return res != null
                ? res
                : new KamiResult(state)
                .panic(new KamiPanicError("Result is null"));
    }

    @Getter
    public enum Priority {
        /// Used to convert non-readable expressions to readable ones, before outputting.
        LAST(4),
        LOW(3),
        MEDIUM(2),
        HIGH(1);

        private final int value;

        Priority(int value) {
            this.value = value;
        }
    }
}
