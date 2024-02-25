package com.nhnacademy.inkbridge.auth.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * class: JWTEnums.
 *
 * @author devminseo
 * @version 2/24/24
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum JWTEnums {
    ACCESS_TOKEN,
    REFRESH_TOKEN,
    UUID,
    HEADER_UUID,
    HEADER_EXPIRED_TIME,
    JWT
}
