package org.example.utility;

import lombok.Builder;
import lombok.Getter;
import org.example.constants.ResponseStatus;

@Getter
@Builder
public class Response {
    private final Object data;
    private final ResponseStatus status;
    private final String message;

//    public Response(Object data, ResponseStatus status, String message) {
//        this.data = data;
//        this.status = status;
//        this.message = message;
//    }

//    public boolean isSuccess(){
//        return status.equals(ResponseStatus.SUCCESS);
//    }
}