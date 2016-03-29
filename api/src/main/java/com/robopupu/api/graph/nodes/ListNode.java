package com.robopupu.api.graph.nodes;

import com.robopupu.api.graph.AbstractOutputNode;

import java.util.ArrayList;
import java.util.List;

public class ListNode<OUT> extends AbstractOutputNode<OUT> {

    protected ArrayList<OUT> mList;

    public ListNode(final List<OUT> list) {
        mList = new ArrayList<>();
        mList.addAll(list);
    }

    /**
     * Invoked to emit the contents of this {@link ListNode}.
     */
    public void emit() {
        for (final OUT output : mList) {
            out(output);
        }
        completed();
    }
}
