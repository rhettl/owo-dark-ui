package io.github.rhettl.owodarkui.playground;

import io.wispforest.owo.ui.container.FlowLayout;

/** A small, independently composed page shown in the component playground. */
public interface PlaygroundTab {
    String tabName();

    /** Returns a fresh content tree each time this page is selected. */
    FlowLayout getContent();

    /** Larger compositions may request more vertical room than a single-widget page. */
    default int preferredFrameHeight() { return 250; }

    /** Refresh live display values on the client thread while this page is selected. */
    default void tick() {}
}
