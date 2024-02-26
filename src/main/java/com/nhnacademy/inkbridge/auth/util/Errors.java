package com.nhnacademy.inkbridge.auth.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * class: Errors.
 *
 * @author devminseo
 * @version 2/26/24
 */
@Getter
@RequiredArgsConstructor
public enum Errors {
    MEMBER_NOT_FOUND("요청한 회원을 찾을 수 없습니다.");
    private final String name;
}
