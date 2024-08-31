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

/**
 * A paginated gui that is based on a config.
 * Using the {@link Storage} class to store data and the {@link PaginatedGui} class to create the gui.
 */
public abstract class ConfigBasedPaginatedGui extends ConfigBasedGuiBase<PaginatedGui, Storage> {
    private final int pageSize;

    /**
     * Create a new ConfigBasedPaginatedGui.
     * <br/>
     *
     * @param id       The id of the gui.
     * @param title    The title of the gui.
     * @param rows     The amount of rows in the gui.
     * @param fillType The fill type of the gui.
     * @param fillItem The fill item of the gui.
     * @param pageSize The amount of items per page.
     */
    public ConfigBasedPaginatedGui(String id, @NotNull String title, int rows, @NotNull FillType fillType, ItemBuilder fillItem, int pageSize) {
        super(id, title, rows, fillType, fillItem);
        this.pageSize = pageSize;
    }

    /**
     * Create a new ConfigBasedPaginatedGui.
     * <br/>
     *
     * @param id       The id of the gui.
     * @param title    The title of the gui.
     * @param rows     The amount of rows in the gui.
     * @param fillType The fill type of the gui.
     * @param fillItem The fill item of the gui.
     * @param category The category of the gui.
     * @param pageSize The amount of items per page.
     */
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
    protected Storage createStorage(@Nullable Storage parent) {
        if (parent == null) return new Storage();
        return new Storage(parent);
    }
}
