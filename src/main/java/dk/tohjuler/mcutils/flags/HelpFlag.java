package dk.tohjuler.mcutils.flags;

public class HelpFlag extends Flag{
    public HelpFlag() {
        super(
                "help",
                "h",
                "Prints this help message"
        );
    }

    public String getHelpMessage(Flag[] flags) {
        StringBuilder sb = new StringBuilder();
        sb.append("Flags:\n");
        for (Flag flag : flags) {
            sb.append("  --")
                    .append(flag.getFullName())
                    .append(" (-")
                    .append(flag.getShortAlias())
                    .append(") - ")
                    .append(flag.getDescription())
                    .append("\n");
        }
        return sb.toString();
    }
}
