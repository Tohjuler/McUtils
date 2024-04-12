package dk.tohjuler.mcutils.chat;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;

public class ChatInput implements Listener {

    private final JavaPlugin plugin;
    @Getter
    @Setter
    private static List<String> cancelWords = new ArrayList<>(Arrays.asList(
            "cancel",
            "exit"
    ));

    public ChatInput(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private static final Map<UUID, ChatInputEvent> inputMap = new HashMap<>();

    /**
     * Add a chat input
     * Remember to handler all messages.
     * There will be no default messages
     * The default cancel words are "cancel" and "exit"
     *
     * @param p The player to add the input to
     * @param message The message to send to the player
     * @param onComplete The consumer to run when the player has completed the input
     * @param onCancel The consumer to run when the player has canceled the input
     */
    public static void add(Player p, String message, Consumer<String> onComplete, Consumer<Player> onCancel) {
        p.closeInventory();

        inputMap.put(p.getUniqueId(), new ChatInputEvent(onComplete, onCancel));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if (!inputMap.containsKey(e.getPlayer().getUniqueId())) return;

        e.setCancelled(true);

        if (e.getMessage().equalsIgnoreCase("cancel")) {
            inputMap.get(e.getPlayer().getUniqueId()).getOnCancel().accept(e.getPlayer());
            inputMap.remove(e.getPlayer().getUniqueId());
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            inputMap.get(e.getPlayer().getUniqueId()).getOnComplete().accept(e.getMessage());
            inputMap.remove(e.getPlayer().getUniqueId());
        });
    }

    @Getter
    private static class ChatInputEvent {
        private final Consumer<String> onComplete;
        private final Consumer<Player> onCancel;

        public ChatInputEvent(Consumer<String> onComplete, Consumer<Player> onCancel) {
            this.onComplete = onComplete;
            this.onCancel = onCancel;
        }
    }
}
