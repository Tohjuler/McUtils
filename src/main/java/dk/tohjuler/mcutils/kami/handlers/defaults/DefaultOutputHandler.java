package dk.tohjuler.mcutils.kami.handlers.defaults;

import dk.tohjuler.mcutils.kami.errors.KamiError;
import dk.tohjuler.mcutils.kami.handlers.IOutputHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("CallToPrintStackTrace")
public class DefaultOutputHandler implements IOutputHandler {
    @Override
    public void out(String message, @Nullable Player player) {
        System.out.println(message);
    }

    @Override
    public void err(KamiError error, @Nullable Player player) {
        System.err.println("ERROR: " + error.getMessage());
        error.getException().printStackTrace();
    }
}
