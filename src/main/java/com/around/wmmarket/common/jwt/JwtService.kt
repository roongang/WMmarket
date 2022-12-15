package com.around.wmmarket.common.jwt

import com.around.wmmarket.common.error.CustomException
import com.around.wmmarket.common.error.ErrorCode
import com.around.wmmarket.common.jwt.refreshToken.RefreshTokenEntity
import com.around.wmmarket.common.jwt.refreshToken.RefreshTokenEntityRepository
import com.around.wmmarket.controller.dto.user.UserSignInRequestDto
import com.around.wmmarket.domain.user.SignedUser
import com.around.wmmarket.domain.user.UserRepository
import com.around.wmmarket.service.user.CustomUserDetailsService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JwtService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenEntityRepository: RefreshTokenEntityRepository,
    private val customUserDetailsService: CustomUserDetailsService,
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository
) {
    @Transactional
    fun createTokenDTO(requestDTO: UserSignInRequestDto): TokenDTO {
        // authenticate
        val signedUser: SignedUser
        try{
            signedUser=customUserDetailsService.getSignedUser(requestDTO)
        } catch (e: CustomException) {
            throw CustomException(ErrorCode.USER_NOT_FOUND)
        }

        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(signedUser.username, signedUser.password))
        } catch (e: Exception) {
            throw CustomException(ErrorCode.INVALID_USER_PASSWORD)
        }
        // get user
        val user = userRepository.findByEmail(signedUser.username)
            .orElseThrow { throw CustomException(ErrorCode.USER_NOT_FOUND) }
        // create token
        // TODO : Kotlin 으로 변경하면 getter 없애기
        val tokenDto = jwtTokenProvider.createTokenDTO(user.getEmail(), user.getUserRoles().map { it.getRole().getName() })

        // save refresh token
        refreshTokenEntityRepository.deleteByKey(tokenDto.key)
        refreshTokenEntityRepository.save(RefreshTokenEntity(tokenDto.refreshToken, tokenDto.key))

        // return tokenDto
        return tokenDto
    }
}