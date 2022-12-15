package com.around.wmmarket.service.user

import com.around.wmmarket.common.error.CustomException
import com.around.wmmarket.common.error.ErrorCode
import com.around.wmmarket.common.jwt.JwtTokenProvider
import com.around.wmmarket.controller.dto.signin.SigninRequestDto
import com.around.wmmarket.controller.dto.signin.TokenResponseDto
import com.around.wmmarket.domain.refresh_token.RefreshTokenEntity
import com.around.wmmarket.domain.refresh_token.RefreshTokenEntityRepository
import com.around.wmmarket.domain.user.SignedUser
import com.around.wmmarket.domain.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

@Service
class SignService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenEntityRepository: RefreshTokenEntityRepository,
    private val customUserDetailsService: CustomUserDetailsService,
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(SignService::class.java)
    @Transactional
    fun createToken(requestDTO: SigninRequestDto): TokenResponseDto {
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

        // return
        return TokenResponseDto(
            accessToken = tokenDto.accessToken,
            refreshToken = tokenDto.refreshToken
        )
    }

    @Transactional
    fun reissueToken(request: HttpServletRequest): TokenResponseDto {
        // resolve refresh token
        val refreshToken = jwtTokenProvider.resolveRefreshToken(request)
                ?: throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        // validate refresh token
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        }
        // get refresh tokenDto
        val refreshTokenEntity = refreshTokenEntityRepository.findByRefreshToken(refreshToken)
                ?: throw CustomException(ErrorCode.INVALID_REFRESH_TOKEN)

        // get user
        logger.info("reissueTokenDto refreshTokenEntity.key: ${refreshTokenEntity.key}")
        val user = userRepository.findByEmail(refreshTokenEntity.key)
                .orElseThrow { throw CustomException(ErrorCode.USER_NOT_FOUND) }

        // get tokenDto
        val tokenDto = jwtTokenProvider.createTokenDTO(user.getEmail(), user.getUserRoles().map { it.getRole().getName() })

        // return
        return TokenResponseDto(
            accessToken = tokenDto.accessToken,
            refreshToken = tokenDto.refreshToken
        )
    }

    @Transactional
    fun expireToken(request: HttpServletRequest) {
        // expire access token
        jwtTokenProvider.expireAccessToken(jwtTokenProvider.resolveAccessToken(request))

        // expire refresh token
        val refreshToken = jwtTokenProvider.resolveRefreshToken(request)
        jwtTokenProvider.expireRefreshToken(refreshToken)

        // delete refresh token entity
        refreshTokenEntityRepository.deleteByKey(refreshToken)
    }
}