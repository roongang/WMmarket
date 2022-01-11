package com.around.wmmarket.controller.dto.dealPostImage;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class DealPostImageSaveRequestDto {
    @ApiParam(value = "거래 글 아이디",example = "1",required = true)
    @Min(1)
    private Integer dealPostId;
    @ApiParam(value = "거래 글 사진들",allowMultiple = true,required = true)
    @NotEmpty
    private List<MultipartFile> files;
}
