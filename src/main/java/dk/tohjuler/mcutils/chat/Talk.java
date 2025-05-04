package dk.tohjuler.mcutils.chat;

import com.cryptomorin.xseries.XSound;
import dk.tohjuler.mcutils.math.TimeUtils;
import dk.tohjuler.mcutils.strings.ColorUtils;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("UnusedReturnValue")
@Getter
public class Talk {
    private final TalkManager talkManager;

    private final List<String> messages = new ArrayList<>();
    private final Map<String, Choice> choices = new HashMap<>();
    private Consumer<Player> endAction;

    private final Map<String, String> replacers = new HashMap<>();
    private XSound msgSound = XSound.ENTITY_VILLAGER_YES;

    private String defaultChoiceFormat = "&8(&a&n%choice%&8) ";
    private String defaultChoiceTimeout = "60s";
    private int msgDelayMin = 0;
    private int msgDelayMax = 0;

    private boolean randomMessage = false;

    public Talk(TalkManager talkManager) {
        this.talkManager = talkManager;
        replacers.put("%prefix%", "");
        replacers.put("%suffix%", "");
    }

    /**
     * Adds a message to the talk.
     * Messages are sent before the choices.
     * <br>
     *
     * @param message The message to add.
     * @return The talk object.
     */
    public Talk msg(String message) {
        messages.add("%prefix%" + message + "%suffix%");
        return this;
    }

    /**
     * Adds a message to the talk.
     * Messages are sent before the choices.
     * <br>
     *
     * @param message The message to add.
     * @return The talk object.
     */
    public Talk message(String... message) {
        for (String msg : message)
            msg(msg);
        return this;
    }

    /**
     * Adds a message to the talk.
     * Messages are sent before the choices.
     * <br>
     *
     * @param message The message to add.
     * @return The talk object.
     */
    public Talk message(List<String> message) {
        for (String msg : message)
            msg(msg);
        return this;
    }

    /**
     * Adds multiple messages to the talk.
     * Only one message is sent, chosen randomly.
     * <br>
     *
     * @param messages The messages to add.
     * @return The talk object.
     */
    public Talk randomMessage(String... messages) {
        this.randomMessage = true;
        for (String message : messages)
            msg(message);
        return this;
    }

    /**
     * Make the talk choose a random message to send.
     * <br>
     *
     * @return The talk object.
     */
    public Talk random() {
        this.randomMessage = true;
        return this;
    }

    /**
     * Make the talk choose a random message to send.
     * <br>
     *
     * @param random Whether to choose a random message.
     * @return The talk object.
     */
    public Talk random(boolean random) {
        this.randomMessage = random;
        return this;
    }

    /**
     * Adds a choice to the talk.
     * Choices are displayed after the messages.
     * <br>
     *
     * @param name      The name of the choice.
     * @param hoverText The text to display when hovering over the choice.
     * @param choice    The choice object.
     * @return The talk object.
     */
    public Talk choice(String name, String hoverText, Function<Choice, Choice> choice) {
        choices.put(
                name.toLowerCase(),
                choice.apply(new Choice(talkManager, this, name, hoverText))
        );
        return this;
    }

    /**
     * Add an action to run when the talk ends.
     * <br>
     *
     * @param action The action to run.
     * @return The talk object.
     */
    public Talk endAction(Consumer<Player> action) {
        this.endAction = action;
        return this;
    }

    /**
     * Change the default choice format.
     * <br>
     *
     * @param format The new default choice format.
     * @return The talk object.
     */
    public Talk defaultChoiceFormat(String format) {
        this.defaultChoiceFormat = format;
        return this;
    }

    /**
     * Change the default choice timeout.
     * The default choice timeout is "60s".
     * <br>
     *
     * @param timeout The new default choice timeout.
     * @return The talk object.
     */
    public Talk defaultChoiceTimeout(String timeout) {
        this.defaultChoiceTimeout = timeout;
        return this;
    }

    /**
     * Set the delay between messages.
     * <br>
     *
     * @param delay The delay between messages.
     * @return The talk object.
     */
    public Talk msgDelay(int delay) {
        this.msgDelayMin = delay;
        this.msgDelayMax = delay;
        return this;
    }

    /**
     * Set the delay between messages, with a random delay between min and max.
     * <br>
     *
     * @param min The minimum delay between messages.
     * @param max The maximum delay between messages.
     * @return The talk object.
     */
    public Talk msgDelay(int min, int max) {
        this.msgDelayMin = min;
        this.msgDelayMax = max;
        return this;
    }

    /**
     * Add a replacer to the talk.
     * Replacers are used to replace placeholders in messages.
     * <br>
     *
     * @param key   The key to replace.
     * @param value The value to replace the key with.
     * @return The talk object.
     */
    public Talk replacer(String key, String value) {
        replacers.put(key, value);
        return this;
    }

    /**
     * Add multiple replacers to the talk.
     * Replacers are used to replace placeholders in messages.
     * <br>
     *
     * @param replacers The replacers to add.
     * @return The talk object.
     * @throws IllegalArgumentException If the replacers are not in pairs.
     */
    public Talk replacers(String... replacers) {
        if (replacers.length % 2 != 0) throw new IllegalArgumentException("Replacers must be in pairs");

        for (int i = 0; i < replacers.length; i += 2)
            replacer(replacers[i], replacers[i + 1]);
        return this;
    }

    /**
     * Add a prefix to all messages.
     * <br>
     *
     * @param prefix The prefix to add.
     * @return The talk object.
     */
    public Talk prefix(String prefix) {
        return replacer("%prefix%", prefix);
    }

    /**
     * Add a suffix to all messages.
     * <br>
     *
     * @param suffix The suffix to add.
     * @return The talk object.
     */
    public Talk suffix(String suffix) {
        return replacer("%suffix%", suffix);
    }

    /**
     * Set the sound to play when a message is sent.
     * <br>
     *
     * @param sound The sound to play.
     * @return The talk object.
     */
    public Talk msgSound(XSound sound) {
        this.msgSound = sound;
        return this;
    }

    /**
     * Create a child talk.
     * A child talk is a talk that uses the settings from the parent talk.
     * <br>
     *
     * @return The child talk.
     */
    public Talk child() {
        return new Talk(talkManager)
                .useSettingsFrom(this);
    }

    /**
     * Sends the talk to a player.
     *
     * @param player The player to send the talk to.
     */
    public void send(Player player) {
        if (randomMessage)
            handleMessage(player, (int) (Math.random() * messages.size()), true);
        else
            handleMessage(player, 0, false);
    }

    private void handleMessage(Player player, int index, boolean runOnce) {
        if (index >= messages.size()) return;

        Bukkit.getScheduler().runTaskLater(talkManager.getPlugin(), () -> {
            player.sendMessage(messages.get(index));
            if (msgSound != null) msgSound.play(player);

            if (index == messages.size() - 1) {
                TextComponent comp = new TextComponent();
                for (Choice choice : choices.values())
                    comp.addExtra(choice.getComponent(player, defaultChoiceFormat, defaultChoiceTimeout, endAction));

                player.spigot().sendMessage(comp);
                if (this.choices.isEmpty() && endAction != null)
                    endAction.accept(player);
            }

            if (runOnce) return;
            handleMessage(player, index + 1, false);
        }, msgDelayMin == msgDelayMax ? msgDelayMin : (int) (Math.random() * (msgDelayMax - msgDelayMin) + msgDelayMin));
    }

    /**
     * Use the settings from another talk.
     * <br>
     *
     * @param settingsTalk The talk to use the settings from.
     * @return The talk object.
     */
    public Talk useSettingsFrom(Talk settingsTalk) {
        replacers.putAll(settingsTalk.replacers);
        msgSound = settingsTalk.msgSound;
        msgDelayMin = settingsTalk.msgDelayMin;
        msgDelayMax = settingsTalk.msgDelayMax;
        defaultChoiceFormat = settingsTalk.defaultChoiceFormat;
        defaultChoiceTimeout = settingsTalk.defaultChoiceTimeout;
        return this;
    }

    @Getter
    public static class Choice {
        private final TalkManager talkManager;
        private final String name;
        private final Talk parent;

        private @Nullable Talk talk;
        private Consumer<Player> action;

        private final String hoverText;
        private String customFormat;
        private String customTimeout;

        public Choice(TalkManager talkManager, Talk parent, String name, String hoverText) {
            this.talkManager = talkManager;
            this.name = name;
            this.hoverText = hoverText;
            this.parent = parent;
        }

        /**
         * Set the action for the choice, to open a new talk.
         * <br>
         *
         * @param setupTalk The setup function for the new talk.
         * @return The choice object.
         */
        public Choice talk(Function<Talk, Talk> setupTalk) {
            this.talk = setupTalk.apply(
                    new Talk(talkManager)
                            .useSettingsFrom(parent)
            );
            return this;
        }

        /**
         * Set the action for the choice, to run a callback.
         * <br>
         *
         * @param action The callback to run.
         * @return The choice object.
         */
        public Choice action(Consumer<Player> action) {
            this.action = action;
            return this;
        }

        /**
         * Change the format for the choice.
         * <br>
         *
         * @param format The new format for the choice.
         * @return The choice object.
         */
        public Choice format(String format) {
            this.customFormat = format;
            return this;
        }

        /**
         * Change the timeout for the choice.
         * <br>
         *
         * @param timeout The new timeout for the choice.
         * @return The choice object.
         */
        public Choice timeout(String timeout) {
            this.customTimeout = timeout;
            return this;
        }

        /**
         * Get the component for the choice.
         * This is an internal method, and should not be used.
         * <br>
         *
         * @param player  The player to get the component for.
         * @param format  The format for the choice.
         * @param timeout The timeout for the choice.
         * @param endAction The action to run when the choice ends.
         * @return The component for the choice.
         */
        public TextComponent getComponent(Player player, String format, String timeout, Consumer<Player> endAction) {
            if (customFormat != null) format = customFormat;
            format = ColorUtils.colorize(format);

            if (talk == null) action = (p) -> {
                action.accept(player);
                endAction.accept(player);
            };

            TextComponent comp = new TextComponent(format.split("%choice%")[0]);
            TextComponent choice = new TextComponent(ColorUtils.colorize(name));
            if (hoverText != null)
                choice.setHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[]{
                                new TextComponent(ColorUtils.colorize(hoverText))
                        }
                ));

            // Set up the choice
            String id = talkManager.runChoice(() -> run(player), TimeUtils.formattedTimeToMillis(timeout));
            choice.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + talkManager.getCommand() + " " + id));

            comp.addExtra(choice);
            comp.addExtra(ColorUtils.colorize(format.split("%choice%")[1]));
            return comp;
        }

        /**
         * Run the choice.
         * This is an internal method, and should not be used.
         * <br>
         *
         * @param player The player to run the choice for.
         */
        public void run(Player player) {
            if (talk != null)
                talk.send(player);

            if (action != null)
                action.accept(player);
        }
    }

}
