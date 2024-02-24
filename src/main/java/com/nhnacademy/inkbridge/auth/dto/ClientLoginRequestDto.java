package com.nhnacademy.inkbridge.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * class: ClientLoginRequestDto.
 *
 * @author devminseo
 * @version 2/22/24
 */
@Getter
@AllArgsConstructor
public class ClientLoginRequestDto {
    private String email;
    private String password;
}
