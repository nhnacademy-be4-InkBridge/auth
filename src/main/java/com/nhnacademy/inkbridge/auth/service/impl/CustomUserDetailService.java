package com.nhnacademy.inkbridge.auth.service.impl;

import com.nhnacademy.inkbridge.auth.adaptor.MemberLoginAdaptor;
import com.nhnacademy.inkbridge.auth.dto.MemberLoginRequestDto;
import com.nhnacademy.inkbridge.auth.dto.MemberLoginResponseDto;
import com.nhnacademy.inkbridge.auth.exception.NotFoundUserException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * class: CustomUserDetailService.
 *
 * @author devminseo
 * @version 2/24/24
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final MemberLoginAdaptor memberLoginAdaptor;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ResponseEntity<MemberLoginResponseDto> response;

        try {
            response = memberLoginAdaptor.login(new MemberLoginRequestDto(email));

            MemberLoginResponseDto responseDto = response.getBody();

            return new User(Objects.requireNonNull(responseDto).getEmail(), responseDto.getPassword(),
                    getAuthorities(responseDto));

        } catch (Exception e) {
            throw new NotFoundUserException("잉크 브릿지의 회원이 아닙니다.");
        }
    }

    private List<? extends GrantedAuthority> getAuthorities(MemberLoginResponseDto dto) {
        return dto.getRole()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
