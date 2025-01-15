package dk.tohjuler.mcutils.flags;

import static org.junit.Assert.*;

public class TestParser2 extends FlagParser {
    private final Flag testFlag = new Flag(
            "test",
            "t",
            "Used to test"
    );

    private final ValueFlag<String> testFlag2 = new ValueFlag<>(
            "valuetest",
            "v",
            "Used to test2"
    );

    @Override
    public void onParsed(String parsed) {
        assertEquals("", parsed);
        assertFalse(testFlag.isEnabled());
        assertEquals("HERE", testFlag2.getValue());
    }
}
