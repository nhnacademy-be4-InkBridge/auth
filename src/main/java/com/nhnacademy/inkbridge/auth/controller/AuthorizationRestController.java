package com.nhnacademy.inkbridge.auth.controller;

import com.nhnacademy.inkbridge.auth.dto.AuthorizationResponseDto;
import com.nhnacademy.inkbridge.auth.exception.InvalidHeaderException;
import com.nhnacademy.inkbridge.auth.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * class: AuthorizationRestController.
 *
 * @author devminseo
 * @version 2/26/24
 */
@RestController
@RequestMapping("/authorization")
@RequiredArgsConstructor
public class AuthorizationRestController {
    private static final String HEADER_BEARER = "Bearer";
    private final AuthorizationService authorizationService;

    @GetMapping(headers = "Authorization")
    public ResponseEntity<AuthorizationResponseDto> authorization(@RequestHeader(name = "Authorization")String authorization) {
        if (!authorization.startsWith(HEADER_BEARER)) {
            throw new InvalidHeaderException("Header is not valid");
        }
        AuthorizationResponseDto authorizationResponseDto =
                authorizationService.authorization(authorization.substring(HEADER_BEARER.length()));

        return ResponseEntity.ok().body(authorizationResponseDto);
    }
}
