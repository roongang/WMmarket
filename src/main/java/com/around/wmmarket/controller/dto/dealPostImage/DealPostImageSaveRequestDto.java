package com.around.wmmarket.controller.dto.dealPostImage;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class DealPostImageSaveRequestDto {
    @ApiParam(value = "거래 글 아이디",example = "1",required = true)
    private Integer dealPostId;
    @ApiParam(value = "거래 글 사진들",allowMultiple = true,required = true)
    private List<MultipartFile> files;
}
