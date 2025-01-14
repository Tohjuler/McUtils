package dk.tohjuler.mcutils.kami.expressions.debugexps;

import dk.tohjuler.mcutils.kami.KamiExp;
import dk.tohjuler.mcutils.kami.KamiResult;
import dk.tohjuler.mcutils.kami.KamiState;
import dk.tohjuler.mcutils.kami.KamiUtils;
import dk.tohjuler.mcutils.kami.errors.KamiError;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CallMethodFromRefExp extends KamiExp {
    public CallMethodFromRefExp() {
        super(
                Pattern.compile("(obj:\\{\\b[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}\\b})\\.([a-z_][\\w$]*)\\(([^)]*)\\)"),
                "<obj ref>.<method name>([params])",
                "Call method from obj ref",
                "Calls a java method from an object reference with the given parameters.\n" +
                        "This is used internal to allow chained method calls.",
                Priority.LOW
        );
    }

    @Override
    public @NotNull KamiResult execute(KamiState state, KamiResult result, Matcher matcher) {
        String objRef = matcher.group(1);
        String methodName = matcher.group(2);
        String params = matcher.group(3);

        // Get object
        Object obj = state.getObjFromRef(objRef);
        if (obj == null) return result.error(new KamiError("Object not found: " + objRef));

        // Get the method
        Method method = KamiUtils.getMethod(
                obj.getClass(),
                methodName,
                !params.isEmpty() ? Arrays.stream(params.split(",")).map(KamiUtils::determineClass).toArray(Class[]::new) : new Class[0]
        );

        if (method == null)
            return result.error(new KamiError("Method not found: " + methodName + " in " + obj.getClass().getName()));



        // Execute the method
        Object res;
        try {
            if (KamiUtils.hasMethodOverridden(method.getDeclaringClass(), methodName)){
                List<Object> paramsList = Arrays.stream(params.split(",")).map(state::parseObject).collect(Collectors.toList());
                paramsList.add(0, obj);
                res = KamiUtils.runMethodOverride(method.getDeclaringClass(), methodName, paramsList.toArray());
            } else {
                if (!method.isAccessible()) method.setAccessible(true);
                res = method.invoke(obj, Arrays.stream(params.split(",")).map(state::parseObject).toArray());
            }
        } catch (Exception e) {
            return result.error(new KamiError("Failed to execute method: " + methodName, e));
        }
        if (res == null) return result.success();
        if (KamiUtils.PRINT_TYPES.contains(res.getClass())) return result.success(res.toString());

        return result.successWithObj(res);
    }
}
