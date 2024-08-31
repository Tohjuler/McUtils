package dk.tohjuler.mcutils.flags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestParser extends FlagParser {
    private final Flag testFlag = new Flag(
            "test",
            "t",
            "Used to test"
    );

    @Override
    public void onParsed(String parsed) {
        assertEquals("", parsed);
        assertTrue(testFlag.isEnabled());
    }
}
