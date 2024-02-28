package com.nhnacademy.inkbridge.auth.controller;

import com.nhnacademy.inkbridge.auth.provider.JwtProvider;
import com.nhnacademy.inkbridge.auth.service.AuthenticationService;
import com.nhnacademy.inkbridge.auth.util.JWTEnums;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * class: AuthenticationRestController.
 *
 * @author devminseo
 * @version 2/26/24
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class JwtRestController {
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthenticationService authenticationService;
    private static final String ACCESS_HEADER = "Authorization-Access";
    private static final String REFRESH_HEADER = "Authorization-Refresh";

    /**
     * 만료된 access 토큰을 refresh 토큰 인증을 통해 재발급
     *
     * @param request  기존 사용자의 access, refresh 토큰 정보
     * @param response 새로 발급한 access 토큰 정보
     * @return 성공할 경우 200번대 반환
     */
    @PostMapping("/reissue")
    public ResponseEntity<String> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader(ACCESS_HEADER);
        String refreshToken = request.getHeader(REFRESH_HEADER);
        String uuid = jwtProvider.getUUID(accessToken);
        // TODO: 나중에 여기서 제발급한 accessToken 을 블랙리스트에 올려줘야하지 않을까?

        if (isValidHeaders(accessToken, uuid)) {
            return ResponseEntity.badRequest().body("헤더 정보가 올바르지 않습니다.");
        }

        if (isNotValidKey(uuid)) {
            return ResponseEntity.badRequest().body("존재하지 않는 회원입니다.");
        }
        if (isValidRefreshToken(uuid,refreshToken)) {
            return ResponseEntity.badRequest().body("refresh token 만료");
        }

        Claims claims = jwtProvider.getClaims(accessToken);
        String newAccessToken = jwtProvider.reissueAccessToken(claims);


        response.setHeader(ACCESS_HEADER, "Bearer" + newAccessToken);

        return ResponseEntity.ok().build();
    }

    /**
     * 키값이 들어있는지 확인.
     *
     * @param uuid 전달 받은 키
     * @return 결과값
     */
    private boolean isNotValidKey(String uuid) {
        return redisTemplate.opsForHash().keys(uuid).isEmpty();
    }

    /**
     * refresh 토큰의 만료시간이 넘지 않았는지 확인
     *
     * @param uuid 전달받은 키
     * @return 결과 값
     */
    private boolean isValidRefreshToken(String uuid,String refreshToken) {

        String redisRefreshToken =
                Objects.requireNonNull(redisTemplate.opsForHash().get(uuid, JWTEnums.REFRESH_TOKEN.getName()))
                        .toString();
        if (!Objects.equals(refreshToken, redisRefreshToken)) {
            return false;
        }

        long expiredTime = jwtProvider.getExpiredTime(redisRefreshToken).getTime();
        long now = new Date().getTime();

        return (expiredTime - (now / 1000)) > 0;
    }

    private boolean isValidHeaders(String accessToken, String uuid) {
        return Objects.isNull(accessToken) || Objects.isNull(uuid) || !accessToken.startsWith("Bearer") ||
                !jwtProvider.isValidJwt(accessToken.substring(7));
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        return ResponseEntity.ok().build();
    }

}
