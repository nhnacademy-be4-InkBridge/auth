package com.nhnacademy.inkbridge.auth.adaptor;

import com.nhnacademy.inkbridge.auth.config.MetaDataProperties;
import com.nhnacademy.inkbridge.auth.dto.request.MemberLoginRequestDto;
import com.nhnacademy.inkbridge.auth.dto.response.MemberLoginResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * class: MemberLoginAdaptor.
 *
 * @author devminseo
 * @version 2/22/24
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MemberLoginAdaptor {
    private final RestTemplate restTemplate;
    private final MetaDataProperties metaDataProperties;

    /**
     * api 서버로 로그인 요청하는 메서드.
     * @param requestDto 로그인 요청 정보
     * @return 로그인 성공후 회원 정보
     */
    public ResponseEntity<MemberLoginResponseDto> login(MemberLoginRequestDto requestDto) {
        log.info("MemberLoginAdaptor start ->");

        return restTemplate.exchange(
                metaDataProperties.getGateway() + "/api/member/login",
//                "http://localhost:8080/api/member/login",
                HttpMethod.POST,
                new HttpEntity<>(requestDto, createHttpHeaders()),
                new ParameterizedTypeReference<>() {}
        );
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
