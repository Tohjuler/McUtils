package dk.tohjuler.mcutils.gui.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlotParser {
    public static List<Integer> parseSlotString(String str) {
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
