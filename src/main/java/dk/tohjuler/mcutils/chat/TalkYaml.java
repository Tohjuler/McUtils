package dk.tohjuler.mcutils.chat;

import com.cryptomorin.xseries.XSound;
import dk.tohjuler.mcutils.data.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;

/**
 * Utility to load a Talk from a YAML file
 */
public class TalkYaml {

    /**
     * Load a Talk from a YAML file
     * Supports only talks in choices
     * <br>
     *
     * @param talkManager The talk manager
     * @param cf          The YAML configuration
     * @return The loaded talk
     */
    public static Talk load(TalkManager talkManager, ConfigurationSection cf) {

        Talk talk = talkManager.createTalk();

        // Messages
        talk.message(
                ConfigUtils.getList(cf, "", Collections.singletonList("MESSAGES NOT FOUND"), "messages", "message", "msg")
        );

        // Delay
        int[] delay = parseDelay(cf);
        talk.msgDelay(delay[0], delay[1]);

        // Sound
        talk.msgSound(
                XSound.matchXSound(
                        ConfigUtils.get(cf, "", "ENTITY_EXPERIENCE_ORB_PICKUP", "sound", "msg-sound", "message-sound")
                ).orElse(XSound.ENTITY_EXPERIENCE_ORB_PICKUP)
        );

        // defaultChoiceFormat
        talk.defaultChoiceFormat(
                ConfigUtils.get(cf, "", "&8(&a&n%choice%&8) ", "default-choice-format", "default-format", "choice-format", "format")
        );

        // defaultChoiceTimeout
        talk.defaultChoiceTimeout(
                ConfigUtils.get(cf, "", "60s", "default-choice-timeout", "default-timeout", "choice-timeout", "timeout")
        );

        // Randomize messages
        talk.random(
                ConfigUtils.get(cf, "", false, "randomize-messages", "randomize", "shuffle")
        );

        // Prefix and suffix
        talk.prefix(ConfigUtils.get(cf, "", "", "prefix"));
        talk.suffix(ConfigUtils.get(cf, "", "", "suffix"));

        // Replacers
        if (cf.isSet("replacers")) {
            cf.getConfigurationSection("replacers").getKeys(false).forEach(replace -> {
                talk.replacer(
                        replace,
                        cf.getString("replacers." + replace)
                );
            });
        }

        // Choices
        if (cf.isSet("choices")) {
            cf.getConfigurationSection("choices").getKeys(false).forEach(choice -> {
                talk.choice(
                        choice,
                        ConfigUtils.get(cf, "choices." + choice + ".", "HOVER TEXT NOT FOUND", "hover", "hover-text"),
                        ch ->
                                ch.talk(talk2 ->
                                        load(talkManager, cf.getConfigurationSection("choices." + choice))
                                                .useSettingsFrom(talk2)
                                )
                );
            });
        }

        return talk;
    }

    private static int[] parseDelay(ConfigurationSection cf) {
        if (cf.isInt("delay"))
            return new int[]{cf.getInt("delay"), cf.getInt("delay")};

        if (cf.isSet("delay.min") && cf.isSet("delay.max"))
            return new int[]{cf.getInt("delay.min"), cf.getInt("delay.max")};
        else if (cf.isSet("delay.min"))
            return new int[]{cf.getInt("delay.min"), cf.getInt("delay.min")};
        else if (cf.isSet("delay.max"))
            return new int[]{cf.getInt("delay.max"), cf.getInt("delay.max")};

        return new int[]{0, 0};
    }

}
