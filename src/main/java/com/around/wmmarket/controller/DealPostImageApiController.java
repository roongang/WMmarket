package com.around.wmmarket.controller;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.ResourceResponse;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.dealPostImage.DealPostImageSaveRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.dealPostImage.DealPostImageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

@Validated
@RequestMapping(Constants.API_PATH)
@RequiredArgsConstructor
@RestController
public class DealPostImageApiController {
    private final DealPostImageService dealPostImageService;
    private final ResourceLoader resourceLoader;
    private final Tika tika=new Tika();

    @ApiOperation(value = "거래 글 이미지 삽입") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 201, message = "CREATED"),
    })
    @ResponseStatus(value = HttpStatus.CREATED) // SWAGGER
    @PostMapping("/deal-post-images")
    public ResponseEntity<?> save(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                  @Valid @ModelAttribute DealPostImageSaveRequestDto requestDto) {
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.CREATED)
                .message("거래글 이미지 삽입 성공했습니다.")
                .data(dealPostImageService.save(signedUser,requestDto.getDealPostId(),requestDto.getFiles()))
                .build());
    }

    @ApiOperation(value = "거래 글 이미지 삭제") // SWAGGER
    @DeleteMapping("/deal-post-images/{dealPostImageId}")
    public ResponseEntity<?> delete(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                    @Min(1) @PathVariable("dealPostImageId") Integer dealPostImageId) {
        // delete
        dealPostImageService.delete(signedUser,dealPostImageId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("거래글 이미지 삭제 성공했습니다.")
                .build());
    }

    @ApiOperation(value = "거래 글 이미지 반환") // SWAGGER
    @ApiResponses({
            @ApiResponse(code = 200,message = "return Image File")
    }) // SWAGGER
    @GetMapping("/deal-post-images/{dealPostImageId}")
    public ResponseEntity<?> get(
            @Min(1) @PathVariable("dealPostImageId") Integer dealPostImageId) {
        if(dealPostImageService.get(dealPostImageId)==null){
            return ResponseHandler.toResponse(SuccessResponse.builder()
                    .status(HttpStatus.OK)
                    .message("거래글 이미지 반환 성공했습니다.(존재하지 않음)")
                    .data(Arrays.asList()).build());
        }
        String fileName=dealPostImageService.get(dealPostImageId).getName();
        Resource resource=resourceLoader.getResource("file:"+ Paths.get(Constants.dealPostImagePath.toString(),fileName));
        File file= null;
        try { file = resource.getFile(); } catch (IOException e) { new CustomException(ErrorCode.FILE_NOT_FOUND); }
        String mediaType;
        try { mediaType = tika.detect(file); } catch (IOException e) { throw new CustomException(ErrorCode.MEDIA_TYPE_NOT_FOUND); }

        HttpHeaders headers=new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+resource.getFilename()+"\"");
        headers.add(HttpHeaders.CONTENT_TYPE,mediaType);
        headers.add(HttpHeaders.CONTENT_LENGTH,String.valueOf(file.length()));

        return ResponseHandler.toResponse(ResourceResponse.builder()
                .status(HttpStatus.OK)
                .headers(headers)
                .message("거래글 이미지 반환 성공했습니다.")
                .resource(resource).build());
    }

}
