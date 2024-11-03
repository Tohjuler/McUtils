package dk.tohjuler.mcutils.plays.steps;

import dk.tohjuler.mcutils.Init;
import dk.tohjuler.mcutils.chat.TalkYaml;
import dk.tohjuler.mcutils.gui.utils.Storage;
import dk.tohjuler.mcutils.plays.Play;
import dk.tohjuler.mcutils.plays.PlayManager;
import dk.tohjuler.mcutils.plays.PlayStep;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class TalkStep extends PlayStep<Player> {
    public TalkStep(PlayManager playManager) {
        super(playManager, "default.talk");
    }

    @Override
    public void play(Play currentPlay, Player context, Storage storage, ConfigurationSection params, Runnable next) {
        TalkYaml.load(
                        Init.getTalkManager(),
                        params
                )
                .endAction((p) -> next.run())
                .send(context);
    }

    @Override
    public boolean canBePlayed(Play currentPlay, Object context) {
        return context instanceof Player;
    }
}
