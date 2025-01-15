package dk.tohjuler.mcutils.flags;

import lombok.Getter;

@Getter
public class ValueFlag<T> extends Flag {
    public ValueFlag(String fullName, String shortAlias, String description) {
        super(fullName, shortAlias, description);
    }

    private T value;

    @Override
    @SuppressWarnings("unchecked")
    public boolean parse(String str) {
        if (enabled) return false;
        if (!str.startsWith(fullName) && !str.startsWith(shortAlias)) return false;

        String val = "";
        if (str.contains("="))
            val = str.split("=")[1];
        else if (str.contains(" "))
            val = str.split(" ")[1];

        if (val.isEmpty()) return false;

        try {
            value = (T) val;
        } catch (ClassCastException e) {
            new IllegalArgumentException("Invalid value for flag " + getFullName() + ": " + val, e).printStackTrace();
        }
        enabled = true;
        return true;
    }
}
