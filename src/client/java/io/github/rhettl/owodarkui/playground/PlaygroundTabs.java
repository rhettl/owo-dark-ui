package io.github.rhettl.owodarkui.playground;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * Tabs shown in the component playground. Register during client
 * initialization; each playground screen creates fresh tab instances.
 *
 * <p>Tabs appear in ascending {@code order}, then registration order. The
 * library's own tabs use {@link #LIBRARY_ORDER}; register before it to appear
 * first.
 */
public final class PlaygroundTabs {
    public static final int LIBRARY_ORDER = 100;
    public static final int DEFAULT_ORDER = 200;

    private record Entry(int order, int sequence, Function<PlaygroundContext, PlaygroundTab> factory) {}

    private static final List<Entry> ENTRIES = new ArrayList<>();

    private PlaygroundTabs() {}

    public static void register(Function<PlaygroundContext, PlaygroundTab> factory) {
        register(DEFAULT_ORDER, factory);
    }

    public static synchronized void register(int order, Function<PlaygroundContext, PlaygroundTab> factory) {
        ENTRIES.add(new Entry(order, ENTRIES.size(), factory));
    }

    static synchronized List<PlaygroundTab> create(PlaygroundContext context) {
        return ENTRIES.stream()
            .sorted(Comparator.comparingInt(Entry::order).thenComparingInt(Entry::sequence))
            .map(entry -> entry.factory().apply(context))
            .toList();
    }
}
