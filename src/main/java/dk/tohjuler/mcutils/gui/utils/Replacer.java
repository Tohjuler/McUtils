package dk.tohjuler.mcutils.gui.utils;

import dk.tohjuler.mcutils.gui.Storage;
import dk.tohjuler.mcutils.items.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Replacer {
    private Storage storage;

    private @Nullable ItemBuilder item;
    private @Nullable String str;

    @Getter @Setter
    private @Nullable Player player;

    /**
     * Replace a string in the lore and display name
     * <p>
     *
     * @param regex The string to replace
     * @param func  The function to replace the string with
     */
    protected void replace(String regex, Function<ReplaceEvent, String> func) {
        if (item != null)
            item.replaceAllFromGui(regex, storage, func);
        else if (str != null) {
            str = replaceInString(str, regex, storage, func);
        }
    }

    public abstract void replace(Player p);

    /**
     * DON'T USE THIS METHOD
     * It is only used internally
     * <p>
     *
     * @since 1.5.0
     */
    public void replaceCall(Storage storage, ItemBuilder item, Player p) {
        this.storage = storage;
        this.item = item;
        replace(p);
    }

    /**
     * DON'T USE THIS METHOD
     * It is only used internally
     * <p>
     *
     * @since 1.5.0
     */
    public void replaceCall(Storage storage, String str, Player p) {
        this.storage = storage;
        this.str = str;
        replace(p);
    }

    public static String replaceInString(String str, String regex, Storage storage, Function<ReplaceEvent, String> func) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (!matcher.find()) return str;
        String matchedPart = matcher.group();
        String res = func.apply(new ReplaceEvent(matchedPart, storage));
        if (res == null) res = "REPLACE_ERROR";

        return str.replace(matchedPart, res);
    }

    @Getter
    public static class ReplaceEvent {
        private final String matchedPart;
        private final Storage storage;

        public ReplaceEvent(String matchedPart, Storage storage) {
            this.matchedPart = matchedPart;
            this.storage = storage;
        }
    }
}
