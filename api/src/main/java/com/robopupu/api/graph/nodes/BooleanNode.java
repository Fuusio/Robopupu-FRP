package com.robopupu.api.graph.nodes;

import com.robopupu.api.graph.AbstractNode;

public class BooleanNode<IN> extends AbstractNode<IN, Boolean> {

    @Override
    protected Boolean processInput(final IN input) {
        if (input instanceof Boolean) {
            return (Boolean)input;
        } else if (input == null) {
            error(new NullPointerException(createErrorMessage("Received a null object")));
            return null;
        } else {
            error(new NullPointerException(createErrorMessage("Received a non Boolean object")));
            return null;
        }
    }
}
