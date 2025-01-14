package dk.tohjuler.mcutils.kami;

import dk.tohjuler.mcutils.kami.expressions.HelpExp;
import dk.tohjuler.mcutils.kami.expressions.ToStringExp;
import dk.tohjuler.mcutils.kami.expressions.debugexps.CallFunctionExp;
import dk.tohjuler.mcutils.kami.expressions.debugexps.CallMethodExp;
import dk.tohjuler.mcutils.kami.expressions.debugexps.CallMethodFromRefExp;
import dk.tohjuler.mcutils.kami.expressions.debugexps.SetExp;
import dk.tohjuler.mcutils.kami.handlers.IGlobalStorage;
import dk.tohjuler.mcutils.kami.handlers.IHandler;
import dk.tohjuler.mcutils.kami.handlers.IOutputHandler;
import dk.tohjuler.mcutils.kami.handlers.defaults.DefaultOutputHandler;
import dk.tohjuler.mcutils.kami.handlers.defaults.EmptyGlobalStorage;
import dk.tohjuler.mcutils.kami.handlers.defaults.MemoryGlobalStorage;
import dk.tohjuler.mcutils.kami.storage.KamiStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a builder for Kami.
 */
public class KamiBuilder {

    private final List<KamiExp> expressions = new ArrayList<>();
    private IOutputHandler outputHandler;
    private IGlobalStorage globalStorage = new MemoryGlobalStorage();

    private @Nullable KamiStorage<String> defaultStorage = null;
    private final List<IHandler> handlers = new ArrayList<>();

    private String globalStorageId = null;

    public KamiBuilder() {
        addExpression(new HelpExp());
    }

    /**
     * Setup a default storage.
     * <br>
     *
     * @param setup The setup for the storage.
     * @return The builder.
     */
    public KamiBuilder useDefaultStorage(Consumer<KamiStorage<String>> setup) {
        defaultStorage = new KamiStorage<>();
        setup.accept(defaultStorage);
        return this;
    }

    /**
     * Add a handler.
     * Handlers are used for custom logic in expressions.
     * <br>
     *
     * @param handler The handler.
     * @return The builder.
     */
    public KamiBuilder use(IHandler handler) {
        handlers.add(handler);
        return this;
    }

    // Expressions
    // ---

    // Defaults

    /**
     * Use the default debug expressions.
     * The default debug expressions are focused for debugging.
     * Allowing you to access variables, methods and classes.
     * To debug your code.
     * <br>
     *
     * @return The builder.
     */
    public KamiBuilder useDebugExps() {
        return addExpression(
                new ToStringExp(),
                new CallMethodExp(),
                new CallMethodFromRefExp(),
                new SetExp(),
                new CallFunctionExp()
        );
    }

    /**
     * Use the default math expressions.
     * The default math expressions are focused for math.
     * Allowing you to do math operations.
     * <br>
     *
     * @return The builder.
     */
    public KamiBuilder useMathExps() {
        throw new UnsupportedOperationException("Not implemented yet");
        // TODO: Implement
        //return this;
    }

    /**
     * Add expression(s).
     * <br>
     *
     * @param exp The expression(s).
     * @return The builder.
     */
    @SuppressWarnings("UnusedReturnValue")
    public KamiBuilder addExpression(KamiExp... exp) {
        expressions.addAll(Arrays.asList(exp));
        return this;
    }

    /**
     * Disable the default help expression.
     * <br>
     *
     * @return The builder.
     */
    public KamiBuilder disableHelpExp() {
        return disableExp(HelpExp.class);
    }

    /**
     * Disable an expression from specific class.
     * <br>
     *
     * @param expClass The expression class.
     * @return The builder.
     */
    public KamiBuilder disableExp(Class<? extends KamiExp> expClass) {
        expressions.removeIf(exp -> exp.getClass().isAssignableFrom(expClass));
        return this;
    }

    /**
     * Set the global storage.
     * <br>
     *
     * @param globalStorage The global storage.
     * @return The builder.
     */
    public KamiBuilder useGlobalStorage(IGlobalStorage globalStorage) {
        this.globalStorage = globalStorage;
        return this;
    }

    /**
     * Change witch global storage to use.
     * <br>
     *
     * @param id The id of the global storage.
     * @return The builder.
     */
    public KamiBuilder setGlobalStorageId(String id) {
        this.globalStorageId = id;
        return this;
    }

    public KamiBuilder disableGlobalStorage() {
        this.globalStorage = new EmptyGlobalStorage();
        return this;
    }

    // Build
    // ---

    /**
     * Build the parser.
     * <br>
     *
     * @return The parser.
     */
    public KamiParser build() {
        assert globalStorage != null : "Global storage can't be null, instead use disableGlobalStorage()";

        if (outputHandler == null) outputHandler = new DefaultOutputHandler();
        globalStorage.load(globalStorageId);
        KamiParser parser = new KamiParser(outputHandler, expressions, globalStorage);
        if (defaultStorage != null) parser.setDefaultStorage(defaultStorage);

        parser.addHandlers(handlers);
        return parser;
    }
}
