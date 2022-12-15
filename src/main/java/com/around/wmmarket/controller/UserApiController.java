package com.around.wmmarket.controller;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.ResourceResponse;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.around.wmmarket.controller.dto.mannerReview.MannerReviewSaveRequestDto;
import com.around.wmmarket.controller.dto.user.*;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.user.UserService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

@Slf4j
@Validated
@RequestMapping(Constants.API_PATH)
@RequiredArgsConstructor
@RestController
public class UserApiController {
    private final UserService userService;
    private final ResourceLoader resourceLoader;
    private final Tika tika=new Tika();

    @ApiOperation(value = "유저 삽입") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201, message = "CREATED"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @PostMapping("/users")
    public ResponseEntity<Object> save(@Valid @ModelAttribute UserSaveRequestDto requestDto){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("유저 회원가입 성공했습니다.")
                .data(userService.save(requestDto))
                .build());
    }


    @ApiOperation(value = "유저 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : user info",response = UserGetResponseDto.class),
    }) // SWAGGER
    @GetMapping("/users/{userId}")
    public ResponseEntity<Object> get(
            @Min(1) @PathVariable("userId") Integer userId){
        UserGetResponseDto responseDto=userService.getUserDto(userId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 반환 성공했습니다.")
                .data(responseDto!=null
                        ? responseDto
                        : Collections.emptyList())
                .build());
    }

    @ApiOperation(value = "유저 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : user info",response = UserGetResponseDto.class),
    }) // SWAGGER
    @GetMapping("/users")
    public ResponseEntity<Object> getByQuery(@Valid UserGetRequestDto requestDto) {
        UserGetResponseDto responseDto=userService.getUserDto(requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 반환 성공했습니다.")
                .data(responseDto!=null
                        ? responseDto
                        : Collections.emptyList())
                .build());
    }

    @ApiOperation(value = "유저 수정") // SWAGGER
    @PutMapping("/users/{userId}")
    public ResponseEntity<Object> update(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                         @Min(1) @PathVariable("userId") Integer userId,
                                         @Valid @RequestBody UserUpdateRequestDto requestDto) {
        // update
        userService.update(signedUser,userId,requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 수정 성공했습니다.")
                .build());
    }

    // TODO : 유저 삭제가 이뤄지면 안됨, Email 이 후보키이므로
    @ApiOperation(value = "유저 삭제") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "remove session"),
    }) // SWAGGER
    @Transactional
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Object> delete(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                         @ApiIgnore HttpSession session,
                                         @Min(1) @PathVariable Integer userId){
        // delete
        userService.delete(signedUser,userId,session);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 삭제 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "유저 이미지 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return Image File"),
    }) // SWAGGER
    @GetMapping("/users/{userId}/image")
    public ResponseEntity<Object> getImage(
            @Min(1) @PathVariable("userId") Integer userId) {
        String fileName= userService.getImage(userId);
        if(fileName==null) {
            return ResponseHandler.toResponse(SuccessResponse.builder()
                    .status(HttpStatus.OK)
                    .message("유저 이미지 반환 성공했습니다.(이미지 존재하지 않음)")
                    .data(Collections.emptyList())
                    .build());
        }
        Resource resource=resourceLoader.getResource("file:"+ Paths.get(Constants.userImagePath.toString(),fileName));

        File file;
        try { file = resource.getFile(); }
        catch (IOException e) { throw new CustomException(ErrorCode.FILE_NOT_FOUND); }
        String mediaType;
        try { mediaType = tika.detect(file); }
        catch (IOException e) { throw new CustomException(ErrorCode.MEDIA_TYPE_NOT_FOUND); }

        HttpHeaders headers=new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+resource.getFilename()+"\"");
        headers.add(HttpHeaders.CONTENT_TYPE,mediaType);
        headers.add(HttpHeaders.CONTENT_LENGTH,String.valueOf(file.length()));

        return ResponseHandler.toResponse(ResourceResponse.builder()
                .status(HttpStatus.OK)
                .headers(headers)
                .message("유저 이미지 반환 성공했였습니다.")
                .resource(resource).build());
    }

    @ApiOperation(value = "유저 이미지 수정") // SWAGGER
    @PutMapping("/users/{userId}/image")
    public ResponseEntity<Object> updateImage(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                              @Min(1) @PathVariable("userId") Integer userId,
                                              @ApiParam(value = "이미지",allowMultiple = true,required = true)
                                              @NotNull @RequestPart MultipartFile file) {
        userService.updateImage(signedUser,userId,file);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 이미지 수정 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "유저 이미지 삭제") // SWAGGER
    @Transactional
    @DeleteMapping("/users/{userId}/image")
    public ResponseEntity<Object> deleteImage(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                              @Min(1) @PathVariable("userId") Integer userId) {
        // check
        userService.deleteImage(signedUser,userId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 이미지 삭제 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "유저 좋아요 삽입") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201,message = "CREATED"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @PostMapping("/users/{userId}/likes")
    public ResponseEntity<Object> saveLike(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                           @Min(1) @PathVariable("userId") Integer userId,
                                           @Valid @RequestBody UserLikeSaveRequestDto requestDto) {
        userService.saveLike(signedUser,userId,requestDto.getDealPostId());
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("유저 좋아요 삽입 성공하였습니다.")
                .build());
    }

    @ApiOperation(value = "유저 좋아요한 거래글 리스트 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : List DealPostGetResponseDto",response = DealPostGetResponseDto.class),
    }) // SWAGGER
    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<Object> getLikes(
            @Min(1) @PathVariable("userId") Integer userId) {
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 좋아요한 거래글 반환 성공했습니다.")
                .data(userService.getLikes(userId)).build());
    }

    @ApiOperation(value = "유저 좋아요 삭제") // SWAGGER
    @DeleteMapping("/users/{userId}/likes")
    public ResponseEntity<Object> deleteLike(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                             @Min(1) @PathVariable("userId") Integer userId,
                                             @Valid @RequestBody UserLikeDeleteRequestDto requestDto) {
        userService.deleteLike(signedUser,userId,requestDto.getDealPostId());
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 좋아요 삭제 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "유저 거래 글 리스트 반환")
    @GetMapping("/users/{userId}/deal-posts")
    public ResponseEntity<Object> getDealPosts(
            @Min(1) @PathVariable("userId") Integer userId){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 거래글 리스트 반환 성공했습니다.")
                .data(userService.getDealPosts(userId))
                .build());
    }

    // manner review
    @ApiOperation(value = "유저 구매 매너 리뷰 삽입")
    @PostMapping("/users/{userId}/buy-manner-reviews")
    public ResponseEntity<Object> saveBuyMannerReview(@AuthenticationPrincipal SignedUser signedUser,
                                                       @Min(1) @PathVariable Integer userId,
                                                       @RequestBody MannerReviewSaveRequestDto requestDto){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("매너 리뷰 삽입 성공했습니다.")
                .data(userService.saveBuyMannerReview(signedUser,userId,requestDto))
                .build());
    }
    
    @ApiOperation(value = "유저 판매 매너 리뷰 반환")
    @GetMapping("/users/{userId}/sell-manner-reviews")
    public ResponseEntity<Object> getSellMannerReviews(@Min(1) @PathVariable Integer userId){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 판매 매너 리뷰 리스트 반환 성공했습니다.")
                .data(userService.getSellMannerReviews(userId))
                .build());
    }

    @ApiOperation(value = "유저 구매 매너 리뷰 반환")
    @GetMapping("/users/{userId}/buy-manner-reviews")
    public ResponseEntity<Object> getBuyMannerReviews(@Min(1) @PathVariable Integer userId){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 구매 매너 리뷰 리스트 반환 성공했습니다.")
                .data(userService.getBuyMannerReviews(userId))
                .build());
    }
    @DeleteMapping("/users/{userId}/buy-manner-reviews/{mannerReviewId}")
    public ResponseEntity<Object> deleteBuyMannerReview(@AuthenticationPrincipal SignedUser signedUser,
                                                        @Min(1) @PathVariable Integer userId,
                                                        @Min(1) @PathVariable Integer mannerReviewId){
        userService.deleteBuyMannerReview(signedUser,userId,mannerReviewId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 구매 매너 리뷰 삭제 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "유저 검색")
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : slice",response = Slice.class),
    })
    @GetMapping("/users/page")
    public ResponseEntity<Object> searchUser(UserSearchRequestDto requestDto){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 검색 성공했습니다.")
                .data(userService.findByFilter(requestDto))
                .build());
    }
    // auth
    @ApiOperation(value = "유저 인증 코드 발송")
    @PostMapping("/users/{userId}/code")
    public ResponseEntity<Object> sendCode(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser
            ,@Min(1) @PathVariable Integer userId){
        userService.setAuthCode(signedUser,userId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("유저 인증 메일 발송 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "유저 인증")
    @PostMapping("/users/{userId}/auth")
    public ResponseEntity<Object> authUser(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                           @Min(1) @PathVariable Integer userId,
                                           @RequestBody ObjectNode json){
        userService.authUser(signedUser,userId,json.get("code")!=null?json.get("code").asText():null);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("유저 인증 성공했습니다.")
                .build());
    }
    // keyword
    @ApiOperation(value = "유저 키워드 반환")
    @GetMapping("/users/{userId}/keywords")
    public ResponseEntity<Object> getUserKeyWords(@AuthenticationPrincipal SignedUser signedUser,
                                                  @PathVariable("userId") Integer userId){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .data(userService.getKeyWords(signedUser,userId))
                .message("유저 키워드 반환 성공했습니다.")
                .build());
    }
    @ApiOperation(value = "유저 키워드 삭제")
    @DeleteMapping("/users/{userId}/keywords")
    public ResponseEntity<Object> deleteUserKeyWordByWord(@AuthenticationPrincipal SignedUser signedUser,
                                                          @PathVariable("userId") Integer userId,
                                                          @NotNull @RequestParam String word){
        userService.deleteKeyWordByWord(signedUser,userId,word);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 키워드 삭제 성공했습니다.").build());
    }
}
