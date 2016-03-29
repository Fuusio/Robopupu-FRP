package com.robopupu.api.graph.nodes;

import com.robopupu.api.graph.AbstractNode;

public class RepeatNode<IN> extends AbstractNode<IN, IN> {


    private int mTimes;

    public RepeatNode(final int times) {
        mTimes = times;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected IN processInput(final IN input) {
        if (input != null) {
            for (int i = 0; i < mTimes; i++) {
                out(input);
            }
        }
        return null;
    }
}
