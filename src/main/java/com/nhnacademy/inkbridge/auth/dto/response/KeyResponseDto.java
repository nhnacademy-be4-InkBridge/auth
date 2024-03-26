package com.nhnacademy.inkbridge.auth.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * class: KeyResponseDto.
 *
 * @author devminseo
 * @version 3/26/24
 */
@Getter
public class KeyResponseDto {
    private Header header;
    private Body body;

    @Getter
    @NoArgsConstructor
    public static class Body {
        private String secret;
    }

    @Getter
    @NoArgsConstructor
    public static class Header {
        private Integer resultCode;
        private String resultMessage;
        private boolean isSuccessful;
    }
}