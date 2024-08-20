package dk.tohjuler.mcutils.flags;

import lombok.Getter;

@Getter
public class ValueFlag<T> extends Flag {
    public ValueFlag(String fullName, String shortAlias, String description) {
        super(fullName, shortAlias, description);
    }

    private T value;

    @Override
    public void parse(String str) {
        super.parse(str);
        if (!enabled) return;

        String val = str.substring(str.indexOf('=') + 1);
        if (val.isEmpty()) return;

        try {
            value = (T) val;
        } catch (ClassCastException e) {
            new IllegalArgumentException("Invalid value for flag " + getFullName() + ": " + val, e).printStackTrace();
        }
    }
}
