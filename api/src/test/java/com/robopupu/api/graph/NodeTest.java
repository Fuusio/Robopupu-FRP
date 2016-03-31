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
import com.robopupu.api.graph.nodes.SimpleNode;
import com.robopupu.api.graph.nodes.TakeNode;
import com.robopupu.api.graph.nodes.Zip2Node;
import com.robopupu.api.graph.nodes.Zip3Node;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
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
        mEndNode = new TerminalNode<Integer>();
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

        final Node<Integer, Integer> beginNode = new SimpleNode<>();
        final Node<Integer, Integer> node1 = new SimpleNode<>();
        final Node<Integer, Integer> node2 = new SimpleNode<>();

        mEndNode.reset();

        graph.next(beginNode).next(node1).next(node2).next(mEndNode);

        for (int i = 1; i < 4; i++) {
            beginNode.onInput(i);
        }

        assertTrue(mEndNode.received(1, 2, 3));

        graph = Graph.begin();
        graph.eval(input -> Integer.toString(input)).
                eval(input -> Integer.parseInt(input)).
                eval(input -> input > 10).
                eval(input -> input ? 1000 : 0).
                next(mEndNode);

        mEndNode.reset();
        final Node<Integer, Integer> functionNode = graph.getBeginNode();
        functionNode.onInput(20);
        assertTrue(mEndNode.received(1000));
    }

    @Test
    public void test_skip() {
        final Graph<Integer> graph = Graph.begin();

        final Node<Integer, Integer> node0 = new SimpleNode<>();
        final Node<Integer, Integer> node1 = new SimpleNode<>();
        final Node<Integer, Integer> node2 = new SimpleNode<>();

        mEndNode.reset();

        graph.skip(3).next(node0).next(node1).next(node2).next(mEndNode);

        final Node<Integer, Integer> beginNode = graph.getBeginNode();

        for (int i = 1; i < 7; i++) {
            beginNode.onInput(i);
        }

        assertTrue(mEndNode.received(4, 5, 6));
    }

    @Test
    public void test_repeat() {

        Graph<Integer> graph = Graph.begin();
        graph.repeat(0).next(mEndNode);
        mEndNode.reset();
        Node<Integer, Integer> beginNode = graph.getBeginNode();
        beginNode.onInput(1);
        assertTrue(mEndNode.received());

        graph = Graph.begin();
        graph.repeat(1).next(mEndNode);
        mEndNode.reset();
        beginNode = graph.getBeginNode();
        beginNode.onInput(1);
        assertTrue(mEndNode.received(1));

        graph = Graph.begin();
        graph.repeat(5).next(mEndNode);
        mEndNode.reset();
        beginNode = graph.getBeginNode();
        beginNode.onInput(1);
        assertTrue(mEndNode.received(1, 1, 1, 1, 1));
    }

    @Test
    public void test_skipWhile() {

        final Graph<Integer> graph = Graph.begin();
        graph.skipWhile(input -> input > 3).next(mEndNode);
        mEndNode.reset();
        Node<Integer, Integer> beginNode = graph.getBeginNode();
        beginNode.onInput(5);
        beginNode.onInput(6);
        beginNode.onInput(7);
        beginNode.onInput(1);
        beginNode.onInput(8);
        beginNode.onInput(9);
        beginNode.onInput(2);
        beginNode.onInput(3);
        assertTrue(mEndNode.received(1, 8, 9, 2, 3));
    }

    @Test
    public void test_take() {
        final Graph<Integer> graph = Graph.begin();

        final Node<Integer, Integer> node0 = new SimpleNode<>();
        final Node<Integer, Integer> node1 = new SimpleNode<>();
        final Node<Integer, Integer> node2 = new SimpleNode<>();

        mEndNode.reset();

        graph.take(3).next(node0).next(node1).next(node2).next(mEndNode);

        final Node<Integer, Integer> beginNode = graph.getBeginNode();

        for (int i = 1; i < 7; i++) {
            beginNode.onInput(i);
        }

        assertTrue(mEndNode.received(1, 2, 3));
    }

    @Test
    public void test_filter() {
        final Graph<Integer> graph = Graph.begin();

        mEndNode.reset();

        graph.filter(value -> value > 3).next(mEndNode);

        final Node<Integer, Integer> beginNode = graph.getBeginNode();

        for (int i = 1; i < 7; i++) {
            beginNode.onInput(i);
        }

        assertTrue(mEndNode.received(4, 5, 6));
    }

    @Test
    public void test_list() {

        Graph<Integer> graph = Graph.from(mIntegerList);
        graph.take(3).next(mEndNode);
        mEndNode.reset();
        ListNode<Integer> listNode = graph.getBeginNode();
        listNode.emit();
        assertTrue(mEndNode.received(0, 1, 2));

        graph = Graph.from(mIntegerList);
        graph.skip(7).next(mEndNode);
        mEndNode.reset();
        listNode = graph.getBeginNode();
        listNode.emit();
        assertTrue(mEndNode.received(7, 8, 9));
    }

    @Test
    public void test_zip2() {

        final TerminalNode<String> endNode = new TerminalNode<>();
        final Zip2Node<Character, Integer, String> zipNode =
                new Zip2Node<>((input1, input2) -> Character.toString(input1) + Integer.toString(input2));

        final List<Character> characters = createList('A', 'B', 'C');

        Graph<Character> graph = Graph.from("list", characters);
        graph.next(zipNode.input1).<Character>find("list").eval(character -> character - 'A' + 1).next(zipNode.input2).end(endNode);

        graph.getBeginNode().emit();

        assertTrue(endNode.received("A1", "B2", "C3"));
    }

    @Test
    public void test_zip3() {

        final TerminalNode<String> endNode = new TerminalNode<>();
        final Zip3Node<Character, Integer, String, String> zipNode =
                new Zip3Node<>((input1, input2, input3) -> Character.toString(input1) + Integer.toString(input2) + input3);

        final List<Character> characters = createList('A', 'B', 'C');

        Graph<Character> graph = Graph.from("list", characters);
        graph.next(zipNode.input1).
                <Character>find("list").eval(character -> character - 'A' + 1).next(zipNode.input2).
                <Character>find("list").eval(character -> Character.toString(character)).next(zipNode.input3).end(endNode);

        graph.getBeginNode().emit();

        assertTrue(endNode.received("A1A", "B2B", "C3C"));
    }

    private <T> List<T> createList(final T... values) {
        final ArrayList<T> list = new ArrayList<>();
        for (final T value : values) {
            list.add(value);
        }
        return list;
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

    private class TerminalNode<T> extends AbstractNode<T, T> {

        private final ArrayList<T> mReceivedInputs;

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

        public ArrayList<T> getReceivedInputs() {
            return mReceivedInputs;
        }

        public void reset() {
            mReceivedInputs.clear();
            mCompleted = false;
            mErrorReceived = false;
        }

        @Override
        public T processInput(final OutputNode<T> outputNode, final T input) {
            mReceivedInputs.add(input);
            return input;
        }

        @Override
        public void onCompleted(final OutputNode<?> outputNode) {
            mCompleted = true;
        }

        @Override
        public void onError(final OutputNode<?> outputNode, final Throwable throwable) {
            mErrorReceived = true;
        }

        public boolean received(final T... inputs) {
            final int receivedCount = mReceivedInputs.size();

            if (inputs.length == receivedCount) {
                for (int i = 0; i < receivedCount; i++) {
                    if (!inputs[i].equals(mReceivedInputs.get(i))) {
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
