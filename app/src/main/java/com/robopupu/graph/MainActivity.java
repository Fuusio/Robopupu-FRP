package com.robopupu.graph;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.robopupu.api.graph.Graph;
import com.robopupu.api.network.volley.GsonRequest;
import com.robopupu.api.network.volley.RequestBuilder;

public class MainActivity extends AppCompatActivity {

    private TextView mJokesTextView;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mJokesTextView = (TextView) findViewById(R.id.text_view);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);

        final RequestBuilder<JokeResponse> getJoke =
                new RequestBuilder<JokeResponse>(this, "http://api.icndb.com/jokes/random").
                    request(new GsonRequest<>(JokeResponse.class));

        Graph.onClick(fab).request(getJoke).action(this::displayJoke);
    }

    private void displayJoke(final JokeResponse response) {
        final String joke = response.getValue().getJoke();
        final String formattedJoke = joke.replace("&quot;", "\"");

        mJokesTextView.append(formattedJoke);
        mJokesTextView.append("\n\n");

        mScrollView.fullScroll(View.FOCUS_DOWN);
    }
}
