package dk.tohjuler.mcutils.gui.utils;

import dk.tohjuler.mcutils.gui.ChestPatterns;
import dk.tohjuler.mcutils.gui.items.Item;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SlotParser {
    public static List<Integer> parseSlotString(String str) {
        return parseSlotString(str, null);
    }

    public static List<Integer> parseSlotString(String str, @Nullable Item<?, ?> item) {
        return parseSlotString(str, item, null);
    }

    public static List<Integer> parseSlotString(String str, @Nullable Item<?, ?> item, @Nullable Supplier<Integer> getItemAmount) {
        if (str.startsWith("auto") && item != null && item.getAsList() != null) {
            int rows = str.substring(4).isEmpty() ? 1 : Integer.parseInt(str.substring(5, str.length() - 1));

            if (item.getAsList().getList() == null && getItemAmount == null) {
                new RuntimeException("Item as list is null. Gui: " + item.getGuiConfig().getId() + " Item: " + item.getId()).printStackTrace();
                return Collections.singletonList(-1);
            }
            str = ChestPatterns.getStringPattern(rows, getItemAmount != null ? getItemAmount.get() : item.getAsList().getList().size());
        }

        if (str.contains("-") && str.contains(",")) {
            List<Integer> slots = new ArrayList<>();
            for (String s : str.split(",")) {
                if (s.contains("-"))
                    slots.addAll(parseRange(s));
                else
                    try {
                        slots.add(Integer.parseInt(s));
                    } catch (Exception ignored) {
                    }
            }
            return slots;
        } else if (str.contains("-") && !str.startsWith("-"))
            return parseRange(str);
        else if (str.contains(","))
            return parseList(str);
        else
            try {
                return Collections.singletonList(Integer.parseInt(str));
            } catch (Exception ignored) {
            }

        new RuntimeException("Invalid slot string: " + str).printStackTrace();
        return Collections.singletonList(0);
    }

    private static List<Integer> parseList(String str) {
        String[] split = str.split(",");
        List<Integer> slots = new ArrayList<>();
        for (String s : split) {
            try {
                slots.add(Integer.parseInt(s));
            } catch (Exception ignored) {
            }
        }
        return slots;
    }

    private static List<Integer> parseRange(String str) {
        String[] split = str.split("-");
        if (split.length != 2) return Collections.emptyList();
        try {
            int start = Integer.parseInt(split[0]);
            int end = Integer.parseInt(split[1]);
            List<Integer> slots = new ArrayList<>();
            for (int i = start; i <= end; i++)
                slots.add(i);
            return slots;
        } catch (Exception ignored) {
        }
        return Collections.emptyList();
    }
}
