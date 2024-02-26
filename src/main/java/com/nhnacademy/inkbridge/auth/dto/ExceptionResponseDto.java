package com.nhnacademy.inkbridge.auth.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * class: ExceptionResponseDto.
 *
 * @author devminseo
 * @version 2/26/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExceptionResponseDto {
    private boolean success;
    @JsonIgnore
    private HttpStatus status;
    private String errorMsg;

    @JsonGetter
    public int getStatus() {
        return this.status.value();
    }

    @Builder
    public ExceptionResponseDto(boolean success, HttpStatus status, String errorMsg) {
        this.success = success;
        this.status = status;
        this.errorMsg = errorMsg;
    }
}
