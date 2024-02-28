package com.nhnacademy.inkbridge.auth.exception;

/**
 * class: NotFoundUserException.
 *
 * @author devminseo
 * @version 2/25/24
 */
public class NotFoundUserException extends RuntimeException {
    private static final String MSG = "회원을 찾을 수 없습니다.";
    public NotFoundUserException() {
        super(MSG);
    }
}
