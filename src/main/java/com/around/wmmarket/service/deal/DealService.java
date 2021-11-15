package com.around.wmmarket.service.deal;

import com.around.wmmarket.controller.dto.DealPostListResponseDto;
import com.around.wmmarket.controller.dto.DealPostSaveRequestDto;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealPostRepository;
import com.around.wmmarket.domain.deal_post.DealState;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

//final이 선언된 모든 필드를 인자값으로 하는 생성자를 만듦
@RequiredArgsConstructor
@Service
public class DealService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DealPostRepository dealPostRepository;

    @Transactional
    public Integer save(DealPostSaveRequestDto requestDto){
        logger.info("DealService save");
        // User 정보는 어디서 넘겨받을 것인가?

        DealPost dealPost = DealPost.builder()
                .user(requestDto.getUser())
                .category(requestDto.getCategory())
                .title(requestDto.getTitle())
                .price(requestDto.getPrice())
                .content(requestDto.getContent())
                .dealState(requestDto.getDealState())
                .build();

        return dealPostRepository.save(dealPost).getId();
    }

    @Transactional
    public List<DealPost> getDealPost(){
        return dealPostRepository.findAll();
    }

    //readOnly를 쓰면 조회 속도가 개선
    //@Transactional(readOnly = true)
    //public List<DealPostListResponseDto> findAllDesc(){
    //    return dealPostRepository.findAllDesc().stream()
    //            .map(DealPostListResponseDto::new)
    //            .collect(Collectors.toList());
        //dealPostRepository 결과롤 넘어온 Posts의 Stream을 map을 통해
        //DealPostListResponseDto 로 변환 -> List로 반환하는 메소드
    //}

}
