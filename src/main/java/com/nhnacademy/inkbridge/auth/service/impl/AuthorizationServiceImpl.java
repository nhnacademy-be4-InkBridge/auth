package com.nhnacademy.inkbridge.auth.service.impl;

import com.nhnacademy.inkbridge.auth.dto.AuthorizationResponseDto;
import com.nhnacademy.inkbridge.auth.service.AuthorizationService;
import com.nhnacademy.inkbridge.auth.util.JwtUtil;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * class: AuthorizationServiceImpl.
 *
 * @author devminseo
 * @version 2/26/24
 */
@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {
    private final JwtUtil jwtUtil;
    @Override
    public AuthorizationResponseDto authorization(String token) {
        jwtUtil.isValidJwt(token);
        Authentication authentication = jwtUtil.getAuthentication(token);
        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
                Collectors.toList());

        return new AuthorizationResponseDto(authentication.getName(), roles);
    }
}
