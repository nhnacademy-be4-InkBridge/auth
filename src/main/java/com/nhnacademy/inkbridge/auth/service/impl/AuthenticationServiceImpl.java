package com.nhnacademy.inkbridge.auth.service.impl;

import com.nhnacademy.inkbridge.auth.service.AuthenticationService;
import com.nhnacademy.inkbridge.auth.util.JWTEnums;
import java.util.Objects;
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
    /**
     * {@inheritDoc}
     */
    @Override
    public String getId(String uuid) {
        return Objects.requireNonNull(redisTemplate.opsForHash().get(uuid, JWTEnums.EMAIL_ID.getName())).toString();
    }

    /**
     * {@inheritDoc}
     * @param uuid 회원 식별 아이디
     */
    @Override
    public String getRoles(String uuid) {
        return Objects.requireNonNull(redisTemplate.opsForHash().get(uuid, JWTEnums.PRINCIPAL.getName())).toString();
    }

    /**
     * {@inheritDoc}
     * @param uuid 회원 식별 고유 아이디
     * @param accessToken accessToken
     */
    @Override
    public void reissue(String uuid, String accessToken) {
        redisTemplate.opsForHash().delete(uuid, JWTEnums.ACCESS_TOKEN.getName());
        redisTemplate.opsForHash().put(uuid, JWTEnums.ACCESS_TOKEN.getName(), accessToken);
    }

    /**
     * {@inheritDoc}
     * @param uuid
     */
    @Override
    public void logout(String uuid) {
        redisTemplate.opsForHash().delete(uuid, JWTEnums.ACCESS_TOKEN.getName());
        redisTemplate.opsForHash().delete(uuid, JWTEnums.REFRESH_TOKEN.getName());
        redisTemplate.opsForHash().delete(uuid, JWTEnums.EMAIL_ID.getName());
    }
}
