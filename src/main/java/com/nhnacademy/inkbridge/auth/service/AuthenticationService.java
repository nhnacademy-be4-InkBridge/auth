package com.nhnacademy.inkbridge.auth.service;

/**
 * class: AuthenticationService.
 * 인증 관련 서비스 .
 * redis(in memory 기반 데이터베이스)에서 db 정보를 가져오는 작업 .
 * @author devminseo
 * @version 2/24/24
 */
public interface AuthenticationService {

    /**
     * uuid 를 받아 accessToken 재발급
     * @param uuid 회원 식별 고유 아이디
     * @param accessToken accessToken
     */
    void reissueToken(String uuid,String accessToken);

    void logout(String token);
}
