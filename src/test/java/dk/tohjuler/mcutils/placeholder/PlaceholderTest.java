package dk.tohjuler.mcutils.placeholder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlaceholderTest {

    @Test
    public void testSimplePlaceholder() {
        String res = new PlaceholderHandler()
                .replace("%test%", "test value")
                .apply("Hello, %test%!");
        assertEquals("Hello, test value!", res);
    }

    @Test
    public void testObjectRef() {
        String res = new PlaceholderHandler()
                .apply("Hello, %TestObj.name%!", new TestObj("test"));
        assertEquals("Hello, test!", res);
    }

    private static class TestObj {
        private final String name;

        public TestObj(String name) {
            this.name = name;
        }
    }

    @Test
    public void testClassPlaceholder() {
        PlaceholderRegistry.registerPlaceholder(String.class, (instance, input, player) -> input.replace("%str%", instance));

        String res = new PlaceholderHandler()
                .apply("Hello, %str%!", "test value");
        assertEquals("Hello, test value!", res);
    }

    @Test
    public void testClassPlaceholder2() {
        PlaceholderRegistry.registerPlaceholder(
                String.class,
                new ClassPlaceholder<String>()
                        .register("%str.upper%", str -> str.toUpperCase())
                        .register("%str.lower%", str -> str.toLowerCase())
                        .register("%str%", str -> str)
        );

        String res = new PlaceholderHandler()
                .apply("%str% %str.upper% %str.lower%", "test value");
        assertEquals("test value TEST VALUE test value", res);
    }
}
