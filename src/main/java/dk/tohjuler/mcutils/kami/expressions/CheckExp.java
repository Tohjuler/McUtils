package dk.tohjuler.mcutils.kami.expressions;

import dk.tohjuler.mcutils.kami.KamiExp;
import dk.tohjuler.mcutils.kami.KamiResult;
import dk.tohjuler.mcutils.kami.KamiState;
import dk.tohjuler.mcutils.kami.errors.KamiError;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckExp extends KamiExp {
    public CheckExp() {
        super(
                Pattern.compile("(\\S+)\\s(==|!=|<|>|<=|>=)\\s(\\S+)"),
                "<value 1> <operator> <value 2>",
                "Checks 2 values with an operator",
                Priority.LOW,
                "Checks 2 values with the operators: ==, !=, <, >, <=, >=",
                "Example:",
                " 5 == 5 : true",
                " 5 != 5 : false",
                " 10 > 5 : true"
        );
    }

    @Override
    public @NotNull KamiResult execute(KamiState state, KamiResult result, Matcher matcher) {
        String value1 = matcher.group(1);
        String operator = matcher.group(2);
        String value2 = matcher.group(3);

        Object obj1 = state.parseObject(value1, value1);
        Object obj2 = state.parseObject(value2, value2);

        if (operator.equals("=="))
            return result.success(String.valueOf(obj1.equals(obj2)));
        else if (operator.equals("!="))
            return result.success(String.valueOf(!obj1.equals(obj2)));

        if (!(obj1 instanceof Number) || !(obj2 instanceof Number))
            return result.error(new KamiError("Both values must be numbers for operator: " + operator));

        double num1 = ((Number) obj1).doubleValue();
        double num2 = ((Number) obj2).doubleValue();

        switch (operator) {
            case "<":
                return result.success(String.valueOf(num1 < num2));
            case ">":
                return result.success(String.valueOf(num1 > num2));
            case "<=":
                return result.success(String.valueOf(num1 <= num2));
            case ">=":
                return result.success(String.valueOf(num1 >= num2));

            default:
                return result.error(new KamiError("Operator not supported: " + operator));
        }
    }
}
