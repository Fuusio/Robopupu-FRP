package com.robopupu.api.graph.nodes;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.robopupu.api.graph.AbstractNode;

public class TextViewNode<IN> extends AbstractNode<IN, String> implements TextWatcher {

    private TextView mTextView;

    public TextViewNode(final TextView textView) {
        mTextView = textView;
        mTextView.addTextChangedListener(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String processInput(final IN input) {
        return null;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        // Do nothing
    }

    @Override
    public void afterTextChanged(final Editable editable) {
        out(editable.toString());
    }
}
