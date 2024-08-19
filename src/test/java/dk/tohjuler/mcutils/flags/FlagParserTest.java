package dk.tohjuler.mcutils.flags;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlagParserTest {

    @Test
    public void testParser1() {
        new FlagParser()
                .newFlag(
                        "test",
                        "t",
                        "Used to test"
                )
                .parse(
                        "-t Hi there",
                        (str, flags) -> {
                            assertEquals("Hi there", str);
                            assertTrue(flags.get("t").isEnabled());
                        }
                );
    }

    @Test
    public void testParserSingle() {
        Flag testFlag = new Flag(
                "test",
                "t",
                "Used to test"
        );

        String res = FlagParser.singleFlag(testFlag, "-t Hi there");
        assertEquals("Hi there", res);
        assertTrue(testFlag.isEnabled());

        res = FlagParser.singleFlag(testFlag, "--test Just a test");
        assertEquals("Just a test", res);
        assertTrue(testFlag.isEnabled());
    }

}