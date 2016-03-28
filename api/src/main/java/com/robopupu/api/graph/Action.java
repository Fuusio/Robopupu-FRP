package com.robopupu.api.graph;

public interface Action<IN> {

    /**
     * Executes an {@link Action} using the given {@link Object} as an input.
     * @param input The input value {@link Object}.
     */
    void execute(final IN input);
}
