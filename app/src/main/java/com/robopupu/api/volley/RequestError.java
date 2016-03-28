/*
 * Copyright (C) 2001 - 2015 Marko Salmela, http://fuusio.org
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
package com.robopupu.api.volley;

import com.android.volley.VolleyError;
import com.robopupu.api.network.HttpHeaders;

/**
 * {@link RequestError} extends {@link Exception} that is used as a wrapper for
 * a {@link VolleyError},
 */
public class RequestError extends Exception {

    protected final Throwable mCause;
    private final HttpHeaders mHeaders;

    protected String mMessage;
    protected long mNetworkTime;
    protected int mStatusCode;

    public RequestError(final VolleyError error) {
        mCause = error.getCause();
        mMessage = error.getMessage();
        mNetworkTime = error.getNetworkTimeMs();
        mStatusCode = error.networkResponse.statusCode;
        mHeaders = new HttpHeaders(error.networkResponse.headers);
    }

    /**
     * Gets the actual {@link Throwable} causing the error.
     * @return A {@link Throwable}
     */
    public final Throwable getException() {
        return mCause;
    }

    /**
     * Gets the HTTP headers.
     * @return A {@link HttpHeaders}.
     */
    public final HttpHeaders getHeaders() {
        return mHeaders;
    }

    /**
     * Gets the error message.
     * @return The message as a {@link String}.
     */
    public final String getMessage() {
        return mMessage;
    }

    /**
     * Gets the network time of the error.
     * @return
     */
    public final long getNetworkTime() {
        return mNetworkTime;
    }

    /**
     * Gets the HTTP status code of the error response.
     * @return The HTTP status code as an {@code int}.
     */
    public final int getStatusCode() {
        return mStatusCode;
    }

}
