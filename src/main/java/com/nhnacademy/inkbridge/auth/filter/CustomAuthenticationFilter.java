package com.nhnacademy.inkbridge.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.inkbridge.auth.dto.ClientLoginRequestDto;
import com.nhnacademy.inkbridge.auth.exception.ClientLoginException;
import com.nhnacademy.inkbridge.auth.exception.ClientNotFoundException;
import com.nhnacademy.inkbridge.auth.util.Errors;
import com.nhnacademy.inkbridge.auth.util.JWTEnums;
import com.nhnacademy.inkbridge.auth.util.JwtUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
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
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String AUTHORIZATION_HEADER="Authorization";
    private static final String BEARER_PREFIX = "Bearer";


    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                                      RedisTemplate<String, Object> redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    /**
     * front 에서 넘어온 회원정보로 authentication 만들어 넘겨줌.
     * @param request 넘어온 회원 정보
     * @return 넘겨주는 회원정보
     * @throws AuthenticationException 잘못된 형식의 회원정보가 넘어왔을경우
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.debug("front 로부터 회원정보 받음 -> auth server start");
        ObjectMapper objectMapper = new ObjectMapper();

        ClientLoginRequestDto clientLoginRequestDto;

        try{
            clientLoginRequestDto = objectMapper.readValue(request.getInputStream(), ClientLoginRequestDto.class);
            log.debug("Attempt authentication email -> {}",clientLoginRequestDto.getEmail());
            log.debug("Attempt authentication password -> {}",clientLoginRequestDto.getPassword());
        } catch (IOException e) {
            throw new ClientLoginException("Invalid login request");
        }

        String email = clientLoginRequestDto.getEmail();
        String password = clientLoginRequestDto.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.debug("successfulAuthentication -> 인증 성공");

        if (Objects.isNull(authResult)) {
            throw new ClientNotFoundException(Errors.MEMBER_NOT_FOUND);
        }
        String email = authResult.getName();
        List<String> authorities = getAuthorities(authResult.getAuthorities());

        log.debug("email -> {}", email);
        log.debug("authorites -> {}", authorities);

        String emailUuid = UUID.randomUUID().toString();

        String accessToken = jwtUtil.createAccessToken(email,authorities);
        String refreshToken = jwtUtil.createRefreshToken(email, authorities);
        Date expiredTime = jwtUtil.getExpiredTime(accessToken);

        redisTemplate.opsForHash().put(emailUuid, JWTEnums.ACCESS_TOKEN.getName(),accessToken);
        redisTemplate.opsForHash().put(emailUuid,JWTEnums.REFRESH_TOKEN.getName(),refreshToken);
        redisTemplate.opsForHash().put(emailUuid,JWTEnums.EMAIL_ID.getName(),email);
        redisTemplate.opsForHash().put(emailUuid,JWTEnums.PRINCIPAL.getName(),authResult.getAuthorities().toString());

        response.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken);
        response.addHeader(JWTEnums.HEADER_UUID.getName(), emailUuid);
        response.addHeader(JWTEnums.HEADER_EXPIRED_TIME.getName(), String.valueOf(expiredTime));

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
