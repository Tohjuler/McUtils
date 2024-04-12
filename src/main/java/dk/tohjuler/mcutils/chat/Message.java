package dk.tohjuler.mcutils.chat;

import dk.tohjuler.mcutils.strings.ColorUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {

    @Getter
    @Setter
    private static String successPrefix = "&8(&a!&8) &f";
    @Getter
    @Setter
    private static String warnPrefix = "&8(&e!&8) &f";
    @Getter
    @Setter
    private static String adminWarnPrefix = "&8(&b!&8) &f";
    @Getter
    @Setter
    private static String denyPrefix = "&8(&c!&8) &c";

    public static void sendSuccess(Player p, String... messages) {
        for (String s : messages)
            p.sendMessage(ColorUtils.colorize(successPrefix + s));
    }

    public static void sendSuccess(CommandSender sender, String... messages) {
        for (String s : messages)
            sender.sendMessage(ColorUtils.colorize(successPrefix + s));
    }

    public static void sendWarn(Player p, String... messages) {
        for (String s : messages)
            p.sendMessage(ColorUtils.colorize(warnPrefix + s));
    }

    public static void sendWarn(CommandSender sender, String... messages) {
        for (String s : messages)
            sender.sendMessage(ColorUtils.colorize(warnPrefix + s));
    }

    public static void sendAdminWarn(Player p, String... messages) {
        for (String s : messages)
            p.sendMessage(ColorUtils.colorize(adminWarnPrefix + s));
    }

    public static void sendAdminWarn(CommandSender sender, String... messages) {
        for (String s : messages)
            sender.sendMessage(ColorUtils.colorize(adminWarnPrefix + s));
    }

    public static void sendDeny(Player p, String... messages) {
        for (String s : messages)
            p.sendMessage(ColorUtils.colorize(denyPrefix + s));
    }

    public static void sendDeny(CommandSender sender, String... messages) {
        for (String s : messages)
            sender.sendMessage(ColorUtils.colorize(denyPrefix + s));
    }

    public static void sendNoPrefix(Player p, String... messages) {
        for (String s : messages)
            p.sendMessage(s);
    }

    public static void sendNoPrefix(CommandSender sender, String... messages) {
        for (String s : messages)
            sender.sendMessage(s);
    }
}
