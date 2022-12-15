package com.around.wmmarket.controller.dto.signin

class TokenResponseDto(
    accessToken: String,
    refreshToken: String
) {
    var accessToken: String = accessToken
        private set
    var refreshToken: String = refreshToken
        private set
}