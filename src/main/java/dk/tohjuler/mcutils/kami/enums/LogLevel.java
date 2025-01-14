package dk.tohjuler.mcutils.kami.enums;

public enum LogLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR;

    /**
     * Checks if the level is at least the provided level.
     * <br>
     *
     * @param level The level to check.
     * @return If the level is at least the provided level.
     */
    public boolean isAtLeast(LogLevel level) {
        return this.compareTo(level) >= 0;
    }
}
