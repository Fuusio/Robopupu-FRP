package com.robopupu.api.graph;

public class SimpleNode<IN, OUT> extends AbstractNode<IN, OUT> {

    /**
     * Constructs a new instance of {@link SimpleNode}.
     */
    public SimpleNode() {
    }

   /**
     * Invoked by {@link SimpleNode#onInput(Object)} when the given input {@link Object} has been received.
     * @param input The input {@link Object}.
     * @return The output {@link Object}.
     */
    @SuppressWarnings("unchecked")
    protected OUT processInput(final IN input) {
        if (input != null) {
            try {
                return (OUT) input;
            } catch (ClassCastException e) {
                error(e);
            }
        }
        return null;
    }
}
