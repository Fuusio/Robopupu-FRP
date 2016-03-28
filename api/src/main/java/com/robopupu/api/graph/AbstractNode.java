package com.robopupu.api.graph;

import com.robopupu.api.graph.functions.BooleanFunction;
import com.robopupu.api.graph.nodes.ActionNode;
import com.robopupu.api.graph.nodes.FilterNode;
import com.robopupu.api.graph.nodes.SkipNode;
import com.robopupu.api.graph.nodes.StringNode;
import com.robopupu.api.graph.nodes.TakeNode;

import java.util.ArrayList;

/**
 * {@link AbstractNode} provide an abstract base class for implementing {@link Node}s.
 * @param <IN> The input type.
 * @param <OUT> The output type.
 */
public abstract class AbstractNode<IN, OUT> implements Node<IN, OUT> {

    protected final ArrayList<Node<OUT, ?>> mOutputs;

    /**
     * Constructs a new instance of {@link AbstractNode}.
     */
    protected AbstractNode() {
        mOutputs = new ArrayList<>();
    }

    /**
     * Invoked by the input {@link Node} for this {@link Node} to receive the input {@link Object}. This method should
     * not be overriden for processing the received input. Method {@link AbstractNode#processInput(Object)} should be overridden
     * for that purpose.
     * @param input The input {@link Object}.
     * @return  This {@link Node}.
     */
    @Override
    public Node<IN, OUT> onInput(final IN input) {
        out(processInput(input));
        return this;
    }

    /**
     * Invoked to submit the given output to attached output {@link Node}(s).
     * @param output The outbut {@link Object}.
     */
    protected void out(final OUT output) {
        if (output != null) {
            for (final Node<OUT, ?> node : mOutputs) {
                node.onInput(output);
            }
        }
    }

    /**
     * Invoked by {@link Node#onInput(Object)} when the given input {@link Object} has been received.
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

    /**
     * Invoked when the specified input {@link Node} is completed.
     * @param inputNode A completed input {@link Node}.
     */
    @Override
    public void onCompleted(final Node<?, OUT> inputNode) {
        // By default do nothing
    }

    /**
     * Invoked when the input {@link Node} has detected or received an error. The default
     * implementation detaches this {@link Node} from the input {@link Node} that notified about
     * an error and dispatched the error to attached output {@link Node}s.
     * @param inputNode An input {@link Node} notifying about error.
     * @param throwable A {@link Throwable} representing the error.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onError(final Node<?, IN> inputNode, final Throwable throwable) {
        inputNode.detach(this);
        error(throwable);
    }

    /**
     * Dispatches an error to all output {@link Node}s if any.
     * @param throwable A {@link Throwable} representing the error.
     */
    protected void error(final Throwable throwable) {
        for (final Node<OUT, ?> node : mOutputs) {
            node.onError(this, throwable);
        }
    }

    /**
     * Attach the given output {@link Node} to the set of output {@link Node}s.
     * @param outputNode A {@link Node}.
     * @return The attached {@link Node}.
     */
    @Override
    public <T> Node<OUT, T> attach(final Node<OUT, T> outputNode) {
        addOutput(outputNode);
        return outputNode;
    }

    /**
     * Attach the given output {@link Node}s to the set of output {@link Node}s.
     * @param outputNode A {@link Node}.
     * @param outputNodes A {@link Node}s.
     */
    public <T> void attach(final Node<OUT, T> outputNode, final Node<OUT, T>... outputNodes) {
        addOutput(outputNode);

        for (final Node<OUT, T> node : outputNodes) {
            addOutput(node);
        }
    }

    /**
     * Adds the given {@link Node} to the set of output {@link Node}s.
     * @param node A {@link Node}.
     * @return The added {@link Node}.
     */
    protected <T> Node<OUT, T> addOutput(final Node<OUT, T> node) {
        if (!mOutputs.contains(node)) {
            mOutputs.add(node);
        }
        onAttached(node);
        return node;
    }

    /**
     * Invoked when the given output {@link Node} has been attached to this {@link Node}.
     * @param outputNode The attached {@link Node}.
     */
    protected void onAttached(final Node<OUT, ?> outputNode) {
        // By default do nothing
    }

    /**
     * Detaches the given output {@link Node} by removing from the set of output {@link Node}s.
     * @param outputNode A {@link Node}.
     */
    @Override
    public boolean detach(final Node<OUT, ?> outputNode) {
        return mOutputs.remove(outputNode);
    }

    /**
     * Invoked when the given output {@link Node} has been detached from this {@link Node}.
     * @param outputNode The detached {@link Node}.
     */
    protected void onDetached(final Node<OUT, ?> outputNode) {
        // By default do nothing
    }

    /**
     * Tests if this {@link Node} has outputs.
     * @return A {@code boolean} value.
     */
    public boolean hasOutputs() {
        return !mOutputs.isEmpty();
    }

    /**
     * Attaches a {@link FilterNode} with the given {@link BooleanFunction} as a filtering condition.
     * @param condition A {@link BooleanFunction}.
     * @return A {@link SkipNode}.
     */
    @SuppressWarnings("unchecked")
    public Node<OUT, OUT> filter(final BooleanFunction<OUT> condition) {
        final FilterNode<OUT> node = new FilterNode<>(condition);
        return addOutput(node);
    }

    /**
     * Attaches a {@link SkipNode} with the given steps parameter value.
     * @param steps The steps parameter.
     * @return A {@link SkipNode}.
     */
    @SuppressWarnings("unchecked")
    public Node<OUT, OUT> skip(final int steps) {
        final SkipNode<OUT> node = new SkipNode<>(steps);
        return addOutput(node);
    }

    /**
     * Attaches a {@link TakeNode} with the given steps parameter
     * @param steps The steps parameter.
     * @return A {@link TakeNode}.
     */
    @SuppressWarnings("unchecked")
    public Node<OUT, OUT> take(final int steps) {
        final TakeNode<OUT> node = new TakeNode<>(steps);
        return addOutput(node);
    }

    /**
     * Attaches a {@link Node}.
     * @return A {@link Node}.
     */
    public Node<OUT, OUT> node() {
        return addOutput(new SimpleNode<>());
    }

    /**
     * Attaches an {@link StringNode} hat converts the input object to a {@link String}.
     * @return An {@link ActionNode}.
     */
    @SuppressWarnings("unchecked")
    public Node<OUT, String> string() {
        final StringNode<OUT> node = new StringNode<>();
        return addOutput(node);
    }

    /**
     * Attaches an {@link ActionNode} with the given {@link Action}.
     * @param action An {@link Action}.
     * @return An {@link ActionNode}.
     */
    public Node<OUT, OUT> action(final Action<OUT> action) {
        final ActionNode<OUT, OUT> node = new ActionNode<>(action);
        return addOutput(node);
    }

    /**
     * Creates and formats an error message.
     * @param message The error message as a {@link String}. May contain place holders for formatting.
     * @param args Optional formats args.
     * @return The created error message as a {@link String}.
     */
    protected String createErrorMessage(final String message, final String... args) {
        final String formattedMessage = String.format(message, args);
        return "Error in nodes " + getClass().getSimpleName() + " : " + formattedMessage;
    }
}
