package dk.tohjuler.mcutils.kami.expressions.math;

import dk.tohjuler.mcutils.kami.KamiExp;
import dk.tohjuler.mcutils.kami.KamiResult;
import dk.tohjuler.mcutils.kami.KamiState;
import dk.tohjuler.mcutils.kami.errors.KamiError;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndOrExp extends KamiExp {
    public AndOrExp() {
        super(
                Pattern.compile("(true|false)\\s(&&|\\|\\|)\\s(true|false)"),
                "(true|false) <&& | ||> (true|false)",
                "And, Or expression",
                Priority.LAST,
                "Allow for the use of && and ||",
                "&&: and - true if both are true",
                "||: or - true if one is true",
                "Example:",
                " true && true : true",
                " true || false : true"
        );
    }

    @Override
    public @NotNull KamiResult execute(KamiState state, KamiResult result, Matcher matcher) {
        boolean value1 = Boolean.parseBoolean(matcher.group(1));
        String operator = matcher.group(2);
        boolean value2 = Boolean.parseBoolean(matcher.group(3));

        switch (operator) {
            case "&&":
                return result.success(String.valueOf(value1 && value2));
            case "||":
                return result.success(String.valueOf(value1 || value2));
            default:
                return result.error(new KamiError("Operator not supported: " + operator));
        }
    }
}
