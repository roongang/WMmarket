package com.around.wmmarket.controller;

import com.around.wmmarket.common.*;
import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.user.UserGetResponseDto;
import com.around.wmmarket.controller.dto.user.UserSaveRequestDto;
import com.around.wmmarket.controller.dto.user.UserSignInRequestDto;
import com.around.wmmarket.controller.dto.user.UserUpdateRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.service.user.CustomUserDetailsService;
import com.around.wmmarket.service.user.UserService;
import com.around.wmmarket.service.userLike.UserLikeService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

@Slf4j
@RequestMapping(Constants.API_PATH)
@RequiredArgsConstructor
@RestController
public class UserApiController {
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserLikeService userLikeService;

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
    public ResponseEntity<Object> save(@ModelAttribute UserSaveRequestDto requestDto){
        userService.save(requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .message("유저 회원가입 성공했습니다.")
                .status(HttpStatus.CREATED).build());
    }

    // TODO : 분산환경을 위해 쿠키방식 생각
    @ApiOperation(value = "유저 로그인") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201,message = "set session"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @Transactional
    @PostMapping("/signin")
    public ResponseEntity<Object> signIn(@RequestBody UserSignInRequestDto requestDto,
                                         @ApiIgnore HttpSession session){
        // 이미 로그인한 유저면 반환
        if(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)!=null){
            throw new CustomException(ErrorCode.DUPLICATE_SIGN_IN); }
        SignedUser signedUser;
        try { signedUser = customUserDetailsService.getSignedUser(requestDto);}
        catch (Exception e) {
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

    @ApiOperation(value = "유저 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return body : user info",response = UserGetResponseDto.class),
    }) // SWAGGER
    @GetMapping("/users")
    public ResponseEntity<Object> get(
            @ApiParam(value = "유저 이메일",example = "test_email@gmail.com",required = true)
            @RequestParam String email){
        UserGetResponseDto responseDto = userService.getUserResponseDto(email);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 반환 성공했습니다.")
                .data(responseDto).build());
    }

    @ApiOperation(value = "유저 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return body : user info",response = UserGetResponseDto.class),
    }) // SWAGGER
    @GetMapping("/users/{userId}")
    public ResponseEntity<Object> get(
            @PathVariable("userId") Integer userId){
        UserGetResponseDto responseDto = userService.getUserResponseDto(userId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 반환 성공했습니다.")
                .data(responseDto).build());
    }

    @ApiOperation(value = "유저 수정") // SWAGGER
    @PutMapping("/users/{userId}")
    public ResponseEntity<Object> update(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                         @PathVariable("userId") Integer userId,
                                         @RequestBody UserUpdateRequestDto requestDto) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        // compare id, signed user
        if(!userService.getUser(userId).getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);

        // update
        userService.update(userId,requestDto);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 수정 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "유저 삭제") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "remove session"),
    }) // SWAGGER
    @Transactional
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Object> delete(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                         @ApiIgnore HttpSession session,
                                         @PathVariable Integer userId){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        // compare id, signed user
        if(!userService.getUser(userId).getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);

        // delete
        userService.delete(userId);
        session.invalidate();
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
            @PathVariable("userId") Integer userId) {
        if(!userService.isExist(userId)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
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
    @Transactional
    @PutMapping("/users/{userId}/image")
    public ResponseEntity<Object> updateImage(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                              @PathVariable("userId") Integer userId,
                                              @ApiParam(value = "이미지",allowMultiple = true,required = false)
                                              @RequestPart MultipartFile file) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        if(!userService.getUser(userId).getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);

        userService.updateImage(userId,file);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 이미지 수정 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "유저 이미지 삭제") // SWAGGER
    @Transactional
    @DeleteMapping("/users/{userId}/image")
    public ResponseEntity<Object> deleteImage(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                              @PathVariable("userId") Integer userId) throws Exception{
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        if(!userService.getUser(userId).getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);

        userService.deleteImage(userId);
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
                                           @PathVariable("userId") Integer userId,
                                           @ApiParam(value = "거래 글 아이디",example = "1",required = true)
                                           @RequestParam Integer dealPostId) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        if(!userService.getUser(userId).getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);

        userLikeService.save(userId,dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("유저 좋아요 삽입 성공하였습니다.")
                .build());
    }

    @ApiOperation(value = "유저 좋아요들 ID 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return body : List dealPostId",response = ArrayList.class),
    }) // SWAGGER
    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<Object> getLikes(
            @PathVariable("userId") Integer userId) {
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 좋아요들 ID 반환 성공했습니다.")
                .data(userService.getLikesDealPostId(userId)).build());
    }

    @ApiOperation(value = "유저 좋아요 삭제") // SWAGGER
    @DeleteMapping("/users/{userId}/likes")
    public ResponseEntity<Object> deleteLike(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                             @PathVariable("userId") Integer userId,
                                             @ApiParam(value = "거래 글 아이디",example = "1",required = true)
                                             @RequestParam Integer dealPostId) {
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        User user=userService.getUser(userId);
        if(!user.getEmail().equals(signedUser.getUsername())) throw new CustomException(ErrorCode.UNAUTHORIZED_USER_TO_USER);

        userService.deleteLike(userId,dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 좋아요 삭제 성공했습니다.")
                .build());
    }
}
