package dk.tohjuler.mcutils.gui;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dk.tohjuler.mcutils.items.ItemBuilder;
import dk.tohjuler.mcutils.strings.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class StaticGui {
    protected BaseGui gui;

    public StaticGui(String title, int rows, XMaterial filler) {
        gui = Gui.gui()
                .title(Component.text(ColorUtils.colorize(title)))
                .rows(rows)
                .create();

        defaultClickStop();
        fillTop(filler.parseItem());
        fillBottom(filler.parseItem());
    }

    public StaticGui(String title, int rows, XMaterial top, XMaterial bottom) {
        gui = Gui.gui()
                .title(Component.text(ColorUtils.colorize(title)))
                .rows(rows)
                .create();

        defaultClickStop();
        fillTop(top.parseItem());
        fillBottom(bottom.parseItem());
    }

    public StaticGui(String title, int rows, ItemStack filler) {
        gui = Gui.gui()
                .title(Component.text(ColorUtils.colorize(title)))
                .rows(rows)
                .create();

        defaultClickStop();
        fillTop(filler);
        fillBottom(filler);
    }

    public StaticGui() {
    }

    protected void createPaginated(String title, int pageSize, int rows, XMaterial top, XMaterial bottom) {
        gui = Gui.paginated()
                .title(Component.text(ColorUtils.colorize(title)))
                .rows(rows)
                .pageSize(pageSize)
                .create();

        defaultClickStop();
        fillTop(top.parseItem());
        fillBottom(bottom.parseItem());
    }

    protected void fillTop(ItemStack filler) {
        gui.getFiller().fillTop(new ItemBuilder(filler).setDisplayName(" ").buildAsGuiItem());
    }

    protected void fillBottom(ItemStack filler) {
        gui.getFiller().fillBottom(new ItemBuilder(filler).setDisplayName(" ").buildAsGuiItem());
    }

    protected void defaultClickStop() {
        gui.setDefaultClickAction(e -> e.setCancelled(true));
    }

    public abstract void open(Player p);
}
