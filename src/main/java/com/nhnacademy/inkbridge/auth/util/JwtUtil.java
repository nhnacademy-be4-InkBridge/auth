package com.nhnacademy.inkbridge.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
public class JwtUtil {
    private static final long ACCESS_TOKEN_EXPIRED_TIME = 1000L * 60 * 60;
    private static final long REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60L * 60L * 24L * 7;
    private final UserDetailsService userDetailsService;
    private Key key;

    public JwtUtil(@Value("${inkbridge.jwt.secret.key}") String secret, UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        byte[] byteSecretKey = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    /**
     * 회원 식별 아이디
     *
     * @param token 사용자 토큰
     * @return 토큰의 회원 식별 아이디
     */
    public String getId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("UUID", String.class);
    }

    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
                .get("ROLE", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration()
                .before(new Date());
    }


    public String createJwt(String id, List<String> role, Long expiredMs) {
        Claims claims = Jwts.claims();
        claims.put("UUID", id);
        claims.put("ROLE", role);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
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
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String reissueToken(String id,List<String> role) {
        return createAccessToken(id,role);
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getId(token));
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }
}