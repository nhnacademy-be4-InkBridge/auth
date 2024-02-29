package com.nhnacademy.inkbridge.auth.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * class: MemberLoginResponseDto.
 *
 * @author devminseo
 * @version 2/22/24
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginResponseDto {
    private Long memberId;
    private String email;
    private String password;
    private List<String> role;
}
