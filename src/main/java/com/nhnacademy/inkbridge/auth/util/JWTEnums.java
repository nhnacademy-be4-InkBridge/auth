package com.nhnacademy.inkbridge.auth.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * class: JWTEnums.
 *
 * @author devminseo
 * @version 2/24/24
 */
@Getter
@AllArgsConstructor
public enum JWTEnums {
    ACCESS_TOKEN("access_token"),
    REFRESH_TOKEN("refresh_token"),
    EMAIL_ID("email_id"),
    HEADER_UUID("header_uuid"),
    HEADER_EXPIRED_TIME("header_expired_time"),
    PRINCIPAL("principal");
    private final String name;
}
