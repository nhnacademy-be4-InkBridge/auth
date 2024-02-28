package com.nhnacademy.inkbridge.auth.exception;

/**
 * class: ClientLoginException.
 *
 * @author devminseo
 * @version 2/26/24
 */
public class AuthenticationMethodException extends RuntimeException {
    public AuthenticationMethodException(String invalidLoginRequest) {
        super(invalidLoginRequest);
    }
}
