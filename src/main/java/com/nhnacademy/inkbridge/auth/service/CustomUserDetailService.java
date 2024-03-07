package com.nhnacademy.inkbridge.auth.service;

import com.nhnacademy.inkbridge.auth.adaptor.MemberLoginAdaptor;
import com.nhnacademy.inkbridge.auth.dto.request.MemberLoginRequestDto;
import com.nhnacademy.inkbridge.auth.dto.response.MemberLoginResponseDto;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * class: CustomUserDetailService.
 *
 * @author devminseo
 * @version 2/24/24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {
    private final MemberLoginAdaptor memberLoginAdaptor;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ResponseEntity<MemberLoginResponseDto> response;

        try {

            response = memberLoginAdaptor.login(new MemberLoginRequestDto(email));
            MemberLoginResponseDto responseDto = response.getBody();

            return new User(Objects.requireNonNull(responseDto).getMemberId().toString(), responseDto.getPassword(),
                    getAuthorities(responseDto));

        } catch (HttpClientErrorException e) {
            log.error("회원을 찾을 수 없습니다.");
            throw new UsernameNotFoundException("회원을 찾을 수 없습니다.");
        }
    }

    private List<? extends GrantedAuthority> getAuthorities(MemberLoginResponseDto dto) {
        return dto.getRole()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
