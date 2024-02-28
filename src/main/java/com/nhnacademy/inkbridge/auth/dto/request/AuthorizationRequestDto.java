package com.nhnacademy.inkbridge.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * class: AuthorizationResponseDto.
 *
 * @author devminseo
 * @version 2/26/24
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationRequestDto {
    private String email;
    private String password;
}
