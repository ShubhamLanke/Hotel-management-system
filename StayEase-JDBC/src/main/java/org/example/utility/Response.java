package org.example.utility;

import org.example.constants.ResponseStatus;

public class Response {
    private final Object response;
    private final ResponseStatus status;

    public Response(Object response, ResponseStatus status) {
        this.response = response;
        this.status = status;
    }

    public Object getResponse() {
        return response;
    }

    public ResponseStatus getStatus() {
        return status;
    }
}