package com.around.wmmarket.controller.dto.signin

import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class SigninRequestDto(
    email: String,
    password: String
) {
    @ApiModelProperty(value = "유저 이메일", example = "test_email@email.com", required = true)
    @NotBlank @Email
    var email: String = email
        private set

    @ApiModelProperty(value = "유저 비밀번호", example = "test_password", required = true)
    @NotBlank
    var password: String = password
        private set
}