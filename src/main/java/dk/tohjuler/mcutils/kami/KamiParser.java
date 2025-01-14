package dk.tohjuler.mcutils.kami;

import dk.tohjuler.mcutils.kami.handlers.IGlobalStorage;
import dk.tohjuler.mcutils.kami.handlers.IHandler;
import dk.tohjuler.mcutils.kami.handlers.IOutputHandler;
import dk.tohjuler.mcutils.kami.storage.KamiStorage;
import dk.tohjuler.mcutils.kami.storage.TypeItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


/**
 * Kami parser used for parsing inputs.
 * <br>
 * Use {@link KamiBuilder} to create a parser.
 */
@Getter
public class KamiParser {
    private final List<KamiExp> expressions;

    private final IOutputHandler outputHandler;
    private final IGlobalStorage globalStorage;

    private final List<IHandler> handlers = new ArrayList<>();

    @Setter
    private @Nullable KamiStorage<String> defaultStorage = null;

    /**
     * Creates a new Kami parser.
     * Please use {@link KamiBuilder} to create a parser.
     * <br>
     *
     * @param outputHandler        The output handler.
     * @param expressions          The expressions to use.
     * @param globalStorage The global storage handler.
     */
    public KamiParser(IOutputHandler outputHandler, List<KamiExp> expressions, IGlobalStorage globalStorage) {
        this.outputHandler = outputHandler;
        this.expressions = expressions;
        this.globalStorage = globalStorage;
    }

    /**
     * Parses the input.
     * <br>
     *
     * @param input The input to parse.
     * @return The result of the parsing.
     */
    public @NotNull TypeItem<String> parse(String input) {
        return parse(input, null);
    }

    /**
     * Parses the input.
     * <br>
     *
     * @param input The input to parse.
     * @param p     The player to parse for.
     * @return The result of the parsing.
     */
    public @NotNull TypeItem<String> parse(String input, @Nullable Player p) {
        KamiState state = new KamiState(this, p);
        state.getLocalStorage().put("input", new TypeItem<>(input));
        if (defaultStorage != null)
            state.getLocalStorage().getStorage().putAll(defaultStorage.getStorage());
        state.setCurrentStr(input);

        expressions.sort(Comparator.comparing(exp -> exp.getPriority().getValue()));

        boolean panic = false;
        for (KamiExp exp : expressions) {
            KamiResult result = exp.match(state);
            if (result == null) continue;

            if (!result.isPanic()) {
                if (result.isBlocking()) {
                    state.writeDebug("Blocking expression, stopping parsing.");
                    state.writeDebug("State: " + state);
                    break;
                }
                continue;
            }

            panic = true;
            state.writeDebug("Panic error is written to err, stopping parsing.");
            state.writeDebug("Input: " + state.getLocalStorage().get("input").asString("ERROR: Not found"));
            state.writeDebug("Reported old expression: " + result.getOldExp());
            state.writeDebug("State expression: " + state.getCurrentExp());
            state.writeDebug("State: " + state);
            outputHandler.err(result.getPanicError(), p);
        }

        if (panic) return new TypeItem<>(null);

        return new TypeItem<>(state.getCurrentStr());
    }

    /**
     * Add handlers to the parser.
     * <br>
     *
     * @param handlers The handlers to add.
     */
    public void addHandlers(List<IHandler> handlers) {
        this.handlers.addAll(handlers);
    }

    /**
     * Adds a handler to the parser.
     * <br>
     *
     * @param handler The handler to add.
     */
    public void addHandler(IHandler handler) {
        handlers.add(handler);
    }

    // Tests methods

    public static String staticTest() {
        return "Hello, World!";
    }

    public static List<String> staticTestList() {
        return Arrays.asList("Hello", "World");
    }
}
