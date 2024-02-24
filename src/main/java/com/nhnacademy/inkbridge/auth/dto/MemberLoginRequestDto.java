package com.nhnacademy.inkbridge.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * class: MemberLoginRequestDto.
 *
 * @author devminseo
 * @version 2/22/24
 */
@Getter
@AllArgsConstructor
public class MemberLoginRequestDto {
    private String email;
}
