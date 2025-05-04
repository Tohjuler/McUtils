package dk.tohjuler.mcutils.flags;

import static org.junit.Assert.*;

public class TestParser3 extends FlagParser {
    private final Flag testFlag = new Flag(
            "test",
            "t",
            "Used to test"
    );

    private final ValueFlag<String> testFlag2 = new ValueFlag<>(
            "valueTest",
            "v",
            "Used to test2"
    );

    private final HelpFlag helpFlag = new HelpFlag();

    @Override
    public void onParsed(String parsed) {
        assertEquals("", parsed);
        assertFalse(testFlag.isEnabled());
        assertTrue(helpFlag.isEnabled());
        assertEquals("HERE", testFlag2.getValue());
    }
}
