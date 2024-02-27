package com.nhnacademy.inkbridge.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * class: AuthorizationResponseDto.
 *
 * @author devminseo
 * @version 2/26/24
 */
@Getter
@AllArgsConstructor
public class AuthorizationRequestDto {
    private String email;
    private String password;
}
