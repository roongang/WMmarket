package com.around.wmmarket.common

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ExceptionHandlerFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            // TODO : CustomException 으로 변경
            when (e) {
                is ExpiredJwtException -> {
                    response.sendError(401, "ExpiredJwtException")
                }
                is JwtException -> {
                    response.sendError(401, "JwtException")
                }
                is IllegalArgumentException -> {
                    response.sendError(400, "IllegalArgumentException")
                }
                else -> {
                    response.sendError(500, "Exception")
                }
            }
        }
    }
}