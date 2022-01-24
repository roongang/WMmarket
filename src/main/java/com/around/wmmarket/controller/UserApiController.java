package com.around.wmmarket.controller;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.ResourceResponse;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.user.*;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.user.CustomUserDetailsService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
import java.util.ArrayList;
import java.util.Collections;

@Slf4j
@Validated
@RequestMapping(Constants.API_PATH)
@RequiredArgsConstructor
@RestController
public class UserApiController {
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    private final AuthenticationManager authenticationManager;
    private final ResourceLoader resourceLoader;
    private final Tika tika=new Tika();

    @ApiOperation(value = "유저 삽입") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201, message = "CREATED"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @Transactional
    @PostMapping("/users")
    public ResponseEntity<Object> save(@Valid @ModelAttribute UserSaveRequestDto requestDto){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("유저 회원가입 성공했습니다.")
                .data(userService.save(requestDto))
                .build());
    }

    // TODO : 분산환경을 위해 쿠키방식 생각
    @ApiOperation(value = "유저 로그인") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201,message = "set session"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @Transactional
    @PostMapping("/signin")
    public ResponseEntity<Object> signin(@Valid @RequestBody UserSignInRequestDto requestDto,
                                         @ApiIgnore HttpSession session){
        // 이미 로그인한 유저면 반환
        if(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)!=null) throw new CustomException(ErrorCode.DUPLICATED_SIGN_IN);

        SignedUser signedUser;
        try { signedUser = customUserDetailsService.getSignedUser(requestDto);}
        catch (UsernameNotFoundException e) {
            session.invalidate();
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        // 인증 토큰 발급
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(signedUser.getUsername(),signedUser.getPassword());
        // 인증 객체
        Authentication authentication;
        try{ authentication = authenticationManager.authenticate(token);}
        catch (Exception e){
            session.invalidate();
            throw new CustomException(ErrorCode.INVALID_USER_PASSWORD);
        }
        // 시큐리티 컨텍스트에 인증 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 세션에 컨텍스트 저장
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,SecurityContextHolder.getContext());
        return ResponseHandler.toResponse(SuccessResponse.builder()
                        .status(HttpStatus.CREATED)
                        .message("유저 로그인 성공했습니다.").build());
    }
    @ApiOperation(value = "유저 로그아웃") // SWAGGER
    @PostMapping("/signout")
    public ResponseEntity<Object> signout(@ApiIgnore HttpSession session){
        if(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)==null){
            throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        }
        session.invalidate();
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 로그아웃 성공했습니다.")
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
                                           @ApiParam(value = "거래 글 아이디",example = "1",required = true)
                                           @Min(1) @RequestParam Integer dealPostId) {
        userService.saveLike(signedUser,userId,dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("유저 좋아요 삽입 성공하였습니다.")
                .build());
    }

    @ApiOperation(value = "유저 좋아요 ID 리스트 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : List dealPostId",response = ArrayList.class),
    }) // SWAGGER
    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<Object> getLikes(
            @Min(1) @PathVariable("userId") Integer userId) {
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 좋아요 ID 리스트 반환 성공했습니다.")
                .data(userService.getLikes(userId)).build());
    }

    @ApiOperation(value = "유저 좋아요 삭제") // SWAGGER
    @DeleteMapping("/users/{userId}/likes")
    public ResponseEntity<Object> deleteLike(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                             @Min(1) @PathVariable("userId") Integer userId,
                                             @ApiParam(value = "거래 글 아이디",example = "1",required = true)
                                             @Min(1) @RequestParam Integer dealPostId) {
        userService.deleteLike(signedUser,userId,dealPostId);
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
                .message("유저 거래글 ID 리스트 반환 성공했습니다.")
                .data(userService.getDealPosts(userId))
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
}
