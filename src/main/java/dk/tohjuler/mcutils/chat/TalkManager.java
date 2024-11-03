package dk.tohjuler.mcutils.chat;

import dk.tohjuler.mcutils.strings.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * A class for managing talks.
 * Talks are a way to create interactive chat messages.
 * Where the player can choose between different options.
 * It can be used for NPC dialogues, quests, etc.
 */
public class TalkManager {
    @Getter
    private final JavaPlugin plugin;
    @Getter
    private final String command = StringUtils.randomString(8);

    private final Map<String, RunningChoice> runningChoices = new HashMap<>();

    private CommandMap commandMap;
    private final Command choiceHandlerCommand = new Command(command) {
        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (!(sender instanceof Player)) return false;

            if (args.length != 1) return false;
            String id = args[0];
            RunningChoice choice = runningChoices.get(id);
            if (choice == null) return false;
            if (System.currentTimeMillis() > choice.getExpire()) {
                runningChoices.remove(id);
                return false;
            }

            choice.getRunnable().run();
            runningChoices.remove(id);
            return true;
        }
    };

    public TalkManager(JavaPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> runningChoices.entrySet()
                .removeIf(entry -> System.currentTimeMillis() > entry.getValue().getExpire()
                ), 0, 20 * 60);

        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getServer());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        register();
    }

    /**
     * Creates a new talk.
     * <br>
     * @return The created talk.
     */
    public Talk createTalk() {
        return new Talk(this);
    }

    private void register() {
        commandMap.register(plugin.getName(), choiceHandlerCommand);
    }

    /**
     * Unregister the choice handler command.
     */
    public void unregister() {
        commandMap.getCommand(choiceHandlerCommand.getName()).unregister(commandMap);
    }

    /**
     * Run a choice.
     * This is an internal method, and should not be used.
     * <br>
     * @param runnable The runnable to run.
     * @param expireIn The time in milliseconds before the choice expires.
     * @return The id of the choice.
     */
    public String runChoice(Runnable runnable, long expireIn) {
        String id = StringUtils.randomString(8);
        runningChoices.put(id, new RunningChoice(System.currentTimeMillis() + expireIn, runnable));
        return id;
    }

    @Getter
    private static class RunningChoice {
        private final long expire;
        private final Runnable runnable;

        public RunningChoice(long expire, Runnable runnable) {
            this.expire = expire;
            this.runnable = runnable;
        }
    }
}
