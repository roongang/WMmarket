package com.around.wmmarket.controller.dto.dealPostImage;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class DealPostImageSaveRequestDto {
<<<<<<< HEAD
    @ApiParam(value = "거래 글 아이디",example = "1",required = true)
    private Integer dealPostId;
    @ApiParam(value = "거래 글 사진들",allowMultiple = true,required = true)
=======
    @ApiParam(value = "거래 글 번호",example = "1",required = true)
    private Integer dealPostId;
    @ApiParam(value = "거래 글 이미지들",allowMultiple = true)
>>>>>>> 20a40a1d9bd70d31fce77a0c6f1003d1e69f417b
    private List<MultipartFile> files;
}
