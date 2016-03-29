package com.robopupu.api.graph;

/**
 * {@link AbstractNode} provide an abstract base class for implementing {@link Node}s.
 * @param <IN> The input type.
 * @param <OUT> The output type.
 */
public abstract class AbstractNode<IN, OUT> extends AbstractOutputNode<OUT>
        implements Node<IN, OUT> {

    /**
     * Constructs a new instance of {@link AbstractNode}.
     */
    protected AbstractNode() {
    }

    @Override
    public void onInput(final IN input) {
        out(processInput(null, input));
    }

    @Override
    public void onInput(final OutputNode<IN> outputNode, final IN input) {
        out(processInput(outputNode, input));
    }

    /**
     * Invoked by {@link InputNode#onInput(OutputNode, Object)} when the given input {@link Object}
     * has been received from the {@link OutputNode} that emitted it.
     * @param outputNode An {@link OutputNode} that emitted the input.
     * @param input The input {@link Object}.
     */
    @SuppressWarnings("unchecked")
    protected OUT processInput(final OutputNode<IN> outputNode, final IN input) {
        if (input != null) {
            try {
                return (OUT) input;
            } catch (ClassCastException e) {
                error(e);
            }
        }
        return null;
    }

    @Override
    public void onCompleted(final OutputNode<IN> outputNode) {
        // By default do nothing
    }

    @Override
    public void onError(final OutputNode<IN> outputNode, final Throwable throwable) {
        error(throwable);
    }
}
