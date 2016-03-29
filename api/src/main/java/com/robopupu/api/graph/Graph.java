package com.robopupu.api.graph;

import com.robopupu.api.graph.functions.BooleanFunction;
import com.robopupu.api.graph.nodes.ActionNode;
import com.robopupu.api.graph.nodes.BufferNode;
import com.robopupu.api.graph.nodes.FilterNode;
import com.robopupu.api.graph.nodes.FunctionNode;
import com.robopupu.api.graph.nodes.RepeatNode;
import com.robopupu.api.graph.nodes.SkipNode;
import com.robopupu.api.graph.nodes.SkipWhileNode;
import com.robopupu.api.graph.nodes.TakeNode;

import java.util.HashMap;

/**
 * {@link Graph} is a builder utility for constructing graphcs consisting of {@link Node}s.
 *
 * @param <T> The parametrized output type of the {@link Graph}.
 */
public class Graph<T> {

    protected final HashMap<Object, Node<?,?>> mTaggedNodes;

    private Node<?,?> mBeginNode;
    private Node<?,?> mCurrentNode;
    private Object mPendingTag;

    protected Graph() {
        mTaggedNodes = new HashMap<>();
    }

    public static <OUT> Graph<OUT> begin() {
        return new Graph<>();
    }

    /**
     * Converts this {@link Graph}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <G extends Graph<?>> G to() {
        return (G)this;
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
     * @param <OUT> The input type of the current {@link Node}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <OUT> Graph<OUT> find(final Object tag) {
        mCurrentNode = mTaggedNodes.get(tag);
        return (Graph<OUT>)this;
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
    public <OUT> Graph<OUT> next(final Node<T, OUT> node) {
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
        return (Graph<OUT>)this;
    }

    /**
     * Attaches the given {@link Node} to be the next {@link Node} after the current {@link Node}.
     * The attached {@link Node} is tagged with the given tag {@link Object}.
     * @param tag The tag {@link Object}.
     * @param node A {@link Node}.
     * @return This {@link Graph}.
     */
    public <OUT> Graph<OUT> next(final Object tag, final Node<T, OUT> node) {
        final Graph<T> graph = find(tag);
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
    public <OUT> Graph<OUT> next(final Object findTag, final Object attachTag, final Node<T, OUT> node) {
        final Graph<T> graph = find(findTag);
        return graph.tag(attachTag).next(node);
    }

    /**
     * Attaches a {@link FilterNode} with the given condition after the current {@link Node}.
     * @param condition The condition as a {@link BooleanFunction}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> filter(final BooleanFunction<T> condition) {
        return next(new FilterNode<>(condition));
    }

    /**
     * Attaches a {@link BufferNode} with the given capacity value after the current {@link Node}.
     * @param capacity The buffer capacity value.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> buffer(final int capacity) {
        return next(new BufferNode<>(capacity));
    }

    /**
     * Attaches a {@link RepeatNode} with the given times parameter value after the current {@link Node}.
     * @param times The steps value.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> repeat(final int times) {
        return next(new RepeatNode<>(times));
    }

    /**
     * Attaches a {@link SkipNode} with the given steps parameter value after the current {@link Node}.
     * @param steps The steps value.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> skip(final int steps) {
        return next(new SkipNode<>(steps));
    }

    /**
     * Attaches a {@link SkipWhileNode} with the given condition after the current {@link Node}.
     * @param condition The condition as a {@link BooleanFunction}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> skipWhile(final BooleanFunction<T> condition) {
        return next(new SkipWhileNode<>(condition));
    }


    /**
     * Attaches a {@link TakeNode} with the given steps parameter value after the current {@link Node}.
     * @param steps The steps value.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> take(final int steps) {
        return next(new TakeNode<>(steps));
    }

    /**
     * Attaches an {@link ActionNode} with the given action after the current {@link Node}.
     * @param action The action as an {@link Action}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> exec(final Action<T> action) {
        return next(new ActionNode<>(action));
    }

    /**
     * Attaches a {@link FunctionNode} with the given function after the current {@link Node}.
     * @param function The function as a {@link Function}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <OUT> Graph<OUT> eval(final Function<T, OUT> function) {
        return next(new FunctionNode<>(function));
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
