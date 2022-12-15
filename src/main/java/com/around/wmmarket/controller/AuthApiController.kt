package com.around.wmmarket.controller

import com.around.wmmarket.common.Constants
import com.around.wmmarket.common.ResponseHandler
import com.around.wmmarket.common.SuccessResponse
import com.around.wmmarket.common.jwt.JwtService
import com.around.wmmarket.controller.dto.user.UserSignInRequestDto
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

import javax.validation.Valid;

@Validated
@RequestMapping(Constants.API_PATH)
@RestController
class AuthApiController(
    private val jwtService: JwtService
) {
    // signin
    @ApiOperation(value = "로그인")
    @ApiResponse(code = 201, message = "로그인 성공")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signin")
    fun signin(@Valid @RequestBody request: UserSignInRequestDto) : ResponseEntity<Any>? {
        // TODO : Kotlin 으로 변경하면 name 추가하기
        return ResponseHandler.toResponse(SuccessResponse(
            HttpStatus.CREATED,
            "로그인 성공",
            jwtService.createTokenDTO(request))
        )
    }
    // signout
    @ApiOperation(value = "로그아웃")
    @ApiResponse(code = 200, message = "로그아웃 성공")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/signout")
    fun signout(request: HttpServletRequest) : ResponseEntity<Any>? {
        // TODO : 로그아웃
        jwtService.expireToken(request)
        return ResponseHandler.toResponse(SuccessResponse(
            HttpStatus.OK,
            "로그아웃 성공",
            null)
        )
    }

    // refresh
    @ApiOperation(value = "토큰 재발급")
    @ApiResponse(code = 201, message = "토큰 재발급 성공")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/refresh")
    fun refresh(request : HttpServletRequest) : ResponseEntity<Any>? {
        return ResponseHandler.toResponse(SuccessResponse(
            HttpStatus.CREATED,
            "토큰 재발급 성공",
            jwtService.reissueTokenDto(request))
        )
    }
}