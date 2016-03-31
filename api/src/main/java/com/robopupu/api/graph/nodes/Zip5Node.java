package com.robopupu.api.graph.nodes;

import com.robopupu.api.graph.Function2;
import com.robopupu.api.graph.Function5;
import com.robopupu.api.graph.ZipInputNode;
import com.robopupu.api.graph.ZipNode;

/**
 * {@link Zip5Node} extends {@link ZipNode} for combining two inputs to one emitted output value.
 */
public class Zip5Node<IN1, IN2, IN3, IN4, IN5, OUT> extends ZipNode<OUT> {

    public final ZipInputNode<IN1, OUT> input1;
    public final ZipInputNode<IN2, OUT> input2;
    public final ZipInputNode<IN3, OUT> input3;
    public final ZipInputNode<IN4, OUT> input4;
    public final ZipInputNode<IN5, OUT> input5;

    private final Function5<IN1, IN2, IN3, IN4, IN5, OUT> mCombineFunction;

    public Zip5Node(final Function5<IN1, IN2, IN3, IN4, IN5, OUT> combineFunction) {
        super(5);
        mCombineFunction = combineFunction;
        input1 = new ZipInputNode<>(this, 0);
        input2 = new ZipInputNode<>(this, 1);
        input3 = new ZipInputNode<>(this, 2);
        input4 = new ZipInputNode<>(this, 3);
        input5 = new ZipInputNode<>(this, 4);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected OUT zip() {
        final IN1 input1 = (IN1)mBuffers[0].remove(0);
        final IN2 input2 = (IN2)mBuffers[1].remove(0);
        final IN3 input3 = (IN3)mBuffers[2].remove(0);
        final IN4 input4 = (IN4)mBuffers[3].remove(0);
        final IN5 input5 = (IN5)mBuffers[4].remove(0);
        return mCombineFunction.eval(input1, input2, input3, input4, input5);
    }
}
