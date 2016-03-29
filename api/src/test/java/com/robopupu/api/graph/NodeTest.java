/*
 * Copyright (C) 2014 - 2015 Marko Salmela, http://robopupu.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.robopupu.api.graph;

import android.test.suitebuilder.annotation.SmallTest;

import com.robopupu.api.graph.nodes.ActionNode;
import com.robopupu.api.graph.nodes.ListNode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@SmallTest
public class NodeTest {

    private static String ERROR_A = "A";
    private static String ERROR_B = "B";
    private static String ERROR_C = "C";
    private static String ERROR_D = "D";
    private static String ERROR_E = "E";

    private TerminalNode mEndNode;
    private List<Integer> mIntegerList;

    @Before
    public void beforeTests() {
        mEndNode = new TerminalNode();
        mIntegerList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            mIntegerList.add(i);
        }
    }

    @After
    public void afterTests() {}

    @Test
    public void test_attach() {
        Graph<Integer> graph = Graph.begin();

        final Node<Integer, Integer> node0 = new SimpleNode<>();
        final Node<Integer, Integer> node1 = new SimpleNode<>();
        final Node<Integer, Integer> node2 = new SimpleNode<>();

        mEndNode.reset();

        graph.next(node0).next(node1).next(node2).next(mEndNode);
        graph.getBeginNode().onInput(1).onInput(2).onInput(3);

        assertTrue(mEndNode.received(1, 2, 3));

        graph = Graph.begin();
        graph.eval(input -> Integer.toString(input)).
                eval(input -> Integer.parseInt(input)).
                eval(input -> input > 10).
                eval(input -> input ? 1000 : 0).
                next(mEndNode);

        mEndNode.reset();
        graph.getBeginNode().onInput(20);
        assertTrue(mEndNode.received(1000));
    }

    @Test
    public void test_skip() {
        final Graph<Integer> graph = Graph.begin();

        final Node<Integer, Integer> node0 = new SimpleNode<>();
        final Node<Integer, Integer> node1 = new SimpleNode<>();
        final Node<Integer, Integer> node2 = new SimpleNode<>();

        mEndNode.reset();

        graph.skip(3).next(node1).next(node2).next(mEndNode);
        graph.getBeginNode().onInput(1).onInput(2).onInput(3).onInput(4).onInput(5).onInput(6);

        assertTrue(mEndNode.received(4, 5, 6));
    }

    @Test
    public void test_repeat() {

        Graph<Integer> graph = Graph.begin();
        graph.repeat(0).next(mEndNode);
        mEndNode.reset();
        graph.getBeginNode().onInput(1);
        assertTrue(mEndNode.received());

        graph = Graph.begin();
        graph.repeat(1).next(mEndNode);
        mEndNode.reset();
        graph.getBeginNode().onInput(1);
        assertTrue(mEndNode.received(1));

        graph = Graph.begin();
        graph.repeat(5).next(mEndNode);
        mEndNode.reset();
        graph.getBeginNode().onInput(1);
        assertTrue(mEndNode.received(1, 1, 1, 1, 1));
    }


    @Test
    public void test_take() {
        final Graph<Integer> graph = Graph.begin();

        final Node<Integer, Integer> node0 = new SimpleNode<>();
        final Node<Integer, Integer> node1 = new SimpleNode<>();
        final Node<Integer, Integer> node2 = new SimpleNode<>();

        mEndNode.reset();

        graph.take(3).next(node1).next(node2).next(mEndNode);
        graph.getBeginNode().onInput(1).onInput(2).onInput(3).onInput(4).onInput(5).onInput(6);

        assertTrue(mEndNode.received(1, 2, 3));
    }

    @Test
    public void test_filter() {
        final Graph<Integer> graph = Graph.begin();

        mEndNode.reset();

        graph.filter(value -> value > 3).next(mEndNode);
        graph.getBeginNode().onInput(1).onInput(2).onInput(3).onInput(4).onInput(5).onInput(6);

        assertTrue(mEndNode.received(4, 5, 6));
    }

    @Test
    public void test_list() {

        Graph<List<Integer>> graph = Graph.begin();

        mEndNode.reset();

        graph.next(new ListNode<>()).take(3).next(mEndNode);
        graph.getBeginNode().onInput(mIntegerList);

        assertTrue(mEndNode.received(0, 1, 2));

        graph = Graph.begin();

        mEndNode.reset();

        graph.next(new ListNode<>()).skip(7).next(mEndNode);
        graph.getBeginNode().onInput(mIntegerList);

        assertTrue(mEndNode.received(7, 8, 9));
    }


    @Test
    public void test_logic() {

        final AuthenticatorImpl authenticator = new AuthenticatorImpl();

        Node<Response, Response> loginNode;
        Response response;

        // Http 400, Error A

        response = new Response();
        response.error = ERROR_A;
        response.statusCode = 400;

        loginNode = createGraph(authenticator);
        authenticator.reset();
        loginNode.onInput(response);
        assertTrue(authenticator.failed());

        // Http 401, Error D

        response = new Response();
        response.error = ERROR_D;
        response.statusCode = 401;

        loginNode = createGraph(authenticator);
        authenticator.reset();
        loginNode.onInput(response);
        assertTrue(authenticator.failed());

        // Http 200

        response = new Response();
        response.statusCode = 200;

        loginNode = createGraph(authenticator);
        authenticator.reset();
        loginNode.onInput(response);
        assertTrue(authenticator.succeeded());
    }

    private Node<Response, Response> createGraph(final Authenticator authenticator) {
        final Graph<Response> graph = Graph.begin();

        Node nAuthCode = graph.exec(response -> authenticator.onRequestAuthCode()).node();

        graph.<Response>find(nAuthCode).
                filter(response -> response.statusCode == 200).
                exec(authenticator::onAuthenticationSucceeded);

        Node nStatus400 = graph.<Response>find(nAuthCode).filter(response -> response.statusCode == 400).node();
        Node nStatus401 = graph.<Response>find(nAuthCode).filter(response -> response.statusCode == 401).node();

        Node<Response, Response> failed = new ActionNode<>(authenticator::onAuthenticationFailed);

        graph.<Response>find(nStatus400).filter(response -> ERROR_A.contentEquals(response.error)).next(failed);
        graph.<Response>find(nStatus400).filter(response -> ERROR_B.contentEquals(response.error)).next(failed);
        graph.<Response>find(nStatus401).filter(response -> ERROR_C.contentEquals(response.error)).next(failed);
        graph.<Response>find(nStatus401).filter(response -> ERROR_D.contentEquals(response.error)).next(failed);
        graph.<Response>find(nStatus401).filter(response -> ERROR_E.contentEquals(response.error)).next(failed);

        return graph.getBeginNode();
    }

    private class TerminalNode extends AbstractNode<Integer, Integer> {

        private final ArrayList<Integer> mReceivedInputs;

        private boolean mCompleted;
        private boolean mErrorReceived;

        public TerminalNode() {
            mReceivedInputs = new ArrayList<>();
            reset();
        }

        public boolean isCompleted() {
            return mCompleted;
        }

        public boolean isErrorReceived() {
            return mErrorReceived;
        }

        public ArrayList<Integer> getReceivedInputs() {
            return mReceivedInputs;
        }

        public void reset() {
            mReceivedInputs.clear();
            mCompleted = false;
            mErrorReceived = false;
        }

        @Override
        public Integer processInput(final Integer input) {
            mReceivedInputs.add(input);
            return input;
        }

        @Override
        public void onCompleted(final Node<?, Integer> outputNode) {
            mCompleted = true;
        }

        @Override
        public void onError(final Node<?, Integer> inputNode, final Throwable throwable) {
            mErrorReceived = true;
        }

        public boolean received(final int... inputs) {
            final int receivedCount = mReceivedInputs.size();

            if (inputs.length == receivedCount) {
                for (int i = 0; i < receivedCount; i++) {
                    if (inputs[i] != mReceivedInputs.get(i)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    private class Response {
        public String error;
        public int statusCode;
    }

    private interface Authenticator {

        void onAuthenticationFailed(Response response);
        void onAuthenticationSucceeded(Response response);
        void onRequestAuthCode();
        void onRequestAuthToken();
    }

    private class AuthenticatorImpl implements Authenticator {

        private boolean mFailed;
        private boolean mSucceeded;

        public AuthenticatorImpl() {
            reset();
        }

        public void reset() {
            mFailed = false;
            mSucceeded = false;
        }

        @Override
        public void onAuthenticationFailed(Response response) {
            mFailed = true;
        }

        @Override
        public void onAuthenticationSucceeded(Response response) {
            mSucceeded = true;
        }

        @Override
        public void onRequestAuthCode() {
        }

        @Override
        public void onRequestAuthToken() {
        }

        public boolean failed() {
            return mFailed;
        }

        public boolean succeeded() {
            return mSucceeded;
        }
    }

}
