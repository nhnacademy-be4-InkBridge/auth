package com.nhnacademy.inkbridge.auth.provider;

import com.nhnacademy.inkbridge.auth.config.KeyMangerConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * class: JwtUtil.
 * jwt 토큰 발급, 재발급 관련 클래스 .
 *
 * @author devminseo
 * @version 2/23/24
 */
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "inkbridge.jwt")
public class JwtProvider {
    private final KeyMangerConfig keyMangerConfig;
    // 15분
    private static final long ACCESS_TOKEN_EXPIRED_TIME = 1000L * 60 * 30;

    // 1주일
    private static final long REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60L * 60L * 24L * 7;
    String secretKey;

    private Key key() {
        byte[] byteSecretKey = Decoders.BASE64.decode(keyMangerConfig.keyStore(secretKey));
        return Keys.hmacShaKeyFor(byteSecretKey);
    }


    /**
     * 토큰으로부터 유효기간을 가져오는 메서드입니다.
     *
     * @param token 토큰
     * @return 만료기간
     */
    public Date getExpiredTime(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getExpiration();
    }

    /**
     * 토큰으로부터 UUID를 가져오는 메서드입니다.
     *
     * @param token 토큰
     * @return UUID
     */
    public String getUUID(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().get("UUID",String.class);
    }

    /**
     * 종류에 따른 토큰을 만들어주는 메서드입니다.
     *
     * @param id 사용자 UUID
     * @param role 사용자 권한
     * @param expiredMs 토큰 만료일
     * @return 토큰
     */
    public String createJwt(String id, List<String> role, Long expiredMs) {
        Claims claims = Jwts.claims();
        claims.put("UUID", id);
        claims.put("ROLE", role.toString());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Access 토큰을 만드는 메서드.
     *
     * @param id 사용자 아이디
     * @param role 사용자 권한
     * @return 토큰
     */
    public String createAccessToken(String id, List<String> role) {
        return createJwt(id, role, ACCESS_TOKEN_EXPIRED_TIME);
    }

    /**
     * Refresh 토큰을 만드는 메서드.
     *
     * @param id 사용자 아이디
     * @param role 사용자 권한
     * @return 토큰
     */
    public String createRefreshToken(String id, List<String> role) {
        return createJwt(id, role, REFRESH_TOKEN_EXPIRED_TIME);
    }

    /**
     * 토큰의 만료기간이 유효한지 체크하는 메서드입니다.
     *
     * @param token 사용자 토큰
     * @return 유효한지 아닌지
     */
    public Boolean isValidJwt(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 기존 Access 토큰의 클레임을 받아 재발급 해줍니다.
     *
     * @param claims 기존 토큰 클레임
     * @return 새로운 엑세스 토큰
     */
    public String reissueAccessToken(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED_TIME))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰으로부터 클레임 정보를 가져옵니다.
     *
     * @param token 토큰
     * @return 클레임ㅁ
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}