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
    @PostMapping("/api/v1/user")
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
    @PostMapping("/api/v1/user/signIn")
    public ResponseEntity<Object> signIn(@RequestBody UserSignInRequestDto requestDto,@ApiIgnore HttpSession session){
        // 이미 로그인한 유저면 반환
        if(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)!=null){
            throw new CustomException(ErrorCode.DUPLICATE_SIGN_IN); }
        SignedUser signedUser;
        try { signedUser = customUserDetailsService.getSignedUser(requestDto);}
        catch (Exception e) {throw new CustomException(ErrorCode.USER_NOT_FOUND);}
        // 인증 토큰 발급
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(signedUser.getUsername(),signedUser.getPassword());
        // 인증 객체
        Authentication authentication;
        try{ authentication = authenticationManager.authenticate(token);}
        catch (Exception e){ throw new CustomException(ErrorCode.INVALID_USER_PASSWORD); }
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
    @GetMapping("/api/v1/user")
    public ResponseEntity<Object> get(
            @ApiParam(value = "유저 이메일",example = "test_email@gmail.com",required = true)
            @RequestParam String email){
        UserGetResponseDto responseDto = userService.getUserResponseDto(email);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 반환 성공했습니다.")
                .data(responseDto).build());
    }

    @ApiOperation(value = "유저 수정") // SWAGGER
    @Transactional
    @PutMapping("/api/v1/user")
    public ResponseEntity<Object> update(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser, @RequestBody UserUpdateRequestDto requestDto) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);

        // update
        userService.update(signedUser.getUsername(),requestDto);
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
    @DeleteMapping("/api/v1/user")
    public ResponseEntity<Object> delete(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,@ApiIgnore HttpSession session){
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        // delete
        userService.delete(signedUser.getUsername());
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
    @GetMapping("/api/v1/user/image")
    public ResponseEntity<Object> getImage(
            @ApiParam(value = "유저 이메일",example = "test_email@gmail.com",required = true)
            @RequestParam String email) {
        if(!userService.isExist(email)) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        String fileName= userService.getImage(email);
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
    @PutMapping("/api/v1/user/image")
    public ResponseEntity<Object> updateImage(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                              @ApiParam(value = "이미지",allowMultiple = true,required = false)
                                              @RequestPart MultipartFile file) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        userService.updateImage(signedUser.getUsername(),file);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 이미지 수정 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "유저 이미지 삭제") // SWAGGER
    @Transactional
    @DeleteMapping("/api/v1/user/image")
    public ResponseEntity<Object> deleteImage(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser) throws Exception{
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        userService.deleteImage(signedUser.getUsername());
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
    @PostMapping("/api/v1/user/like")
    public ResponseEntity<Object> saveLike(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                           @ApiParam(value = "거래 글 아이디",example = "1",required = true)
                                           @RequestParam Integer dealPostId) {
        // check
        if(signedUser==null) throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        userLikeService.save(signedUser.getUsername(),dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("유저 좋아요 삽입 성공하였습니다.")
                .build());
    }

    @ApiOperation(value = "유저 좋아요 리스트 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return body : List dealPostId",response = ArrayList.class),
    }) // SWAGGER
    @GetMapping("/api/v1/user/likes")
    public ResponseEntity<Object> getLikes(
            @ApiParam(value = "유저 아이디",example = "1",required = true)
            @RequestParam Integer userId){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 좋아요 리스트 반환 성공했습니다.")
                .data(userService.getLikesDealPostId(userId)).build());
    }

    @ApiOperation(value = "유저 좋아요 삭제") // SWAGGER
    @DeleteMapping("/api/v1/user/like")
    public ResponseEntity<Object> deleteLike(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                             @ApiParam(value = "거래 글 아이디",example = "1",required = true)
                                             @RequestParam Integer dealPostId) {
        if(signedUser==null) return ResponseEntity.badRequest().body("login 을 먼저 해주세요");
        User user=userService.getUser(signedUser.getUsername());
        userService.deleteLike(user.getId(),dealPostId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("유저 좋아요 삭제 성공했습니다.")
                .build());
    }
}
