package dk.tohjuler.mcutils.kami.expressions.debugexps;

import dk.tohjuler.mcutils.kami.KamiExp;
import dk.tohjuler.mcutils.kami.KamiResult;
import dk.tohjuler.mcutils.kami.KamiState;
import dk.tohjuler.mcutils.kami.errors.KamiError;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallMethodExp extends KamiExp {
    public CallMethodExp() {
        super(
                Pattern.compile("(([a-z_]\\w*\\.)*[A-Z][\\w$]*)\\.([a-z_][\\w$]*)\\(([^)]*)\\)"),
                "[class path]<method name>([params])",
                "Call Method",
                Priority.MEDIUM,
                "Calls a java method with the given parameters.",
                "Validation is applied to the class path and method, so if the exp is not executed, the class path or method is maybe invalid.",
                "Not all types will be printed, so use '.toString()' if you want to print the result.",
                "Examples:",
                "  java.lang.System.currentTimeMillis() : Calls the method currentTimeMillis in the class System"
        );
    }

    @Override
    public @NotNull KamiResult execute(KamiState state, KamiResult result, Matcher matcher) {
        String classPath = matcher.group(1);
        String methodName = matcher.group(3);
        String params = matcher.group(4);

        // Get the class
        Class<?> clazz;
        try {
            clazz = Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            return result.error(new KamiError("Class not found: " + classPath, e));
        }

        // Get the method
        Method method;
        try {
            method = clazz.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            return result.error(new KamiError("Method not found: " + methodName, e));
        }

        // Execute the method
        Object res;
        try {

            method.setAccessible(true);
            res = method.invoke(null, !params.isEmpty() ? Arrays.stream(params.split(",")).map(state::parseObject).toArray() : new Object[0]);
        } catch (Exception e) {
            return result.error(new KamiError("Failed to execute method: " + methodName, e));
        }
        if (res == null) return result.success();
        if (state.getParser().getPrintTypes().contains(res.getClass())) return result.success(res.toString());

        return result.successWithObj(res);
    }
}
