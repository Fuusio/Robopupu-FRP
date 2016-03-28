package com.robopupu.api.graph.nodes;

import com.robopupu.api.graph.AbstractNode;
import com.robopupu.api.graph.Node;

import java.util.List;

public class ListNode<OUT> extends AbstractNode<List<OUT>, OUT> {

    @Override
    public Node<List<OUT>, OUT> onInput(final List<OUT> input) {
        for (final OUT output : input) {
            out(output);
        }
        onCompleted(this);
        return this;
    }
}
