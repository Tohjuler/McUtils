package dk.tohjuler.mcutils.gui;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A collection of chest slots patterne.
 */
@Getter
enum ChestPatterns {

    // 1 row
    R1_1(1, 1, 22),
    R1_2(1, 2, 21, 23),
    R1_3(1, 3, 20, 22, 24),
    R1_4(1, 4, 19, 21, 23, 25),
    R1_5(1, 5, 20, 21, 22, 23, 24),
    R1_6(1, 6, 19, 20, 21, 23, 24, 25),
    R1_7(1, 7, 19, 20, 21, 22, 23, 24, 25),

    // 2 row
    R2_2(2, 2, 21, 31),
    R2_3(2, 3, 21, 31, 23),
    R2_4(2, 4, 21, 23, 30, 32),
    R2_5(2, 5, 20, 22, 24, 30, 32),
    R2_6(2, 6, 20, 22, 24, 29, 31, 33),
    R2_7(2, 7, 20, 22, 24, 28, 30, 32, 24),
    R2_8(2, 8, 19, 21, 23, 25, 28, 30, 32, 34),
    ;

    private final int rows;
    private final int itemAmount;
    private final List<Integer> slots;

    ChestPatterns(int rows, int itemAmount, int... slots) {
        this.rows = rows;
        this.itemAmount = itemAmount;
        this.slots = Arrays.stream(slots).boxed().collect(Collectors.toList());
    }

    // Static methods

    /**
     * Get a pattern by rows and item amount
     * All patternes will be takes as the gui has 6 rows.
     * <p>
     *
     * @param rows The amount of free rows to use.
     * @param itemAmount The amount of items to use.
     * @return The pattern.
     * @since 1.21.0
     */
    public static List<Integer> getPattern(int rows, int itemAmount) {
        for (ChestPatterns pattern : values())
            if (pattern.getRows() == rows && pattern.getItemAmount() == itemAmount)
                return pattern.getSlots();
        if (rows == 0) return Collections.emptyList();
        return getPattern(rows - 1, itemAmount);
    }

    /**
     * Get a pattern by rows and item amount with an offset.
     * All patternes will be takes as the gui has 6 rows.
     * <p>
     *
     * @param row The amount of free rows to use.
     * @param itemAmount The amount of items to use.
     * @param offset The offset to add to the slots.
     * @return The pattern with the offset.
     * @since 1.21.0
     */
    public static List<Integer> getPattern(int row, int itemAmount, int offset) {
        List<Integer> pattern = getPattern(row, itemAmount);
        return pattern.stream().map(slot -> slot + offset).collect(Collectors.toList());
    }
}
