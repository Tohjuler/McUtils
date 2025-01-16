package dk.tohjuler.mcutils.kami.handlers.defaults;

import dk.tohjuler.mcutils.kami.handlers.IHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
public class FunctionHandler implements IHandler {
    private final List<Func<?>> functions;

    /**
     * Create a new function handler with the given functions.
     * <br>
     *
     * @param functions The functions to use.
     */
    public FunctionHandler(@NotNull List<Func<?>> functions) {
        this.functions = functions;

        // Default functions

        addFunction(new SimpleFunc<>(
                "testFunc",
                "Just a test function.",
                () -> "Hello, World!"
        ));

        addFunction(new SimpleFunc<>(
                "functionHelp",
                "Get help for a function.",
                () -> {
                    StringBuilder builder = new StringBuilder();
                    for (Func<?> func : functions)
                        builder.append(func.generatePattern()).append(" : ").append(func.getDescription()).append("\n");
                    return builder.toString();
                }
        ));

        addFunction(new SimpleFunc<>(
                "testError",
                "Just a test function that throws an error.",
                () -> {
                    throw new RuntimeException("Test error, with cause.", new IllegalArgumentException("Test cause."));
                }
        ));
    }

    /**
     * Run a function with the given name and arguments.
     * <br>
     *
     * @param name The name of the function.
     * @param args The arguments to run the function with.
     * @return If the function was found and ran.
     */
    public boolean runFunction(String name, Object... args) {
        for (Func<?> func : functions) {
            if (func.getName().equals(name) && func.matchParams(args)) {
                func.run(args);
                return true;
            }
        }
        return false;
    }

    /**
     * Get functions with the given name.
     * <br>
     *
     * @param name The name of the function.
     * @return The functions with the given name.
     */
    public List<Func<?>> getFunctionByName(String name) {
        return functions.stream().filter(func -> func.getName().equals(name)).collect(Collectors.toList());
    }

    /**
     * Get a function with the given name and arguments.
     * <br>
     *
     * @param name The name of the function.
     * @param args The arguments to run the function with.
     * @return The function if found, otherwise null.
     */
    public @Nullable Func<?> getFunction(String name, Object... args) {
        for (Func<?> func : functions) {
            if (func.getName().equals(name) && func.matchParams(args))
                return func;
        }
        return null;
    }

    /**
     * Add a function to the handler.
     * <br>
     *
     * @param func The function to add.
     */
    public void addFunction(Func<?> func) {
        functions.add(func);
    }

    @Getter
    public static abstract class Func<RETURN> {
        private final String name;
        private final String description;

        public Func(String name, String description) {
            this.name = name;
            this.description = description;
        }

        /**
         * Run the function with the given arguments.
         * <br>
         *
         * @param args The arguments to run the function with.
         * @return The result of the function.
         */
        public abstract RETURN run(Object... args);

        public abstract List<Class<?>> getParameterTypes();

        /**
         * Check if the given arguments match the parameter types.
         * <br>
         *
         * @param args The arguments to check.
         * @return If the arguments match the parameter types.
         */
        public boolean matchParams(Object... args) {
            List<Class<?>> types = getParameterTypes();
            if (types.size() != args.length) return false;
            for (int i = 0; i < args.length; i++)
                if (!types.get(i).isInstance(args[i])) return false;
            return true;
        }

        /**
         * Generate a pattern for the function.
         * <br>
         *
         * @return The pattern.
         */
        public String generatePattern() {
            StringBuilder pattern = new StringBuilder(name + "(");
            for (Class<?> type : getParameterTypes())
                pattern.append("<").append(type.getSimpleName()).append(">").append(getParameterTypes().indexOf(type) == getParameterTypes().size() - 1 ? "" : ", ");
            return pattern.append(")").toString();
        }
    }

    public static class SingleFunc<I, R> extends Func<R> {
        private final Function<I, R> function;

        public SingleFunc(String name, String description, Function<I, R> function) {
            super(name, description);
            this.function = function;
        }

        @Override
        @SuppressWarnings("unchecked")
        public R run(Object... args) {
            return function.apply((I) args[0]);
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<Class<?>> getParameterTypes() {
            return Collections.singletonList(
                    (Class<I>)
                            ((ParameterizedType) getClass()
                                    .getGenericSuperclass())
                                    .getActualTypeArguments()[0]
            );
        }
    }

    public static class SimpleFunc<I> extends Func<I> {
        private final Supplier<I> supplier;

        public SimpleFunc(String name, String description, Supplier<I> supplier) {
            super(name, description);
            this.supplier = supplier;
        }

        @Override
        public I run(Object... args) {
            return supplier.get();
        }

        @Override
        public List<Class<?>> getParameterTypes() {
            return Collections.emptyList();
        }
    }

}
