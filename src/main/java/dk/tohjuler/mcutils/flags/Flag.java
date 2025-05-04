package dk.tohjuler.mcutils.flags;

import lombok.Getter;

@Getter
public class Flag {
    /**
     * Used to describe the flag.
     * Can be used to enable the flag with --
     */
    protected final String fullName;
    /**
     * Used as an alias for the flag.
     * Can be used with the prefix '-'
     */
    protected final String shortAlias;
    protected final String description;

    /**
     * Whether the flag is present in the parsed string
     */
    protected boolean enabled = false;

    public Flag(String fullName, String shortAlias, String description) {
        this.fullName = fullName;
        this.shortAlias = shortAlias;
        this.description = description;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean parse(String str) {
        if (enabled) return false;
        enabled = str.equalsIgnoreCase(fullName) || str.equalsIgnoreCase(shortAlias);
        return enabled;
    }

    public boolean isFlag(String str) {
        return str.equalsIgnoreCase(fullName) || str.equalsIgnoreCase(shortAlias);
    }
}
