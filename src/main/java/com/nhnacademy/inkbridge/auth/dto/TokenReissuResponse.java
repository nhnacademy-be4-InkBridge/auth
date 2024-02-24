package com.nhnacademy.inkbridge.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * class: TokenReissuResponse.
 *
 * @author devminseo
 * @version 2/23/24
 */
@Getter
@AllArgsConstructor
public class TokenReissuResponse {
    private String accessToken;
    private String refreshToken;
}
