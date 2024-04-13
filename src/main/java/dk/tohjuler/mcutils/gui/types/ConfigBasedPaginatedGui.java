package dk.tohjuler.mcutils.gui.types;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import dk.tohjuler.mcutils.enums.FillType;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.strings.ColorUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigBasedPaginatedGui extends ConfigBasedGuiBase<PaginatedGui> {
    private final int pageSize;

    public ConfigBasedPaginatedGui(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem, int pageSize) {
        super(id, title, rows, fillType, fillItem);
        this.pageSize = pageSize;
    }

    @Override
    protected PaginatedGui createGui() {
        return Gui.paginated()
                .title(Component.text(
                        ColorUtils.colorize(
                                getTitle()
                        )
                ))
                .pageSize(pageSize)
                .rows(getRows())
                .create();
    }
}
