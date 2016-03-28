package com.robopupu.api.graph.nodes;

import com.robopupu.api.graph.AbstractNode;

public class StringNode<IN> extends AbstractNode<IN, String> {

    @Override
    protected String processInput(final IN input) {
        if (input != null) {
            return input.toString();
        } else {
            error(new NullPointerException(createErrorMessage("Received a null object")));
            return null;
        }
    }
}
