package io.github.rhettl.owodarkui.playground;

import io.wispforest.owo.ui.core.Component;

/** What the playground screen offers to the tabs it creates. */
public interface PlaygroundContext {
    /**
     * The real handled slot at this menu index (0-26 inventory, 27-35 hotbar).
     * Slots start hidden on every tab switch; mounting the component shows it.
     */
    Component slot(int index);
}
