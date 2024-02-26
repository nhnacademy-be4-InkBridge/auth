package com.nhnacademy.inkbridge.auth.service;

import com.nhnacademy.inkbridge.auth.dto.AuthorizationResponseDto;

/**
 * class: AuthorizationService.
 *
 * @author devminseo
 * @version 2/26/24
 */
public interface AuthorizationService {
    AuthorizationResponseDto authorization(String token);
}
