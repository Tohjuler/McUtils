package dk.tohjuler.mcutils.kami.expressions.debugexps;

import dk.tohjuler.mcutils.kami.KamiExp;
import dk.tohjuler.mcutils.kami.KamiResult;
import dk.tohjuler.mcutils.kami.KamiState;
import dk.tohjuler.mcutils.kami.KamiUtils;
import dk.tohjuler.mcutils.kami.errors.KamiError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetExp extends KamiExp {
    public SetExp() {
        super(
                Pattern.compile("(\\S.+?)\\s*=\\s*(\\S.*)"),
                "(#<variable>|<object>|<java path>) = <value>",
                "Set a variable",
                Priority.LOW,
                "Sets a variable or object to a value.",
                "A variable is a string that starts with a #. Ex. #test",
                "If there is no obj ref or any '.' in the key, it will be treated as a variable.",
                "An object can be a direct reference or a kami object gotten from a previous expression.",
                "Examples:",
                "  #test = 5 : Sets the variable test to 5",
                "  test2 = 5 : Sets the variable test2 to 5",
                "  dk.tohjuler.test.Test.getInstance().cooldown = 10 : Sets the field cooldown in the object Test to 10",
                "  dk.tohjuler.something.Data.staticField = 15 : Sets the static field staticField in the class Data to 15"
        );
    }

    @Override
    public @NotNull KamiResult execute(KamiState state, KamiResult result, Matcher matcher) {
        String key = matcher.group(1).trim();
        String value = matcher.group(2).trim();

        // The key can either be a variable, an object or a java path to a variable.

        boolean isVariable = key.startsWith("#");
        boolean isObject = KamiUtils.OBJECT_REF_PATTERN.matcher(key).matches();
        String objRef = isObject ? key.substring(0, key.indexOf("}") + 1) : null;
        boolean isJavaPath = key.matches("^([a-zA-Z_][a-zA-Z0-9_]*)(\\.[a-zA-Z_][a-zA-Z0-9_]*)*$");

        // No valid key
        if (!isVariable && !isObject && !isJavaPath) return result.skip();

        // Validate object
        if (isObject) {
            if (state.getObjFromRef(objRef) == null)
                return result.error(new KamiError("The object reference is invalid. Object not found."));
            if (!key.replace(objRef, "").contains("."))
                return result.error(new KamiError("There is just a object, no field. Use <object>.<field> = <value>"));
        }

        // Validate java path
        if (isJavaPath && key.contains(".")) {
            String[] split = key.split("\\.");
            String varName = split[split.length - 1];
            String path = key.substring(0, key.length() - varName.length() - 1);

            try {
                Class<?> clazz = Class.forName(path); // Check for class
                clazz.getDeclaredField(varName); // Check for field
            } catch (ClassNotFoundException | NoSuchFieldException e) {
                return result.error(new KamiError("The java path is invalid. Class or variable not found."));
            }
        }

        Object evalValue = evalValue(state, value);
        if (evalValue == null) return result.skip();

        if (isVariable || (isJavaPath && !key.contains("."))) {
            state.getGlobalStorage().set("var:" + (isVariable ? key.substring(1) : key), evalValue);
        } else if (isObject) {
            Object obj = state.getObjFromRef(objRef);
            if (obj == null) return result.error(new KamiError("The object reference is invalid. Object not found."));

            try {
                Field field = obj.getClass().getDeclaredField(key.replace(objRef, ""));
                field.setAccessible(true);

                if (field.getType().isAssignableFrom(evalValue.getClass()))
                    return result.error(new KamiError("The value type does not match the field type."));

                field.set(obj, evalValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                if (e instanceof NoSuchFieldException)
                    return result.error(new KamiError("The field is invalid. Field not found."));
                return result.error(new KamiError("Failed to set field: " + key, e));
            }
        } else {
            String[] split = key.split("\\.");
            String varName = split[split.length - 1];
            String path = key.substring(0, key.length() - varName.length() - 1);

            try {
                Class<?> clazz = Class.forName(path);
                Field field = clazz.getDeclaredField(varName);
                field.setAccessible(true);

                if (field.getType().isAssignableFrom(evalValue.getClass()))
                    return result.error(new KamiError("The value type does not match the field type."));

                field.set(null, evalValue);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                if (e instanceof ClassNotFoundException)
                    return result.error(new KamiError("The java path is invalid. Class not found."));
                if (e instanceof NoSuchFieldException)
                    return result.error(new KamiError("The java path is invalid. Field not found."));
                return result.error(new KamiError("Failed to set field: " + key, e));
            }
        }

        return result.success();
    }

    private @Nullable Object evalValue(KamiState state, String value) {
        if (KamiUtils.OBJECT_REF_PATTERN.matcher(value).matches()) return state.getObjFromRef(value);

        return state.parseObject(value);
    }
}
