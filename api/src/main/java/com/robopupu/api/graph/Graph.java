package com.robopupu.api.graph;

import android.view.View;
import android.widget.TextView;

import com.robopupu.api.graph.functions.BooleanFunction;
import com.robopupu.api.graph.nodes.ActionNode;
import com.robopupu.api.graph.nodes.BooleanNode;
import com.robopupu.api.graph.nodes.BufferNode;
import com.robopupu.api.graph.nodes.ByteNode;
import com.robopupu.api.graph.nodes.CharacterNode;
import com.robopupu.api.graph.nodes.DoubleNode;
import com.robopupu.api.graph.nodes.FilterNode;
import com.robopupu.api.graph.nodes.FloatNode;
import com.robopupu.api.graph.nodes.FunctionNode;
import com.robopupu.api.graph.nodes.IntNode;
import com.robopupu.api.graph.nodes.ListNode;
import com.robopupu.api.graph.nodes.LongNode;
import com.robopupu.api.graph.nodes.RepeatNode;
import com.robopupu.api.graph.nodes.RequestNode;
import com.robopupu.api.graph.nodes.ShortNode;
import com.robopupu.api.graph.nodes.SkipNode;
import com.robopupu.api.graph.nodes.SkipWhileNode;
import com.robopupu.api.graph.nodes.StringNode;
import com.robopupu.api.graph.nodes.SumNode;
import com.robopupu.api.graph.nodes.TakeNode;
import com.robopupu.api.graph.nodes.TextViewNode;
import com.robopupu.api.graph.nodes.ViewNode;
import com.robopupu.api.graph.nodes.ZipInputNode;
import com.robopupu.api.network.RequestDelegate;

import java.util.HashMap;
import java.util.List;

/**
 * {@link Graph} is a builder utility for constructing graphcs consisting of {@link Node}s.
 *
 * @param <T> The parametrized output type of the {@link Graph}.
 */
public class Graph<T> {

    protected final HashMap<Tag, OutputNode<?>> mTaggedNodes;

    private OutputNode<T> mBeginNode;
    private Tag<T> mBeginTag;
    private OutputNode<?> mCurrentNode;
    private Tag mPendingAttachTag;

    protected Graph() {
        mBeginTag = new Tag<>();
        mTaggedNodes = new HashMap<>();
    }

    /**
     * Gest the begin {@link Tag}.
     * @return A {@link Tag}.
     */
    public Tag<T> getBeginTag() {
        return mBeginTag;
    }

    /**
     * Gets the begin {@link OutputNode} for this {@link Graph}.
     * @return The begin {@link OutputNode}.
     */
    @SuppressWarnings("unchecked")
    public <T_Node extends OutputNode<?>> T_Node getBeginNode() {
        return (T_Node)mBeginNode;
    }

    /**
     * Sets the begin {@link OutputNode}.
     * @param outputNode A {@link OutputNode}.
     */
    protected void setBeginNode(final OutputNode<T> outputNode) {
        if (mPendingAttachTag != null) {
            mTaggedNodes.put(mPendingAttachTag, outputNode);
            mPendingAttachTag = null;
        } else {
            mTaggedNodes.put(mBeginTag, outputNode);
        }
        mBeginNode = outputNode;
        mCurrentNode = outputNode;
    }

    /**
     * Sets the begin {@link OutputNode} with the given tag.
     * @param tag An attach {@link Tag}.
     * @param outputNode A {@link OutputNode}.
     */
    protected void setBeginNode(final Tag tag, final OutputNode<T> outputNode) {
        mTaggedNodes.put(tag, outputNode);
        mBeginNode = outputNode;
        mCurrentNode = outputNode;
    }

    /**
     * Gets the current {@link Node}.
     * @return A {@link Node}.
     */
    @SuppressWarnings("unchecked")
    public <T_Node> T_Node getCurrentNode() {
        return (T_Node)mCurrentNode;
    }

    /**
     * Begins this {@link Graph} with an {@link ActionNode} as a begin node. The given {@link Action}
     * is used to create the {@link ActionNode}.
     * @param action The action as an {@link Action}.
     * @return A {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public static <OUT> Graph<OUT> begin(final Action<OUT> action) {
        final Graph<OUT> graph = new Graph<>();
        graph.setBeginNode(new ActionNode<>(action));
        return graph;
    }

    /**
     * Begins this {@link Graph} with a {@link ActionNode} as a begin node. The begin
     * node is tagged with the given {@link Tag}. The given {@link Action}
     * is used to create the {@link ActionNode}.
     * @param tag A {@link Tag}.
     * @param action The action as an {@link Action}.
     * @return A {@link Graph}.
     */
    public static <OUT> Graph<OUT> begin(final Tag tag, final Action<OUT> action) {
        final Graph<OUT> graph = new Graph<>();
        graph.setBeginNode(tag, new ActionNode<>(action));
        return graph;
    }

    /**
     * Begins this {@link Graph} with the given {@link OutputNode} as a begin node.
     * @param outputNode A {@link OutputNode}
     * @return A {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public static <OUT> Graph<OUT> begin(final OutputNode<OUT> outputNode) {
        final Graph<OUT> graph = new Graph<>();
        graph.setBeginNode(outputNode);
        return graph;
    }

    /**
     * Begins this {@link Graph} with the given {@link OutputNode} as a begin node. The begin
     * node is tagged with the given {@link Tag}.
     * @param tag An attach tag {@link Tag}.
     * @param outputNode A {@link OutputNode}
     * @return A {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public static <OUT> Graph<OUT> begin(final Tag tag, final OutputNode<OUT> outputNode) {
        final Graph<OUT> graph = new Graph<>();
        graph.setBeginNode(tag, outputNode);
        return graph;
    }

    /**
     * Begins this {@link Graph} with a {@link ListNode} as a begin node.
     * @param list A {@link List}
     * @return A {@link Graph}.
     */
    public static <OUT> Graph<OUT> begin(final List<OUT> list) {
        return begin(new ListNode<>(list));
    }

    /**
     * Begins this {@link Graph} with a {@link ListNode} as a begin node. The begin
     * node is tagged with the given {@link Tag}.
     * @param tag A {@link Tag}.
     * @param list A {@link List}
     * @return A {@link Graph}.
     */
    public static <OUT> Graph<OUT> begin(final Tag tag, final List<OUT> list) {
        return begin(tag, new ListNode<>(list));
    }

    /**
     * Finds a {@link OutputNode} tagged with the given {@link Tag} and sets it to be current node.
     * @param tag A {@link Tag}.
     * @param <OUT> The output type of the current {@link Node}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <OUT> Graph<OUT> node(final Tag<OUT> tag) {
        mCurrentNode = mTaggedNodes.get(tag);
        return (Graph<OUT>)this;
    }

    /**
     * Finds a {@link OutputNode} tagged with the given {@link Tag}.
     * @param tag A {@link Tag}.
     * @param <OUT> The output type of the {@link OutputNode}.
     * @return The found {@link OutputNode}. May return {@code null}.
     */
    @SuppressWarnings("unchecked")
    public <OUT> OutputNode<OUT> findNode(final Tag tag) {
        return (OutputNode<OUT>)mTaggedNodes.get(tag);
    }

    /**
     * Tags the next {@link Node} with the given {@link Tag}.
     * @param tag The tag {@link Object}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> tag(final Tag tag) {
        mPendingAttachTag = tag;
        return this;
    }

    /**
     * Attaches the given {@link Node} to be the next {@link Node} after the current {@link Node}.
     * @param node A {@link Node}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <OUT> Graph<OUT> next(final Node<T, OUT> node) {
        Node<T, OUT> nextNode = node;

        if (node instanceof ZipInputNode) {
            nextNode = (Node<T, OUT>)((ZipInputNode)node).getZipNode();
        }

        if (mPendingAttachTag != null) {
            mTaggedNodes.put(mPendingAttachTag, nextNode);
            mPendingAttachTag = null;
        }

        if (mCurrentNode != null) {
            ((OutputNode<T>)mCurrentNode).attach(node);
        }
        mCurrentNode = nextNode;

        if (mBeginNode == null) {
            mBeginNode = (OutputNode<T>)node;
        }
        return (Graph<OUT>)this;
    }

    /**
     * Attaches the given {@link Node} to be the next {@link Node} after the current {@link Node}.
     * The attached {@link Node} is tagged with the given tag {@link Object}.
     * @param findTag The tag {@link Object}.
     * @param node A {@link Node}.
     * @return This {@link Graph}.
     * REMOVE
    public <OUT> Graph<OUT> from(final Object findTag, final Node<T, OUT> node) {
        final Graph<T> graph = find(findTag);
        return graph.next(node);
    }*/

    /**
     * Attaches the given {@link Node} to the specified {@link OutputNode}.
     * The attached {@link Node} is tagged with the given tag {@link Object}.
     * @param findTag The tag {@link Object} specifying the current {@link Node}.
     * @param findTag The tag {@link Object} that is used to tag the attached {@link Node}.
     * @param node A {@link Node}.
     * @return This {@link Graph}.
     * REMOVE
    public <OUT> Graph<OUT> next(final Object findTag, final Object attachTag, final Node<T, OUT> node) {
        final Graph<T> graph = to(findTag);
        return graph.tag(attachTag).next(node);
    }*/

    /**
     * Attaches a {@link FilterNode} with the given condition to the current {@link OutputNode}.
     * @param condition The condition as a {@link BooleanFunction}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> filter(final BooleanFunction<T> condition) {
        return next(new FilterNode<>(condition));
    }

    /**
     * Attaches a {@link FilterNode} with the given condition to the current {@link OutputNode}.
     * The {@link FilterNode} is attached using the given tag.
     * @param tag A {@link Tag}.
     * @param condition The condition as a {@link BooleanFunction}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> filter(final Tag<T> tag, final BooleanFunction<T> condition) {
        tag(tag);
        return next(new FilterNode<>(condition));
    }

    /**
     * Attaches a {@link BufferNode} with the given capacity value to the current {@link OutputNode}.
     * @param capacity The buffer capacity value.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> buffer(final int capacity) {
        return next(new BufferNode<>(capacity));
    }

    /**
     * Attaches a {@link RepeatNode} with the given times parameter value to the current {@link OutputNode}.
     * @param times The steps value.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> repeat(final int times) {
        return next(new RepeatNode<>(times));
    }

    /**
     * Attaches a {@link SkipNode} with the given steps parameter value to the current {@link OutputNode}.
     * @param steps The steps value.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> skip(final int steps) {
        return next(new SkipNode<>(steps));
    }

    /**
     * Attaches a {@link SkipWhileNode} with the given condition to the current {@link OutputNode}.
     * @param condition The condition as a {@link BooleanFunction}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> skipWhile(final BooleanFunction<T> condition) {
        return next(new SkipWhileNode<>(condition));
    }

    /**
     * Attaches a {@link StringNode} to the current {@link OutputNode}.
     * @return This {@link Graph}.
     */
    public Graph<String> string() {
        return next(new StringNode<>());
    }

    /**
     * Attaches a {@link TakeNode} with the given steps parameter value to the current {@link OutputNode}.
     * @param steps The steps value.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> take(final int steps) {
        return next(new TakeNode<>(steps));
    }

    /**
     * Attaches an {@link ActionNode} with the given action to the current {@link OutputNode}.
     * @param action The action as an {@link Action}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public Graph<T> action(final Action<T> action) {
        return next(new ActionNode<>(action));
    }

    /**
     * Attaches a {@link FunctionNode} with the given mapping function to the current {@link OutputNode}.
     * @param function The function as a {@link Function}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <OUT> Graph<OUT> map(final Function<T, OUT> function) {
        return next(new FunctionNode<>(function));
    }

    /**
     * Attaches a {@link SumNode} to the current {@link OutputNode}.
     * @return This {@link Graph}.
     */
    public Graph<Double> sum() {
        return next(new SumNode<>());
    }

    /**
     * Attaches an {@link ViewNode} for the given {@link View} to produce click outputs.
     * @param view A {@link View}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public static Graph<View> onClick(final View view) {
        final Graph<View> graph = new Graph<>();
        graph.setBeginNode(new ViewNode(view));
        return graph;
    }

    /**
     * Attaches an {@link TextViewNode} for the given {@link TextView} to produce inputted text
     * as an output.
     * @param view A {@link TextView}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public static Graph<String> onText(final TextView view) {
        final Graph<String> graph = new Graph<>();
        graph.setBeginNode(new TextViewNode(view));
        return graph;
    }

    /**
     * Attaches an {@link RequestNode} for the given {@link RequestDelegate}.
     * @param delegate A {@link RequestDelegate}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <OUT> Graph<OUT> request(final RequestDelegate<OUT> delegate) {
        return next(new RequestNode<>(delegate));
    }

    /**
     * Invokes the begin node to emit its value(s).
     */
    public void emit() {
        getBeginNode().emit();
    }


    /**
     * Invokes emit on {@link Graph} and converts the emitted output to {@code boolean} value.
     * @return A {@code boolean} value.
     */
    public boolean toBoolean() {
        final BooleanNode<T> node = new BooleanNode<>();
        next(node);
        emit();
        return node.getValue();
    }

    /**
     * Invokes emit on {@link Graph} and converts the emitted output to {@code byte} value.
     * @return A {@code byte} value.
     */
    public byte toByte() {
        final ByteNode<T> node = new ByteNode<>();
        next(node);
        emit();
        return node.getValue();
    }

    /**
     * Invokes emit on {@link Graph} and converts the emitted output to {@code char} value.
     * @return A {@code char} value.
     */
    public char toChar() {
        final CharacterNode<T> node = new CharacterNode<>();
        next(node);
        emit();
        return node.getValue();
    }

    /**
     * Invokes emit on {@link Graph} and converts the emitted output to {@code double} value.
     * @return A {@code double} value.
     */
    public double toDouble() {
        final DoubleNode<T> node = new DoubleNode<>();
        next(node);
        emit();
        return node.getValue();
    }

    /**
     * Invokes emit on {@link Graph} and converts the emitted output to {@code float} value.
     * @return A {@code float} value.
     */
    public float toFloat() {
        final FloatNode<T> node = new FloatNode<>();
        next(node);
        emit();
        return node.getValue();
    }

    /**
     * Invokes emit on {@link Graph} and converts the emitted output to {@code int} value.
     * @return An {@code int} value.
     */
    public int toInt() {
        final IntNode<T> node = new IntNode<>();
        next(node);
        emit();
        return node.getValue();
    }

    /**
     * Invokes emit on {@link Graph} and converts the emitted output to {@code long} value.
     * @return A {@code long} value.
     */
    public long toLong() {
        final LongNode<T> node = new LongNode<>();
        next(node);
        emit();
        return node.getValue();
    }

    /**
     * Invokes emit on {@link Graph} and converts the emitted output to {@code short} value.
     * @return A {@code short} value.
     */
    public short toShort() {
        final ShortNode<T> node = new ShortNode<>();
        next(node);
        emit();
        return node.getValue();
    }

    /**
     * Adds the given {@link InputNode} as an end node.
     * @param inputNode An {@link InputNode}.
     */
    @SuppressWarnings("unchecked")
    public <IN> Graph<IN> end(final InputNode<IN> inputNode) {
        ((OutputNode<IN>)mCurrentNode).attach(inputNode);
        return (Graph<IN>)this;
    }

    /**
     * Adds the specified {@link ActionNode} as and end nodes.
     * @param action An {@link Action} specifying the added {@link ActionNode}.
     */
    @SuppressWarnings("unchecked")
    public <IN> Graph<IN> end(final Action<IN> action) {
        ((OutputNode<IN>)mCurrentNode).attach(new ActionNode<>(action));
        return (Graph<IN>)this;
    }

    /**
     * Converts this {@link Graph}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <T_Graph extends Graph<?>> T_Graph cast() {
        return (T_Graph)this;
    }

}
