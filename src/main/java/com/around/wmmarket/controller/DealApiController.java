package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.DealPostSaveRequestDto;
import com.around.wmmarket.service.deal.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class DealApiController {
    private final DealService dealService;

    @GetMapping("/api/v1/hello")
    public String Hello() { return "hello";}

    @PostMapping("/api/v1/dealpost")
    public Integer save(@RequestBody DealPostSaveRequestDto requestDto){
        return dealService.save(requestDto);
    }

}