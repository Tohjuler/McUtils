package dk.tohjuler.mcutils.kami.expressions;

import dk.tohjuler.mcutils.kami.KamiExp;
import dk.tohjuler.mcutils.kami.KamiResult;
import dk.tohjuler.mcutils.kami.KamiState;
import dk.tohjuler.mcutils.kami.KamiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToStringExp extends KamiExp {
    public ToStringExp() {
        super(
                // Matches everything, so it always gets executed.
                // It handles all the validation internal.
                Pattern.compile("(.*?)"),
                "",
                "To String",
                Priority.LAST,
                "Converts all objects to their string representation."
        );
    }

    @Override
    public @NotNull KamiResult execute(KamiState state, KamiResult result, Matcher matcher) {
        String input = state.getCurrentStr();

        Map<String, String> replace = new HashMap<>();

        // Objects
        find(input, KamiUtils.OBJECT_REF_PATTERN)
                .forEach(ref -> {
                    Object obj = state.getObjFromRef(ref);
                    if (obj != null) {
                        state.writeDebug("Found object: " + ref + " -> " + obj);
                        replace.put(ref, state.getTypeHandler().serialize(obj));
                    }
                });

        // Global vars
        find(input, "#[a-zA-Z0-9_]*")
                .forEach(var -> {
                    Object obj = state.getGlobalStorage().get("var:" + var.substring(1)).get();
                    if (obj != null) {
                        state.writeDebug("Found global var: " + var + " -> " + obj);
                        replace.put(var, obj.toString());
                    }
                });

        // Local vars
        find(input, "_[a-zA-Z0-9_]*")
                .forEach(var -> {
                    Object obj = state.getLocalStorage().get(var.substring(1)).get();
                    if (obj != null) {
                        state.writeDebug("Found local var: " + var + " -> " + obj);
                        replace.put(var, obj.toString());
                    }
                });

        return result.successAndAlter(str -> {
            for (Map.Entry<String, String> entry : replace.entrySet())
                str = str.replace(entry.getKey(), entry.getValue());

            return str;
        });
    }

    private List<String> find(String str, String pattern) {
        return find(str, Pattern.compile(pattern));
    }

    private List<String> find(String str, Pattern pattern) {
        List<String> res = new ArrayList<>();
        Matcher matcher = pattern.matcher(str);

        while (matcher.find())
            res.add(matcher.group());

        return res;
    }
}
