package com.around.wmmarket.common.jwt

import com.around.wmmarket.common.error.CustomException
import com.around.wmmarket.common.error.ErrorCode
import com.around.wmmarket.domain.user.UserRepository
import com.around.wmmarket.service.user.CustomUserDetailsService
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
    private var secretKey = "wmmarketSecretKey"
    private val tokenValidTime = 30 * 60 * 1000L

    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }

    fun createToken(userId: Int, roles: List<String>): String {
        val claims = Jwts.claims().setSubject(userId.toString())
        claims["roles"] = roles
        val now = Date()
        val validity = Date(now.time + tokenValidTime)
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun getAuthentication(token: String) : Authentication {
        val userDetails = customUserDetailsService.loadUserByUsername(getUserEmail(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getUserEmail(token: String): String {
        val userId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject.toString().toInt()
        val user = userRepository.findById(userId)
            .orElseThrow(){ CustomException(ErrorCode.USER_NOT_FOUND) }
        return user.email
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }

    fun validateToken(token: String): Boolean {
        try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            return !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}