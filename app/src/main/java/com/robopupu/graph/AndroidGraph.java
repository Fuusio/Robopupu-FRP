package com.robopupu.graph;

import android.view.View;
import android.widget.TextView;

import com.robopupu.api.graph.Graph;
import com.robopupu.api.graph.nodes.TextViewNode;
import com.robopupu.api.graph.nodes.ViewNode;
import com.robopupu.api.volley.RequestBuilder;
import com.robopupu.api.volley.RequestNode;

/**
 * {@link AndroidGraph} extends {@link Graph} to utility methods specific for Android.
 */
public class AndroidGraph<T> extends Graph<T> {

    protected AndroidGraph() {
    }

    public static <IN> AndroidGraph<IN> begin() {
        return new AndroidGraph<>();
    }

    /**
     * Attaches an {@link ViewNode} for the given {@link View} to produce click outputs.
     * @param view A {@link View}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public AndroidGraph<View> onClick(final View view) {
        return (AndroidGraph<View>)next(new ViewNode<>(view));
    }

    /**
     * Attaches an {@link TextViewNode} for the given {@link TextView} to produce inputted text
     * as an output.
     * @param view A {@link TextView}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public AndroidGraph<String> onText(final TextView view) {
        return (AndroidGraph<String>)next(new TextViewNode<>(view));
    }

    /**
     * Attaches an {@link RequestNode} for the given {@link RequestBuilder}.
     * @param builder A {@link RequestBuilder}.
     * @return This {@link Graph}.
     */
    @SuppressWarnings("unchecked")
    public <OUT> AndroidGraph<OUT> request(final RequestBuilder<OUT> builder) {
        return (AndroidGraph<OUT>)next(new RequestNode<>(builder));
    }
}
