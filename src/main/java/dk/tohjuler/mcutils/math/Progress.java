package dk.tohjuler.mcutils.math;

public class Progress {

    /**
     * Get a progress bar
     *
     * @param num the current progress
     * @param outOf the total progress
     * @param bar the bar to use (20 characters long)
     * @param noFillColor the color of the bar when not filled
     * @param fillColor the color of the bar when filled
     * @return the progress bar
     */
    public static String getProgress(int num, int outOf, String bar, String noFillColor, String fillColor) {
        assert bar.length() == 20 : "Bar must be 20 characters long";
        if (num < 0) num = 0;
        float percent = (float) num / outOf;
        if (percent >= 1)
            percent = 1;
        int n = (int) Math.floor(percent * 20);
        String first = bar.substring(0, n);
        String last = bar.substring(n, 20);
        return fillColor + (first.isEmpty() ? "" : first) + noFillColor + (last.isEmpty() ? "" : last);
    }

    /**
     * Get a progress bar
     * This method uses the default bar "▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌"
     * and the default colors "§8" and "§a"
     *
     * @param num the current progress
     * @param outOf the total progress
     * @return the progress bar
     */
     public static String getProgress(int num, int outOf) {
        return getProgress(num, outOf, "▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌▌", "§8", "§a");
     }

    /**
     * Get the current progress in percent
     *
     * @param num the current progress
     * @param outOf the total progress
     * @return the progress in percent
     */
    public static int getProcent(int num, int outOf) {
        if (num < 0) num = 0;
        float percent = (float) num / outOf;
        if (percent >= 1)
            percent = 1;
        return (int) Math.floor(percent * 100);
    }
}
