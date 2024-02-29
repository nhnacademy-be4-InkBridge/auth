package com.nhnacademy.inkbridge.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.inkbridge.auth.dto.request.AuthorizationRequestDto;
import com.nhnacademy.inkbridge.auth.exception.ClientNotFoundException;
import com.nhnacademy.inkbridge.auth.exception.NotFoundUserException;
import com.nhnacademy.inkbridge.auth.provider.CustomAuthenticationProvider;
import com.nhnacademy.inkbridge.auth.provider.JwtProvider;
import com.nhnacademy.inkbridge.auth.util.Errors;
import com.nhnacademy.inkbridge.auth.util.JWTEnums;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * class: CustomAuthenticationFilter.
 *
 * @author devminseo
 * @version 2/25/24
 */
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final CustomAuthenticationProvider provider;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String ACCESS_HEADER = "Authorization-Access";
    private static final String REFRESH_HEADER = "Authorization-Refresh";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60L * 60L * 24L * 7;


    /**
     * front 에서 넘어온 회원정보로 authentication 만들어 넘겨줌.
     *
     * @param request 넘어온 회원 정보
     * @return 넘겨주는 회원정보
     * @throws AuthenticationException 잘못된 형식의 회원정보가 넘어왔을경우
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.debug("front 로부터 회원정보 받음 -> auth server start");

        AuthorizationRequestDto requestDto;
        try {
            requestDto = objectMapper.readValue(request.getInputStream(), AuthorizationRequestDto.class);
        } catch (IOException e) {
            throw new NotFoundUserException();
        }
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        log.debug("Attempt authentication email -> {}", email);
        log.debug("Attempt authentication password -> {}", password);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        log.debug("-> auth attemptAuthentication Filter end");
        return provider.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.debug("successfulAuthentication -> 인증 성공");

        if (Objects.isNull(authResult)) {
            throw new ClientNotFoundException(Errors.MEMBER_NOT_FOUND);
        }
        String memberId = (String) authResult.getPrincipal();
        List<String> authorities = getAuthorities(authResult.getAuthorities());

        log.debug("memberId -> {}", memberId);
        log.debug("authorities -> {}", authorities.toString());

        String uuid = UUID.randomUUID().toString();

        String accessToken = jwtProvider.createAccessToken(uuid, authorities);
        Date accessExpiredTime = jwtProvider.getExpiredTime(accessToken);
        String refreshToken = jwtProvider.createRefreshToken(uuid, authorities);
        Date refreshExpiredTime = jwtProvider.getExpiredTime(refreshToken);


        log.debug("accessToken -> {}",accessToken);
        log.debug("refreshToken -> {}",refreshToken);

        redisTemplate.opsForHash().put(uuid, JWTEnums.REFRESH_TOKEN.getName(), refreshToken);
        redisTemplate.expire(uuid, REFRESH_TOKEN_EXPIRED_TIME, TimeUnit.MILLISECONDS);
        redisTemplate.opsForHash().put(uuid, JWTEnums.MEMBER_ID.getName(), memberId);


        response.addHeader(ACCESS_HEADER, BEARER_PREFIX + accessToken);
        response.addHeader(JWTEnums.HEADER_ACCESS_EXPIRED_TIME.getName(),String.valueOf(accessExpiredTime));
        response.addHeader(REFRESH_HEADER, BEARER_PREFIX + refreshToken);
        response.addHeader(JWTEnums.HEADER_REFRESH_EXPIRED_TIME.getName(), String.valueOf(refreshExpiredTime));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        log.debug("unsuccessfulAuthentication -> {}", failed.toString());
        super.unsuccessfulAuthentication(request, response, failed);
    }

    private List<String> getAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

}
