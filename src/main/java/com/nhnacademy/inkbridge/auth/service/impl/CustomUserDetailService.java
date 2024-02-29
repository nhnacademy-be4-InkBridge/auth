package com.nhnacademy.inkbridge.auth.service.impl;

import com.nhnacademy.inkbridge.auth.adaptor.MemberLoginAdaptor;
import com.nhnacademy.inkbridge.auth.dto.request.MemberLoginRequestDto;
import com.nhnacademy.inkbridge.auth.dto.response.MemberLoginResponseDto;
import com.nhnacademy.inkbridge.auth.exception.NotFoundUserException;
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
            log.debug("loadUserByUsername start ->");

        try {

            response = memberLoginAdaptor.login(new MemberLoginRequestDto(email));
            log.debug("DB에서 가져오는거 성공 ->");
            MemberLoginResponseDto responseDto = response.getBody();
            log.debug("loadUserByUsername responseDto create ->");

            return new User(Objects.requireNonNull(responseDto).getMemberId().toString(), responseDto.getPassword(),
                    getAuthorities(responseDto));

        } catch (Exception e) {
            throw new NotFoundUserException();
        }
    }

    private List<? extends GrantedAuthority> getAuthorities(MemberLoginResponseDto dto) {
        return dto.getRole()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
