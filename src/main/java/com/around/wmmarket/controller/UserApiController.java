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
import com.around.wmmarket.service.user.SignService;
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
    private final SignService signService;
    private final ResourceLoader resourceLoader;
    private final Tika tika=new Tika();

    @ApiOperation(value = "?????? ??????") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201, message = "CREATED"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @Transactional
    @PostMapping("/users")
    public ResponseEntity<Object> save(@Valid @ModelAttribute UserSaveRequestDto requestDto){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("?????? ???????????? ??????????????????.")
                .data(userService.save(requestDto))
                .build());
    }

    // TODO : ??????????????? ?????? ???????????? ??????
    @ApiOperation(value = "?????? ?????????") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201,message = "set session"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @Transactional
    @PostMapping("/signin")
    public ResponseEntity<Object> signin(@Valid @RequestBody UserSignInRequestDto requestDto,
                                         @ApiIgnore HttpSession session){
        signService.signin(requestDto,session);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                        .status(HttpStatus.CREATED)
                        .message("?????? ????????? ??????????????????.").build());
    }
    @ApiOperation(value = "?????? ????????????") // SWAGGER
    @PostMapping("/signout")
    public ResponseEntity<Object> signout(@ApiIgnore HttpSession session){
        signService.signout(session);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ???????????? ??????????????????.")
                .build());
    }

    @ApiOperation(value = "?????? ??????") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : user info",response = UserGetResponseDto.class),
    }) // SWAGGER
    @GetMapping("/users/{userId}")
    public ResponseEntity<Object> get(
            @Min(1) @PathVariable("userId") Integer userId){
        UserGetResponseDto responseDto=userService.getUserDto(userId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ?????? ??????????????????.")
                .data(responseDto!=null
                        ? responseDto
                        : Collections.emptyList())
                .build());
    }

    @ApiOperation(value = "?????? ??????") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : user info",response = UserGetResponseDto.class),
    }) // SWAGGER
    @GetMapping("/users")
    public ResponseEntity<Object> getByQuery(@Valid UserGetRequestDto requestDto) {
        UserGetResponseDto responseDto=userService.getUserDto(requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ?????? ??????????????????.")
                .data(responseDto!=null
                        ? responseDto
                        : Collections.emptyList())
                .build());
    }

    @ApiOperation(value = "?????? ??????") // SWAGGER
    @PutMapping("/users/{userId}")
    public ResponseEntity<Object> update(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                         @Min(1) @PathVariable("userId") Integer userId,
                                         @Valid @RequestBody UserUpdateRequestDto requestDto) {
        // update
        userService.update(signedUser,userId,requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ?????? ??????????????????.")
                .build());
    }

    // TODO : ?????? ????????? ???????????? ??????, Email ??? ??????????????????
    @ApiOperation(value = "?????? ??????") // SWAGGER
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
                .message("?????? ?????? ??????????????????.")
                .build());
    }

    @ApiOperation(value = "?????? ????????? ??????") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return Image File"),
    }) // SWAGGER
    @GetMapping("/users/{userId}/image")
    public ResponseEntity<Object> getImage(
            @Min(1) @PathVariable("userId") Integer userId) {
        String fileName= userService.getImage(userId);
        if(fileName==null) throw new CustomException(ErrorCode.USER_IMAGE_NOT_FOUND);
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
                .message("?????? ????????? ?????? ?????????????????????.")
                .resource(resource).build());
    }

    @ApiOperation(value = "?????? ????????? ??????") // SWAGGER
    @PutMapping("/users/{userId}/image")
    public ResponseEntity<Object> updateImage(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                              @Min(1) @PathVariable("userId") Integer userId,
                                              @ApiParam(value = "?????????",allowMultiple = true,required = true)
                                              @NotNull @RequestPart MultipartFile file) {
        userService.updateImage(signedUser,userId,file);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ????????? ?????? ??????????????????.")
                .build());
    }

    @ApiOperation(value = "?????? ????????? ??????") // SWAGGER
    @Transactional
    @DeleteMapping("/users/{userId}/image")
    public ResponseEntity<Object> deleteImage(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                              @Min(1) @PathVariable("userId") Integer userId) {
        // check
        userService.deleteImage(signedUser,userId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ????????? ?????? ??????????????????.")
                .build());
    }

    @ApiOperation(value = "?????? ????????? ??????") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201,message = "CREATED"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @PostMapping("/users/{userId}/likes")
    public ResponseEntity<Object> saveLike(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                           @Min(1) @PathVariable("userId") Integer userId,
                                           @ApiParam(value = "?????? ??? ?????????",example = "1",required = true)
                                           @Min(1) @RequestParam Integer dealPostId) {
        userService.saveLike(signedUser,userId,dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("?????? ????????? ?????? ?????????????????????.")
                .build());
    }

    @ApiOperation(value = "?????? ???????????? ????????? ????????? ??????") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : List DealPostGetResponseDto",response = DealPostGetResponseDto.class),
    }) // SWAGGER
    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<Object> getLikes(
            @Min(1) @PathVariable("userId") Integer userId) {
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ???????????? ????????? ?????? ??????????????????.")
                .data(userService.getLikes(userId)).build());
    }

    @ApiOperation(value = "?????? ????????? ??????") // SWAGGER
    @DeleteMapping("/users/{userId}/likes")
    public ResponseEntity<Object> deleteLike(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                             @Min(1) @PathVariable("userId") Integer userId,
                                             @ApiParam(value = "?????? ??? ?????????",example = "1",required = true)
                                             @Min(1) @RequestParam Integer dealPostId) {
        userService.deleteLike(signedUser,userId,dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ????????? ?????? ??????????????????.")
                .build());
    }

    @ApiOperation(value = "?????? ?????? ??? ????????? ??????")
    @GetMapping("/users/{userId}/deal-posts")
    public ResponseEntity<Object> getDealPosts(
            @Min(1) @PathVariable("userId") Integer userId){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ????????? ????????? ?????? ??????????????????.")
                .data(userService.getDealPosts(userId))
                .build());
    }

    // manner review
    @ApiOperation(value = "?????? ?????? ?????? ?????? ??????")
    @PostMapping("/users/{userId}/buy-manner-reviews")
    public ResponseEntity<Object> saveBuyMannerReview(@AuthenticationPrincipal SignedUser signedUser,
                                                       @Min(1) @PathVariable Integer userId,
                                                       @RequestBody MannerReviewSaveRequestDto requestDto){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("?????? ?????? ?????? ??????????????????.")
                .data(userService.saveBuyMannerReview(signedUser,userId,requestDto))
                .build());
    }
    
    @ApiOperation(value = "?????? ?????? ?????? ?????? ??????")
    @GetMapping("/users/{userId}/sell-manner-reviews")
    public ResponseEntity<Object> getSellMannerReviews(@Min(1) @PathVariable Integer userId){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ?????? ?????? ?????? ????????? ?????? ??????????????????.")
                .data(userService.getSellMannerReviews(userId))
                .build());
    }

    @ApiOperation(value = "?????? ?????? ?????? ?????? ??????")
    @GetMapping("/users/{userId}/buy-manner-reviews")
    public ResponseEntity<Object> getBuyMannerReviews(@Min(1) @PathVariable Integer userId){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ?????? ?????? ?????? ????????? ?????? ??????????????????.")
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
                .message("?????? ?????? ?????? ?????? ?????? ??????????????????.")
                .build());
    }

    @ApiOperation(value = "?????? ??????")
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : slice",response = Slice.class),
    })
    @GetMapping("/users/page")
    public ResponseEntity<Object> searchUser(UserSearchRequestDto requestDto){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ?????? ??????????????????.")
                .data(userService.findByFilter(requestDto))
                .build());
    }
    // auth
    @ApiOperation(value = "?????? ?????? ?????? ??????")
    @PostMapping("/users/{userId}/code")
    public ResponseEntity<Object> sendCode(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser
            ,@Min(1) @PathVariable Integer userId){
        userService.setAuthCode(signedUser,userId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("?????? ?????? ?????? ?????? ??????????????????.")
                .build());
    }

    @ApiOperation(value = "?????? ??????")
    @PostMapping("/users/{userId}/auth")
    public ResponseEntity<Object> authUser(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                           @Min(1) @PathVariable Integer userId,
                                           @RequestBody ObjectNode json){
        userService.authUser(signedUser,userId,json.get("code")!=null?json.get("code").asText():null);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("?????? ?????? ??????????????????.")
                .build());
    }
    // keyword
    @ApiOperation(value = "?????? ????????? ??????")
    @GetMapping("/users/{userId}/keywords")
    public ResponseEntity<Object> getUserKeyWords(@AuthenticationPrincipal SignedUser signedUser,
                                                  @PathVariable("userId") Integer userId){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .data(userService.getKeyWords(signedUser,userId))
                .message("?????? ????????? ?????? ??????????????????.")
                .build());
    }
    @ApiOperation(value = "?????? ????????? ??????")
    @DeleteMapping("/users/{userId}/keywords")
    public ResponseEntity<Object> deleteUserKeyWordByWord(@AuthenticationPrincipal SignedUser signedUser,
                                                          @PathVariable("userId") Integer userId,
                                                          @NotNull @RequestParam String word){
        userService.deleteKeyWordByWord(signedUser,userId,word);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("?????? ????????? ?????? ??????????????????.").build());
    }
}
