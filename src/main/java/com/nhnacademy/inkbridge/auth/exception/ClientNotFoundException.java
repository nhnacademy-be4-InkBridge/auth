package com.nhnacademy.inkbridge.auth.exception;

import com.nhnacademy.inkbridge.auth.util.Errors;
import lombok.Getter;

/**
 * class: ClientNotFoundException.
 *
 * @author devminseo
 * @version 2/26/24
 */
@Getter
public class ClientNotFoundException extends RuntimeException {
    private final Errors errors;

    public ClientNotFoundException(Errors errors) {
        super(errors.getName());
        this.errors = errors;
    }
}
