package com.robopupu.api.graph;

import com.robopupu.api.graph.functions.BooleanFunction;
import com.robopupu.api.graph.nodes.ActionNode;
import com.robopupu.api.graph.nodes.FilterNode;
import com.robopupu.api.graph.nodes.FunctionNode;
import com.robopupu.api.graph.nodes.SkipNode;
import com.robopupu.api.graph.nodes.TakeNode;

import java.util.HashMap;

/**
 * {@link Graph} is a builder utility for constructing graphcs consisting of {@link Node}s.
 */
public class Graph<T> {

    protected final HashMap<Object, Node<?,?>> mTaggedNodes;

    private Node<?,?> mBeginNode;
    private Node<?,?> mCurrentNode;
    private Object mPendingTag;

    private Graph() {
        mTaggedNodes = new HashMap<>();
    }

    public static <IN> Graph<IN> create() {
        return new Graph<>();
    }

    /**
     * Converts the expected input type of this {@link Graph}.
     * @param <IN> The new type.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <IN> Graph<IN> to() {
        return (Graph<IN>)this;
    }

    /**
     * Gets the begin {@link Node} for this {@link Graph}.
     * @param <IN> The input type of the {@link Node}.
     * @param <OUT> The output type of the {@link Node}.
     * @return The begin {@link Node}.
     */
    @SuppressWarnings("unchecked")
    public <IN,OUT> Node<IN,OUT> getBeginNode() {
        return (Node<IN,OUT>)mBeginNode;
    }

    /**
     * Finds a {@link Node} tagged with the given tag {@link Object} and sets it to be current tag.
     * @param tag The tag {@link Object}.
     * @param <IN> The input type of the current {@link Node}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <IN> Graph<IN> find(final Object tag) {
        mCurrentNode = mTaggedNodes.get(tag);
        return (Graph<IN>)this;
    }

    /**
     * Finds a {@link Node} tagged with the given tag {@link Object}.
     * @param tag The tag {@link Object}.
     * @param <IN> The input type of the {@link Node}.
     * @param <OUT> The output type of the {@link Node}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <IN,OUT> Node<IN,OUT> findNode(final Object tag) {
        return (Node<IN, OUT>)mTaggedNodes.get(tag);
    }

    /**
     * Tags the next {@link Node} with the given tag {@link Object}.
     * @param tag The tag {@link Object}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> tag(final Object tag) {
        mPendingTag = tag;
        return this;
    }

    /**
     * Attaches the given {@link Node} to be the next {@link Node} after the current {@link Node}.
     * @param node A {@link Node}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> next(final Node<T, ?> node) {
        if (mPendingTag != null) {
            mTaggedNodes.put(mPendingTag, node);
            mPendingTag = null;
        } else {
            mTaggedNodes.put(node, node);
        }

        if (mCurrentNode != null) {
            ((Node<?, T>)mCurrentNode).attach(node);
        } else {
            mBeginNode = node;
        }
        mCurrentNode = node;
        return this;
    }

    /**
     * Attaches the given {@link Node} to be the next {@link Node} after the current {@link Node}.
     * The attached {@link Node} is tagged with the given tag {@link Object}.
     * @param tag The tag {@link Object}.
     * @param node A {@link Node}.
     * @return This {@link Graph}.
     */
    public Graph<T> next(final Object tag, final Node<T,?> node) {
        final Graph<T> graph =  find(tag);
        return graph.next(node);
    }

    /**
     * Attaches the given {@link Node} to be the next {@link Node} after the specified {@link Node}.
     * The attached {@link Node} is tagged with the given tag {@link Object}.
     * @param findTag The tag {@link Object} specifying the current {@link Node}.
     * @param findTag The tag {@link Object} that is used to tag the attached {@link Node}.
     * @param node A {@link Node}.
     * @return This {@link Graph}.
     */
    public Graph<T> next(final Object findTag, final Object attachTag, final Node<T, ?> node) {
        final Graph<T> graph =  find(findTag);
        return graph.tag(attachTag).next(node);
    }

    /**
     * Attaches a {@link FilterNode} with the given condition after the current {@link Node}.
     * @param condition The condition as a {@link BooleanFunction}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> filter(final BooleanFunction<T> condition) {
        final FilterNode<T> node = new FilterNode<>(condition);
        return next(node);
    }

    /**
     * Attaches a {@link SkipNode} with the given steps parameter value after the current {@link Node}.
     * @param steps The steps value.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> skip(final int steps) {
        final SkipNode<T> node = new SkipNode<>(steps);
        return next(node);
    }

    /**
     * Attaches a {@link TakeNode} with the given steps parameter value after the current {@link Node}.
     * @param steps The steps value.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> take(final int steps) {
        final TakeNode<T> node = new TakeNode<>(steps);
        return next(node);
    }

    /**
     * Attaches an {@link ActionNode} with the given action after the current {@link Node}.
     * @param action The action as a {@link Action}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> action(final Action<T> action) {
        final ActionNode<T, T> node = new ActionNode<>(action);
        return next(node);
    }

    /**
     * Attaches a {@link FunctionNode} with the given function after the current {@link Node}.
     * @param function The function as a {@link Function}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> function(final Function<T, ?> function) {
        final FunctionNode<T, ?> node = new FunctionNode<>(function);
        return next(node);
    }

    /**
     * Gets the current {@link Node}.
     * @return A {@link Node}.
     */
    @SuppressWarnings("unchecked")
    public <IN, OUT> Node<IN, OUT> node() {
        return (Node<IN, OUT>)mCurrentNode;
    }
}
