package org.example.utility;

import org.example.constants.ResponseStatus;
import org.example.entity.Booking;

public class Response {
    private final Object data;
    private final ResponseStatus status;
    private final String message;

    public Response(Object data, ResponseStatus status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public Booking getObjectResponse() {
        return data;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public String getMessage(){
        return message;
    }
}