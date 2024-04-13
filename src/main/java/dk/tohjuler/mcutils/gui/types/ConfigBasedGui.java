package dk.tohjuler.mcutils.gui.types;

import dev.triumphteam.gui.guis.Gui;
import dk.tohjuler.mcutils.enums.FillType;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.strings.ColorUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigBasedGui extends ConfigBasedGuiBase<Gui> {
    public ConfigBasedGui(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem) {
        super(id, title, rows, fillType, fillItem);
    }

    @Override
    protected Gui createGui() {
        return Gui.gui()
                .title(Component.text(
                        ColorUtils.colorize(
                                getTitle()
                        )
                ))
                .rows(getRows())
                .create();
    }
}
