package com.nhnacademy.inkbridge.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.inkbridge.auth.filter.CustomAuthenticationFilter;
import com.nhnacademy.inkbridge.auth.handler.JwtFailHandler;
import com.nhnacademy.inkbridge.auth.provider.CustomAuthenticationProvider;
import com.nhnacademy.inkbridge.auth.provider.JwtProvider;
import com.nhnacademy.inkbridge.auth.service.impl.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * class: SecurityConfig.
 *
 * @author devminseo
 * @version 2/21/24
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final MetaDataProperties metaDataProperties;
    private final CustomUserDetailService customUserDetailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable();
//        http
//                .cors().disable();
        http
                .formLogin().disable();
        http
                .logout().disable();
        http
                .httpBasic().disable();
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .addFilterAt(customAuthenticationFilter(null), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter(JwtProvider jwtProvider) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter =
                new CustomAuthenticationFilter(customAuthenticationProvider(), jwtProvider,redisTemplate,objectMapper);
        customAuthenticationFilter.setAuthenticationFailureHandler(failureHandler());
        customAuthenticationFilter.setAuthenticationManager(authenticationManager(null));
        customAuthenticationFilter.setUsernameParameter("email");
        customAuthenticationFilter.setFilterProcessesUrl("/auth/login");
        return customAuthenticationFilter;
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(customUserDetailService);
        return provider;
    }

    /**
     * 비밀번호를 다이제스트로 바꿔주는 메서드 .
     *
     * @return 암호화 인코더 반환 .
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return new JwtFailHandler(metaDataProperties);
    }


}