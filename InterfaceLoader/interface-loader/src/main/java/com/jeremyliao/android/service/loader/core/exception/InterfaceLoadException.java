package com.jeremyliao.android.service.loader.core.exception;

/**
 * Created by liaohailiang on 2020-06-06.
 */
public class InterfaceLoadException extends RuntimeException {

    public InterfaceLoadException() {
    }

    public InterfaceLoadException(String message) {
        super(message);
    }

    public InterfaceLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterfaceLoadException(Throwable cause) {
        super(cause);
    }
}
