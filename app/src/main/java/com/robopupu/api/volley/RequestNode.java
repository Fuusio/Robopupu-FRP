package com.robopupu.api.volley;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.robopupu.api.graph.AbstractNode;
import com.robopupu.api.graph.Node;
import com.robopupu.api.graph.OutputNode;

/**
 * {@link RequestNode} extends {@link AbstractNode} to implement {@link Node} for making REST
 * requests.
 */
public class RequestNode<IN, OUT> extends AbstractNode<IN, OUT> implements RequestCallback<OUT> {

    private final BaseRequest<OUT> mRequest;
    private final RequestBuilder mRequestBuilder;

    private RequestQueue mRequestQueue;

    public RequestNode(final RequestBuilder<OUT> builder) {
        mRequest = builder.build();
        mRequest.setCallback(this);
        mRequestBuilder = builder;
    }

    @Override
    public void onInput(final OutputNode<IN> outputNode, final IN input) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mRequestBuilder.getContext());
        }
        mRequestQueue.add(mRequest);
    }

    @Override
    public void onResponse(final OUT response) {
        out(response);
    }

    @Override
    public void onError(final RequestError requestError) {
        error(requestError);
    }
}
