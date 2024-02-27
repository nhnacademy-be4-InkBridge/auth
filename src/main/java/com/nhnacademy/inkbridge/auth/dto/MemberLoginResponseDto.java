package com.nhnacademy.inkbridge.auth.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * class: MemberLoginResponseDto.
 *
 * @author devminseo
 * @version 2/22/24
 */
@Getter
@AllArgsConstructor
public class MemberLoginResponseDto {
    private Long memberId;
    private String email;
    private String password;
    private List<String> role;
}
