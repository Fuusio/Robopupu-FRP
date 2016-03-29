package com.robopupu.api.graph.nodes;

import com.robopupu.api.graph.AbstractNode;
import com.robopupu.api.graph.functions.BooleanFunction;

public class SkipWhileNode<IN> extends AbstractNode<IN, IN> {

    private BooleanFunction<IN> mCondition;
    private boolean mSkippingEnded;

    public SkipWhileNode(final BooleanFunction<IN> condition) {
        setCondition(condition);
    }

    public void setCondition(final BooleanFunction condition) {
        mCondition = condition;
        mSkippingEnded = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected IN processInput(final IN input) {
        if (input != null && !mSkippingEnded) {
            if (mCondition.eval(input)) {
                return null;
            } else {
                mSkippingEnded = true;
            }
        }
        return input;
    }
}
