package com.around.wmmarket.common.jwt

import com.around.wmmarket.common.error.CustomException
import com.around.wmmarket.common.error.ErrorCode
import com.around.wmmarket.common.jwt.refreshToken.RefreshTokenEntity
import com.around.wmmarket.common.jwt.refreshToken.RefreshTokenEntityRepository
import com.around.wmmarket.controller.dto.user.UserSignInRequestDto
import com.around.wmmarket.domain.user.SignedUser
import com.around.wmmarket.domain.user.UserRepository
import com.around.wmmarket.service.user.CustomUserDetailsService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

@Service
class JwtService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenEntityRepository: RefreshTokenEntityRepository,
    private val customUserDetailsService: CustomUserDetailsService,
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(JwtService::class.java)
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

    @Transactional
    fun reissueTokenDto(request: HttpServletRequest): TokenDTO {
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

        // return tokenDto
        return jwtTokenProvider.createTokenDTO(user.getEmail(), user.getUserRoles().map { it.getRole().getName() })
    }

    @Transactional
    fun expireToken(request: HttpServletRequest) {
        // expire access token
        jwtTokenProvider.expiredAccessToken(jwtTokenProvider.resolveAccessToken(request))

        // expire refresh token
        val refreshToken = jwtTokenProvider.resolveRefreshToken(request)
        jwtTokenProvider.expiredRefreshToken(refreshToken)

        // delete refresh token entity
        refreshTokenEntityRepository.deleteByKey(refreshToken)
    }
}