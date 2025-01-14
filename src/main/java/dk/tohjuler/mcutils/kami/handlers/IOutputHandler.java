package dk.tohjuler.mcutils.kami.handlers;

import dk.tohjuler.mcutils.kami.errors.KamiError;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an output handler.
 * Used for handling output messages.
 * <br>
 * Output is split into two channels out and err.
 * Out is for normal messages.
 * Err is for errors.
 */
public interface IOutputHandler {

    /**
     * Write a message to the output channel.
     * Messages written to out should always start with the log level in uppercase with ':' afterward.
     * Example: 'INFO: This is an info message.'
     * <br>
     *
     * @param message The message to write.
     * @param player  The player to write for.
     */
    void out(String message, @Nullable Player player);

    /**
     * Write an error to the error channel.
     * <br>
     *
     * @param error  The error to write.
     * @param player The player to write for.
     */
    void err(KamiError error, @Nullable Player player);

}
