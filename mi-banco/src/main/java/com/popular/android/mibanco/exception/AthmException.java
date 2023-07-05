package com.popular.android.mibanco.exception;

import com.popular.android.mibanco.ws.response.AthmResponse;

/**
 * Exception class to manage ATH Movil exceptions
 */
public class AthmException extends Exception {

    private AthmResponse response;

    public AthmException(AthmResponse response) {
        this.response = response;
    }

    public AthmResponse getResponse() {
        return response;
    }

    public void setResponse(AthmResponse response) {
        this.response = response;
    }
}
