package com.nhnacademy.inkbridge.auth.handler;

import com.nhnacademy.inkbridge.auth.config.MetaDataProperties;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * class: JwtFailHandler.
 *
 * @author devminseo
 * @version 2/26/24
 */
@RequiredArgsConstructor
public class JwtFailHandler implements AuthenticationFailureHandler {
    private final MetaDataProperties metaDataProperties;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.sendRedirect(metaDataProperties.getFront()+"/login");
    }
}
