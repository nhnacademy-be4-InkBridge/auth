package com.nhnacademy.inkbridge.auth.provider;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
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
public class JwtProvider {
    private static final long ACCESS_TOKEN_EXPIRED_TIME = 1000L * 60 * 60;
    private static final long REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60L * 60L * 24L * 7;
    private final UserDetailsService userDetailsService;
    private Key key;

    @Value("${inkbridge.jwt.secret.key}")
    String secretKey;

    private Key key() {
        byte[] byteSecretKey = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(byteSecretKey);

    }


    public Date getExpiredTime(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getExpiration();
    }
    public String getUUID(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().get("UUID",String.class);
    }


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

    public String createAccessToken(String id, List<String> role) {
        return createJwt(id, role, ACCESS_TOKEN_EXPIRED_TIME);
    }

    public String createRefreshToken(String id, List<String> role) {
        return createJwt(id, role, REFRESH_TOKEN_EXPIRED_TIME);
    }

    public Boolean isValidJwt(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String reissueAccessToken(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED_TIME))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}