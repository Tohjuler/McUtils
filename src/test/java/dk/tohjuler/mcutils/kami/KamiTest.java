package dk.tohjuler.mcutils.kami;

import dk.tohjuler.mcutils.kami.enums.LogLevel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KamiTest {
    private final KamiParser defaultDebugParser = new KamiBuilder()
            .useDefaultStorage(st -> st.set("logLevel", LogLevel.DEBUG.name()))
            .useDebugExps()
            .build();

    private final KamiParser defaultMathParser = new KamiBuilder()
            .useDefaultStorage(st -> st.set("logLevel", LogLevel.DEBUG.name()))
            .useMathExps()
            .build();

    @Test
    public void testEmpty() {
        KamiParser parser = new KamiBuilder()
                .build();

        assertEquals("", parser.parse("").asString());
    }

    @Test
    public void testNoExps() {
        KamiParser parser = new KamiBuilder()
                .build();

        assertEquals("Hello, World!", parser.parse("Hello, World!").asString());
    }

    @Test
    public void testMethodCall() {
        assertEquals("Hello, World!", defaultDebugParser.parse("dk.tohjuler.mcutils.kami.KamiParser.staticTest()").asString());
    }

    @Test
    public void testMethodCallChain() {
        assertEquals("Hello", defaultDebugParser.parse("dk.tohjuler.mcutils.kami.KamiParser.staticTestList().get(0)").asString());
    }

    @Test
    public void testSetter() {
        assertEquals("", defaultDebugParser.parse("#test = 5").asString());
        assertEquals("5", defaultDebugParser.getGlobalStorage().get("var:test").asString());
    }

    @Test
    public void testFunc() {
        assertEquals("Hello, World!", defaultDebugParser.parse("testFunc()").asString());
    }

    @Test
    public void testToStringExp() {
        defaultDebugParser.getGlobalStorage().set("var:test", "Hello, World!");
        assertEquals("Hello, World!", defaultDebugParser.parse("#test").asString());
    }

    @Test
    public void testCheckExp() {
        assertEquals("true", defaultMathParser.parse("5 == 5").asString());
        assertEquals("false", defaultMathParser.parse("5 != 5").asString());
        assertEquals("true", defaultMathParser.parse("10 > 5").asString());
    }

}
