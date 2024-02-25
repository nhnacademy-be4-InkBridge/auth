package com.nhnacademy.inkbridge.auth.exception;

/**
 * class: NotFoundUserException.
 *
 * @author devminseo
 * @version 2/25/24
 */
public class NotFoundUserException extends RuntimeException {
    public NotFoundUserException(String msg) {
        super(msg);
    }
}
