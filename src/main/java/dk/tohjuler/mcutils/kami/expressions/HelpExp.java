package dk.tohjuler.mcutils.kami.expressions;

import dk.tohjuler.mcutils.kami.KamiExp;
import dk.tohjuler.mcutils.kami.KamiResult;
import dk.tohjuler.mcutils.kami.KamiState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelpExp extends KamiExp {
    public HelpExp() {
        super(
                Pattern.compile("!help!"),
                "!help!",
                "Help",
                "Displays alls expressions and their descriptions.",
                Priority.HIGH
        );
    }

    @Override
    public @NotNull KamiResult execute(KamiState state, KamiResult result, Matcher matcher) {
        state.write(
                "&e&lExpressions:"
        );

        for (KamiExp exp : state.getParser().getExpressions()) {
            state.write(
                    " &3" + exp.getName(),
                    " &7Expression: &f" + exp.getExpression(),
                    " &7Description:"
            );
            state.write(Arrays.stream(exp.getDescription().split("\n")).map(s -> "  &f" + s).toArray(String[]::new));
        }

        return result.success();
    }
}
