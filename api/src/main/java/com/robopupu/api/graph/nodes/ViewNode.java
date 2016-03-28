package com.robopupu.api.graph.nodes;

import android.view.View;

import com.robopupu.api.graph.AbstractNode;

public class ViewNode<IN, OUT extends View> extends AbstractNode<IN, OUT> implements View.OnClickListener {

    protected OUT mView;

    public ViewNode(final OUT view) {
        mView = view;
        mView.setOnClickListener(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected OUT processInput(final IN input) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(final View view) {
        out((OUT)view);
    }
}
