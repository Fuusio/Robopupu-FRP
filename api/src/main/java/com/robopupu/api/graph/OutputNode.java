package com.robopupu.api.graph;

public interface OutputNode<OUT> {

    /**
     * Attach the given {@link InputNode}s by adding them to the set of {@link InputNode}s.
     * @param inputNodes A list of {@link InputNode}s.
     */
    void attach(final InputNode<OUT>... inputNodes);

    /**
     * Detaches the given {@link InputNode}s by removing them from the set of {@link InputNode}s.
     * @param inputNodes A list of {@link InputNode}s.
     */
    void detach(final InputNode<OUT>... inputNodes);
}
