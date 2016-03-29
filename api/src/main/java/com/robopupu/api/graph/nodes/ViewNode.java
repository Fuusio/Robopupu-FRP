package com.robopupu.api.graph.nodes;

import android.view.View;

import com.robopupu.api.graph.AbstractNode;
import com.robopupu.api.graph.AbstractOutputNode;

public class ViewNode extends AbstractOutputNode<View> implements View.OnClickListener {

    protected View mView;

    public ViewNode(final View view) {
        mView = view;
        mView.setOnClickListener(this);
    }

    public View getView() {
        return mView;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(final View view) {
        out(view);
    }
}
