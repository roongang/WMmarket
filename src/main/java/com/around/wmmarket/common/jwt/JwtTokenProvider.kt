package com.around.wmmarket.common.jwt

import com.around.wmmarket.common.error.CustomException
import com.around.wmmarket.common.error.ErrorCode
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
class JwtTokenProvider(private val customUserDetailsService: CustomUserDetailsService) {
    // TODO : application.yml 로 빼기
    private var accessSecretKey = "wmmarket_accessSecretKey"
    private var refreshSecretKey = "wmmarket_refreshSecretKey"
    //private val accessTokenValidTime = 30 * 60 * 1000L //  30분
    private val accessTokenValidTime = 10 * 1000L // 10초
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
                key = userEmail)
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

    fun resolveAccessToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }

    fun resolveRefreshToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("RefreshToken")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }

    fun validateAccessToken(accessToken: String): Boolean {
        val claims = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(accessToken).body
        return !claims.expiration.before(Date())

    }

    fun validateRefreshToken(refreshToken: String): Boolean {
        val claims = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken).body
        return !claims.expiration.before(Date())
    }

    fun expiredAccessToken(accessToken: String?) {
        try{
            val claims=Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(accessToken).body
            // 만료시간을 현재로 변경
            claims.expiration = Date()
        } catch (e: Exception) {
            return
        }
    }

    fun expiredRefreshToken(refreshToken: String?) {
        try{
            val claims=Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken).body
            // 만료시간을 현재로 변경
            claims.expiration = Date()
        } catch (e: Exception) {
            return
        }
    }
}