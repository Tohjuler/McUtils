package dk.tohjuler.mcutils.math;

import java.text.DecimalFormat;

public class MoneyUtils {
    /**
     * Format a number to a string with 2 decimal points
     *
     * @param num The number to format
     * @return The formatted number
     */
    public static String formatNum(double num) {
        String s = String.format("%,.2f", num);
        s = s.split("\\.")[0];
        return s.replace(",", ".");
    }

    /**
     * Format a number with an alias
     * Supports K, M, B, T, Q, QT
     *
     * @param value The number to format
     * @return The formatted number
     */
    public static String formatNumAsMoney(float value) {
        String[] arr = {"", "K", "M", "B", "T", "Q", "QT"};
        int index = 0;
        while ((value / 1000) >= 1) {
            value = value / 1000;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s%s", decimalFormat.format(value), arr[index]);
    }
}
