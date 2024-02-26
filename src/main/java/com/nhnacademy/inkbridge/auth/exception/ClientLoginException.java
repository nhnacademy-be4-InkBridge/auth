package com.nhnacademy.inkbridge.auth.exception;

/**
 * class: ClientLoginException.
 *
 * @author devminseo
 * @version 2/26/24
 */
public class ClientLoginException extends RuntimeException {
    public ClientLoginException(String invalidLoginRequest) {
        super(invalidLoginRequest);
    }
}
