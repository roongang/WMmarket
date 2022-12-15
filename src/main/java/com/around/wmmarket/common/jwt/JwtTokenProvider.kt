package com.around.wmmarket.common.jwt

import com.around.wmmarket.common.jwt.refreshToken.RefreshTokenEntity
import com.around.wmmarket.domain.user.UserRepository
import com.around.wmmarket.service.user.CustomUserDetailsService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(private val customUserDetailsService: CustomUserDetailsService,
                       private val userRepository: UserRepository) {
    private var accessSecretKey = "wmmarket_accessSecretKey"
    private var refreshSecretKey = "wmmarket_refreshSecretKey"
    private val accessTokenValidTime = 30 * 60 * 1000L //  30분
    private val refreshTokenValidTime = 7 * 24 * 60 * 60 * 1000L // 7일
    private val signatureAlgorithm = SignatureAlgorithm.HS256

    @PostConstruct
    protected fun init() {
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.toByteArray())
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.toByteArray())
    }

    fun createTokenDTO(userEmail: String, roles: List<String>): TokenDTO {
        val claims = Jwts.claims().setSubject(userEmail)
        claims["roles"] = roles
        val now = Date()

        return TokenDTO(accessToken = createAccessToken(claims, now),
                refreshToken = createRefreshToken(claims, now),
                key = accessSecretKey)
    }

    fun createAccessToken(claims: Claims, now: Date): String {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(Date(now.time + accessTokenValidTime))
                .signWith(signatureAlgorithm, accessSecretKey)
                .compact()
    }

    fun createRefreshToken(claims: Claims,now: Date): String {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(Date(now.time + refreshTokenValidTime))
                .signWith(signatureAlgorithm, refreshSecretKey)
                .compact()
    }

    fun getAuthentication(token: String) : Authentication {
        val userDetails = customUserDetailsService.loadUserByUsername(getUserEmail(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getUserEmail(token: String): String {
        return Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).body.subject.toString()
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }

    fun reIssueTokenDTO(refreshTokenEntity: RefreshTokenEntity): TokenDTO? {
        val refreshToken = refreshTokenEntity.refreshToken

        try {
            val claims = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken)
            // 토큰이 만료되지 않았다면 새로운 tokenDTO 을 발급
            if(!claims.body.expiration.before(Date())) {
                return createTokenDTO(claims.body["sub"].toString(), claims.body["roles"] as List<String>)
            }
        } catch (e: Exception) {
            return null
        }

        return null
    }

    fun validateToken(token: String): Boolean {
        val claims = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).body
        return !claims.expiration.before(Date())
    }
}