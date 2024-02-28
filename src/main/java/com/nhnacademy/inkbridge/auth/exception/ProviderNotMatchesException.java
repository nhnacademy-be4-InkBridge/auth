package com.nhnacademy.inkbridge.auth.exception;

/**
 * class: ProviderNotMatchesException.
 *
 * @author devminseo
 * @version 2/27/24
 */
public class ProviderNotMatchesException extends RuntimeException {
    public static final String MSG = "아이디 또는 비밀번호가 틀렸습니다.";
    public ProviderNotMatchesException() {
        super(MSG);
    }
}
