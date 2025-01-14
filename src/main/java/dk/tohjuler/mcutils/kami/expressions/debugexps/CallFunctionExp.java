package dk.tohjuler.mcutils.kami.expressions.debugexps;

import dk.tohjuler.mcutils.kami.KamiExp;
import dk.tohjuler.mcutils.kami.KamiResult;
import dk.tohjuler.mcutils.kami.KamiState;
import dk.tohjuler.mcutils.kami.KamiUtils;
import dk.tohjuler.mcutils.kami.handlers.defaults.FunctionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallFunctionExp extends KamiExp {
    public CallFunctionExp() {
        super(
                Pattern.compile("([a-z_][\\w$]*)\\(([^)]*)\\)"),
                "<function name>([params])",
                "Call Kami Function",
                Priority.MEDIUM,
                "Calls a Kami function with the given parameters.",
                "Kami functions are defined functions, meaning they are not java methods.",
                "Validation is applied to the function name, so if the exp is not executed, the function is maybe invalid.",
                "Not all types will be printed, so use '.toString()' if you want to print the result.",
                "Examples:",
                "  test() : Calls the function test"
        );
    }

    @Override
    public @NotNull KamiResult execute(KamiState state, KamiResult result, Matcher matcher) {
        String functionName = matcher.group(1);
        String params = matcher.group(2);

        FunctionHandler functionHandler = state.getHandler(
                FunctionHandler.class,
                () -> new FunctionHandler(new ArrayList<>())
        );

        FunctionHandler.Func<?> func = functionHandler.getFunction(functionName);
        if (func == null) return result.skip();

        Object res = func.run(!params.isEmpty() ? Arrays.stream(params.split(",")).map(state::parseObject).toArray() : new Object[0]);

        if (res == null) return result.success();
        if (KamiUtils.PRINT_TYPES.contains(res.getClass())) return result.success(res.toString());

        return result.successWithObj(res);
    }
}
