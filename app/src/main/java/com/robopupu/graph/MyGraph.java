package com.robopupu.graph;

import android.view.View;
import android.widget.TextView;

import com.robopupu.api.graph.Graph;
import com.robopupu.api.graph.nodes.TextViewNode;
import com.robopupu.api.graph.nodes.ViewNode;
import com.robopupu.api.volley.RequestBuilder;
import com.robopupu.api.volley.RequestNode;

/**
 * {@link MyGraph} extends {@link Graph} to utility methods specific for Android.
 */
public class MyGraph<T> extends Graph<T> {

    protected MyGraph() {
    }

    public static <IN> MyGraph<IN> begin() {
        return new MyGraph<>();
    }

    /**
     * Attaches an {@link ViewNode} for the given {@link View} to produce click outputs.
     * @param view A {@link View}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public MyGraph<View> onClick(final View view) {
        final ViewNode<T, View> node = new ViewNode<>(view);
        return (MyGraph<View>)next(node);
    }

    /**
     * Attaches an {@link TextViewNode} for the given {@link TextView} to produce inputted text
     * as an output.
     * @param view A {@link TextView}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public MyGraph<String> text(final TextView view) {
        final TextViewNode<T> node = new TextViewNode<>(view);
        return (MyGraph<String>)next(node);
    }

    /**
     * Attaches an {@link RequestNode} for the given {@link RequestBuilder}.
     * @param builder A {@link RequestBuilder}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <OUT> MyGraph<OUT> request(final RequestBuilder<OUT> builder) {
        final RequestNode<T, OUT> node = new RequestNode<>(builder);
        return (MyGraph<OUT>)next(node);
    }
}
