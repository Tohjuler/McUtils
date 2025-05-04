package dk.tohjuler.mcutils.flags;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@SuppressWarnings("CallToPrintStackTrace")
public abstract class FlagParser {
    protected final Map<String, Flag> flags = new HashMap<>();

    /**
     * Create a new flag
     * <br/>
     *
     * @param fullName    The full name of the flag
     * @param shortAlias  The short alias of the flag
     * @param description The description of the flag
     * @return The parser
     * @since 1.20.0
     */
    public FlagParser newFlag(String fullName, String shortAlias, String description) {
        flags.put(shortAlias, new Flag(fullName, shortAlias, description));
        return this;
    }

    /**
     * Add a flag to the parser
     * <br/>
     *
     * @param flags The flags to add
     * @return The parser
     * @since 1.20.0
     */
    public FlagParser addFlag(Flag... flags) {
        for (Flag flag : flags)
            this.flags.put(flag.getShortAlias(), flag);
        return this;
    }

    private static final List<Class<?>> FLAG_CLASSES = Arrays.asList(Flag.class, ValueFlag.class, HelpFlag.class);

    /**
     * Get all flags declared in the class
     * <br/>
     *
     * @return A list of all flags
     * @since 1.20.0
     */
    public List<Flag> getFlags() {
        return Arrays.stream(getClass().getDeclaredFields())
                .filter(field -> FLAG_CLASSES.stream().anyMatch(c -> c.isAssignableFrom(field.getType())))
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        return (Flag) f.get(this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Parse the string
     * <br/>
     *
     * @param toParse The string to parse
     * @return The string without the flags
     * @since 1.20.0
     */
    public String parse(String toParse) {
        List<Flag> flags = new ArrayList<>(getFlags());
        flags.addAll(this.flags.values());

        List<String> foundFlags = new ArrayList<>();
        StringBuilder flag = new StringBuilder();
        char lastChar = ' ';
        StringBuilder flagPrefix = new StringBuilder();
        for (char c : toParse.toCharArray()) {
            if (c == ' ' && flag.length() > 0) {
                boolean isValueFlag = false;
                for (Flag f : flags) {
                    if (f.isFlag(flag.toString())) {
                        isValueFlag = f instanceof ValueFlag;
                        break;
                    }
                }
                if (!isValueFlag || flag.toString().contains(" ")) {
                    flags.forEach(f -> f.parse(flag.toString()));
                    foundFlags.add(flagPrefix.toString() + flag);
                    flag.delete(0, flag.length()); // Clear the flag
                    lastChar = ' ';
                    flagPrefix = new StringBuilder();
                    continue;
                }
            }
            if (c == '-' && lastChar == '-') {
                flagPrefix.append("-");
                continue;
            }
            if (lastChar == '-' || flag.length() > 0)
                flag.append(c);

            if (c == '-' && flag.length() == 0) flagPrefix = new StringBuilder("-");
            lastChar = c;
        }
        if (flag.length() > 0) {
            foundFlags.add(flagPrefix.toString() + flag);
            flags.forEach(f -> f.parse(flag.toString()));
        }

        for (String f : foundFlags) {
            toParse = toParse.replace(f, "");
        }

        onParsed(toParse.trim());
        return toParse.trim();
    }

    /**
     * Called when the string has been parsed
     * <br/>
     *
     * @param parsed The parsed string
     * @since 1.20.0
     */
    public abstract void onParsed(String parsed);

    // Static methods
    // ---

    /**
     * Create a new parser
     * <br/>
     *
     * @param callback The callback to call when the string has been parsed
     * @return The parser
     * @since 1.20.0
     */
    public static FlagParser newParser(BiConsumer<String, Map<String, Flag>> callback) {
        return new FlagParser() {
            @Override
            public void onParsed(String parsed) {
                callback.accept(parsed, flags);
            }
        };
    }

    /**
     * Parse a string with a single flag
     * <br/>
     *
     * @param flag    The flag to parse
     * @param toParse The string to parse
     * @return The string without the flag
     */
    public static String singleFlag(Flag flag, String toParse) {
        return new FlagParser() {
            @Override
            public void onParsed(String parsed) {

            }
        }.addFlag(flag)
                .parse(toParse);
    }
}
