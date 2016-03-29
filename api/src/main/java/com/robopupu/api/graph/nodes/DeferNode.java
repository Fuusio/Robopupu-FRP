package com.robopupu.api.graph.nodes;

import com.robopupu.api.graph.AbstractNode;
import com.robopupu.api.graph.InputNode;
import com.robopupu.api.graph.OutputNode;

import java.util.ArrayList;

public class DeferNode<IN> extends AbstractNode<IN, IN> {

    private final ArrayList<IN> mBuffer;
    private final int mMaxBufferSize;

    public DeferNode() {
        this(Integer.MAX_VALUE);
    }

    public DeferNode(final int maxBufferSize) {
        mBuffer = new ArrayList<>();
        mMaxBufferSize = maxBufferSize;
    }

    @Override
    protected IN processInput(final OutputNode<IN> outputNode, final IN input) {
        if (hasInputNodes()) {
            return input;
        } else {
            final int bufferSize = mBuffer.size();

            if (bufferSize == mMaxBufferSize) {
                mBuffer.remove(0);
            }
            mBuffer.add(input);
            return null;
        }
    }

    @Override
    protected void onAttached(final InputNode<IN> inputNode) {
        if (!mBuffer.isEmpty()) {
            final ArrayList<IN> buffer = new ArrayList<>(mBuffer);
            mBuffer.clear();

            for (final IN output : buffer) {
                out(output);
            }
        }
    }
}
