package dk.tohjuler.mcutils.gui.types;

import dev.triumphteam.gui.guis.Gui;
import dk.tohjuler.mcutils.enums.FillType;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.gui.utils.IStorage;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.strings.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigGui<S extends IStorage> extends ConfigBasedGuiBase<Gui, S> {
    public ConfigGui(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem) {
        super(id, title, rows, fillType, fillItem);
    }

    public ConfigGui(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem, String category) {
        super(id, title, rows, fillType, fillItem, category);
    }

    @Override
    protected Gui createGui(Player p) {
        return Gui.gui()
                .title(Component.text(
                        ColorUtils.colorize(
                                getTitle(p)
                        )
                ))
                .rows(rows)
                .create();
    }
}
