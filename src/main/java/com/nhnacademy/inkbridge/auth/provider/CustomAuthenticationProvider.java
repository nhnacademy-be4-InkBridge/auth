package com.nhnacademy.inkbridge.auth.provider;

import com.nhnacademy.inkbridge.auth.exception.ProviderNotMatchesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * class: CustomAuthenticationProvider.
 *
 * @author devminseo
 * @version 2/27/24
 */
@Slf4j
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        UserDetails user = this.getUserDetailsService().loadUserByUsername(email);

        if (!this.getPasswordEncoder().matches(password, user.getPassword())) {
            log.debug("provider not matches");
            throw new ProviderNotMatchesException();
        }

        return new UsernamePasswordAuthenticationToken(user.getUsername(),
                "",
                user.getAuthorities());
    }
}
