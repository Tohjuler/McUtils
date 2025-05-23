package dk.tohjuler.mcutils.kami.handlers.defaults;

import dk.tohjuler.mcutils.chat.Message;
import dk.tohjuler.mcutils.kami.enums.LogLevel;
import dk.tohjuler.mcutils.kami.errors.KamiError;
import dk.tohjuler.mcutils.kami.errors.KamiPanicError;
import dk.tohjuler.mcutils.kami.handlers.IOutputHandler;
import dk.tohjuler.mcutils.strings.ColorUtils;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class McOutputHandler implements IOutputHandler {
    private final String PREFIX = "&9&lKAMI &f";

    @Override
    public void out(String message, @Nullable Player p) {
        LogLevel level;
        try {
            level = LogLevel.valueOf(message.split(":", 2)[0]);
        } catch (IllegalArgumentException e) {
            level = LogLevel.INFO;
        }
        message = message.substring(message.indexOf(":") + 1).trim();

        String color;
        switch (level) {
            case WARNING:
                color = "&e";
                break;
            case ERROR:
                color = "&c";
                break;
            case DEBUG:
                color = "&b";
                break;
            default:
                color = "&f";
        }

        Message.sendNoPrefix(
                p,
                PREFIX + color + message
        );
    }

    @Override
    public void err(KamiError error, @Nullable Player p) {
        error.printToConsole();

        if (p == null) return;

        TextComponent msg = new TextComponent(
                ColorUtils.colorize(
                        PREFIX + "&c" +
                                (error instanceof KamiPanicError ? "panic error parser canceled: " : "")
                )
        );

        TextComponent errorComponent = new TextComponent(ColorUtils.colorize(error.getMessage()));
        List<String> hoverMsg = new ArrayList<>();
        hoverMsg.add("&c&lError:");

        hoverMsg.add(" &7Message: &f" + (error.getException().getMessage() != null ? error.getException().getMessage() : "None"));
        hoverMsg.add(" &7Class: &f" + error.getException().getClass().getSimpleName());

        Throwable cause = error.getException().getCause();
        while (cause != null) {
            hoverMsg.add(" &7Cause:");
            hoverMsg.add("  &7Message: &f" + error.getException().getCause().getMessage());
            hoverMsg.add("  &7Class: &f" + error.getException().getCause().getClass().getSimpleName());

            cause = cause.getCause();
        }

        hoverMsg.add(" &7Debug details: &f" + (error.getDebugDetails().isEmpty() ? "None" : ""));
        error.getDebugDetails().forEach((key, value) -> hoverMsg.add("  &7" + key + ": &f" + value));
        if (error.getExp() != null) {
            hoverMsg.add(" ");
            hoverMsg.add("&e&lExpression:");
            hoverMsg.add(" &7Name: &f" + error.getExp().getName());
            hoverMsg.add(" &7Description:");
            for (String line : error.getExp().getDescription().split("\n"))
                hoverMsg.add("  &f" + line);
            hoverMsg.add(" &7Expression: &f" + error.getExp().getExpression());
            hoverMsg.add(" &7Regex: &f" + error.getExp().getPattern().pattern());
        }

        TextComponent errorHover = new TextComponent(ColorUtils.colorize(String.join("\n", hoverMsg)));
        errorComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{errorHover}));

        msg.addExtra(errorComponent);
        p.spigot().sendMessage(msg);
    }
}
