package dk.tohjuler.mcutils.gui.types;

import dev.triumphteam.gui.guis.Gui;
import dk.tohjuler.mcutils.enums.FillType;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.gui.utils.Storage;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.strings.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A gui that is based on a config.
 * Using the {@link Storage} class to store data and the {@link Gui} class to create the gui.
 */
public abstract class ConfigBasedGui extends ConfigBasedGuiBase<Gui, Storage> {
    /**
     * Create a new ConfigBasedGui.
     * <br/>
     *
     * @param id       The id of the gui.
     * @param title    The title of the gui.
     * @param rows     The number of rows in the gui.
     * @param fillType The fill type of the gui.
     * @param fillItem The fill item of the gui.
     */
    public ConfigBasedGui(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem) {
        super(id, title, rows, fillType, fillItem);
    }

    /**
     * Create a new ConfigBasedGui.
     * <br/>
     *
     * @param id       The id of the gui.
     * @param title    The title of the gui.
     * @param rows     The number of rows in the gui.
     * @param fillType The fill type of the gui.
     * @param fillItem The fill item of the gui.
     * @param category The category of the gui.
     */
    public ConfigBasedGui(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem, String category) {
        super(id, title, rows, fillType, fillItem, category);
    }

    @Override
    protected Gui createGui(Player p, Storage storage) {
        return Gui.gui()
                .title(Component.text(
                        ColorUtils.colorize(
                                getTitle(p, storage)
                        )
                ))
                .rows(rows)
                .create();
    }

    @Override
    protected Storage createStorage(@Nullable Storage parent) {
        if (parent == null) return new Storage();
        return new Storage(parent);
    }
}
