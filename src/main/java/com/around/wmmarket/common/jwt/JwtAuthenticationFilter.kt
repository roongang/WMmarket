package com.around.wmmarket.common.jwt

import com.around.wmmarket.common.error.CustomException
import com.around.wmmarket.common.error.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import kotlin.math.log


class JwtAuthenticationFilter(private val jwtTokenProvider: JwtTokenProvider) : GenericFilterBean() {
    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val accessToken = jwtTokenProvider.resolveAccessToken(request as HttpServletRequest)
        if (accessToken != null) {
            logger.info("accessToken : $accessToken")
            if(jwtTokenProvider.validateAccessToken(accessToken)){
                logger.info("doFilter validateToken: $accessToken")
                val authentication = jwtTokenProvider.getAuthentication(accessToken)
                // AuthenticationPrincipal 에서 사용하기 위해 SecurityContext 에 저장
                SecurityContextHolder.getContext().authentication = authentication
            }
            else{
                logger.info("doFilter accessToken is not valid")
                // accessToken 만료 신호보내기
                throw CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED)
            }
        }
        chain?.doFilter(request, response)
    }
}