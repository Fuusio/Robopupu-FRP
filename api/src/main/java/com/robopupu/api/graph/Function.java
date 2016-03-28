package com.robopupu.api.graph;

/**
 * Defines an function interface declaring functions objects and lambdas.
 */
public interface Function<IN, OUT> {

    /**
     * Evaluates a {@link Function} using the given {@link Object} as an input.
     * @param input The input value {@link Object}.
     * @return Output value {@link Object}.
     */
    OUT eval(final IN input);
}
