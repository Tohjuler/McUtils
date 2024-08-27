package dk.tohjuler.mcutils.gui.types;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import dk.tohjuler.mcutils.enums.FillType;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.gui.utils.Storage;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.strings.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigBasedPaginatedGui extends ConfigBasedGuiBase<PaginatedGui, Storage> {
    private final int pageSize;

    public ConfigBasedPaginatedGui(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem, int pageSize) {
        super(id, title, rows, fillType, fillItem);
        this.pageSize = pageSize;
    }

    public ConfigBasedPaginatedGui(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem, String category, int pageSize) {
        super(id, title, rows, fillType, fillItem, category);
        this.pageSize = pageSize;
    }

    @Override
    protected PaginatedGui createGui(Player p, Storage storage) {
        return Gui.paginated()
                .title(Component.text(
                        ColorUtils.colorize(
                                getTitle(p, storage)
                        )
                ))
                .pageSize(pageSize)
                .rows(rows)
                .create();
    }

    @Override
    protected PaginatedGui createGui(Player p) {
        return null;
    }

    @Override
    protected Storage createStorage(@Nullable Storage parent) {
        if (parent == null) return new Storage();
        return new Storage(parent);
    }
}
