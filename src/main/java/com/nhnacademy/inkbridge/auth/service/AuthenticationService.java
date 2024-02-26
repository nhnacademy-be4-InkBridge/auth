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
     * 레디스에서 회원 식별 번호를 가져옵니다 .
     * @param uuid 회원 식별 고유 아이디
     * @return 회원 로그인 아이디값
     */
    String getId(String uuid);

    /**
     * 레디스에서 회원 권한을 가져옵니다.
     * @param uuid 회원 식별 아이디
     * @return 권한
     */
    String getRoles(String uuid);

    /**
     * uuid 를 받아 accessToken 재발급
     * @param uuid 회원 식별 고유 아이디
     * @param accessToken accessToken
     */
    void doReissue(String uuid,String accessToken);

    void doLogout(String uuid);
}
