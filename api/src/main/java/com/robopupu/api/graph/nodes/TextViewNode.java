package com.robopupu.api.graph.nodes;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.robopupu.api.graph.AbstractOutputNode;

public class TextViewNode extends AbstractOutputNode<String> implements TextWatcher {

    private TextView mTextView;

    public TextViewNode(final TextView textView) {
        mTextView = textView;
        mTextView.addTextChangedListener(this);
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
