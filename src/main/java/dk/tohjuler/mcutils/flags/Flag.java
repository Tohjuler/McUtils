package dk.tohjuler.mcutils.flags;

import lombok.Getter;

@Getter
public class Flag {
    /**
     * Used to describe the flag.
     * Can be used to enable the flag with --
     */
    private final String fullName;
    /**
     * Used as a alias for the flag.
     * Can be used with the prefix -
     */
    private final String shortAlias;
    private final String description;

    /**
     * Whether the flag is present in the parsed string
     */
    private boolean enabled = false;

    public Flag(String fullName, String shortAlias, String description) {
        this.fullName = fullName;
        this.shortAlias = shortAlias;
        this.description = description;
    }

    public void parse(String str) {
        if (enabled) return;
        enabled = str.equalsIgnoreCase(fullName) || str.equalsIgnoreCase(shortAlias);
    }
}
