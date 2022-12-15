package com.around.wmmarket.common.jwt

class TokenDTO(accessToken: String,
               refreshToken: String,
               key: String){
    // 생성자로만 init 하고 setter 막음
    var accessToken: String = accessToken
        private set
    var refreshToken: String = refreshToken
        private set
    var key: String = key
        private set
}