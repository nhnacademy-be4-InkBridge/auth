package com.nhnacademy.inkbridge.auth.service.impl;

import com.nhnacademy.inkbridge.auth.service.AuthenticationService;
import com.nhnacademy.inkbridge.auth.util.JWTEnums;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * class: AuthenticationServiceImpl.
 * 인증 관련 토큰 재발급, 로그아웃 진행시 redis 추가, 수정, 삭제 작업 서비스 구현 .
 * @author devminseo
 * @version 2/24/24
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void logout(String uuid) {
        redisTemplate.opsForHash().delete(uuid, JWTEnums.REFRESH_TOKEN.getName());
        redisTemplate.opsForHash().delete(uuid, JWTEnums.MEMBER_ID.getName());
    }
}
