package com.around.wmmarket.controller.dto.DealPostImage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class DealPostImageSaveRequestDto {
    private Integer dealPostId;
    private List<MultipartFile> files;
}
