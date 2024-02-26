package com.nhnacademy.inkbridge.auth.dto;

import java.util.List;
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
public class AuthorizationResponseDto {
    private String email;
    private List<String> roles;
}
