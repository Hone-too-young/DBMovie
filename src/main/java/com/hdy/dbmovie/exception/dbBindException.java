package com.hdy.dbmovie.exception;

import com.hdy.dbmovie.common.enums.dbHttpStatus;
import org.springframework.http.HttpStatus;

public class dbBindException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = -4137688758944857209L;

    /**
     * http状态码
     */
    private Integer httpStatusCode;

    private Object object;


    /**
     * @param httpStatus http状态码
     */
    public dbBindException(dbHttpStatus httpStatus) {
        super(httpStatus.getMsg());
        this.httpStatusCode = httpStatus.value();
    }

    /**
     * @param httpStatus http状态码
     */
    public dbBindException(dbHttpStatus httpStatus, String msg) {
        super(msg);
        this.httpStatusCode = httpStatus.value();
    }


    public dbBindException(String msg) {
        super(msg);
        this.httpStatusCode = HttpStatus.BAD_REQUEST.value();
    }

    public dbBindException(String msg, Object object) {
        super(msg);
        this.httpStatusCode = HttpStatus.BAD_REQUEST.value();
        this.object = object;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

}

