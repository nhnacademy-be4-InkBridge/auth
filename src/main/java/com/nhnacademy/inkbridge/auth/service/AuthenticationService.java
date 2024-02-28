package com.nhnacademy.inkbridge.auth.service;

/**
 * class: AuthenticationService.
 * 인증 관련 서비스 .
 * redis(in memory 기반 데이터베이스)에서 db 정보를 가져오는 작업 .
 * @author devminseo
 * @version 2/24/24
 */
public interface AuthenticationService {

    void logout(String token);
}
