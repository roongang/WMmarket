package com.around.wmmarket.controller;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.controller.dto.keyword.KeyWordGetResponseDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.keyword.KeyWordService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Collections;

@Validated
@RequestMapping(Constants.API_PATH)
@RequiredArgsConstructor
@RestController
public class KeyWordApiController {
    private final KeyWordService keyWordService;

    @ApiOperation(value = "키워드 삽입")
    @PostMapping("/keywords")
    public ResponseEntity<Object> save(@AuthenticationPrincipal SignedUser signedUser,
                                       @NotNull @RequestParam String word){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .data(keyWordService.save(signedUser,word))
                .message("키워드 삽입 성공했습니다.")
                .build());
    }
    @ApiOperation(value = "키워드 반환(ID 기반)")
    @GetMapping("/keywords/{keywordId}")
    public ResponseEntity<Object> get(@AuthenticationPrincipal SignedUser signedUser,
                                      @PathVariable("keywordId") Integer keywordId){
        KeyWordGetResponseDto response=keyWordService.getKeyWordGetResponse(signedUser,keywordId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .data(response!=null
                        ? response
                        : Collections.emptyList())
                .message("키워드 조회 성공했습니다.")
                .build());
    }
    @ApiOperation(value = "키워드 반환(단어 기반)")
    @GetMapping("/keywords")
    public ResponseEntity<Object> getByWord(@AuthenticationPrincipal SignedUser signedUser,
                                      @NotNull @RequestParam String word){
        KeyWordGetResponseDto response=keyWordService.getKeyWordGetResponse(signedUser,word);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .data(response!=null
                        ? response
                        : Collections.emptyList())
                .message("키워드 조회 성공했습니다.")
                .build());
    }
    @ApiOperation(value = "키워드 삭제(ID 기반)")
    @DeleteMapping("/keywords/{keywordId}")
    public ResponseEntity<Object> delete(@AuthenticationPrincipal SignedUser signedUser,
                                         @PathVariable("keywordId") Integer keywordId){
        keyWordService.delete(signedUser,keywordId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("키워드 삭제 성공했습니다.")
                .build());
    }
    @ApiOperation(value = "키워드 삭제(단어 기반)")
    @DeleteMapping("/keywords")
    public ResponseEntity<Object> deleteByWord(@AuthenticationPrincipal SignedUser signedUser,
                                               @NotNull @RequestParam String word){
        keyWordService.deleteByWord(signedUser,word);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("키워드 삭제 성공했습니다.")
                .build());
    }
}
