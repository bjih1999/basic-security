package io.security.controller.login.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginExceptionDto {

    private String error;

    private String exception;
}
