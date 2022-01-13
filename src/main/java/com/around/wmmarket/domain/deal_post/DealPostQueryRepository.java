package com.around.wmmarket.domain.deal_post;

import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.around.wmmarket.domain.deal_post.QDealPost.dealPost;

@RequiredArgsConstructor
@Repository
public class DealPostQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Slice<DealPostGetResponseDto> findByDealState(DealState dealState, Pageable pageable) {
        // 기존 repository 에서 findByDealState(DealState dealState,Pageable pageable) 과 비슷
        // 결과값을 DTO 로 받고 싶어서 Custom 한것
        List<DealPost> result=queryFactory
                .selectFrom(dealPost)
                .offset(pageable.getOffset()) // 몇번째 페이지인지
                .limit(pageable.getPageSize()+1) // 페이지당 게시물 수, +1은 hasNext 를 위함
                .fetch();

        List<DealPostGetResponseDto> content=result.stream()
                .map((dealPostEntity -> DealPostGetResponseDto.builder()
                        .id(dealPostEntity.getId())
                        .userId(dealPostEntity.getUser().getId())
                        .category(dealPostEntity.getCategory())
                        .title(dealPostEntity.getTitle())
                        .price(dealPostEntity.getPrice())
                        .content(dealPostEntity.getContent())
                        .dealState(dealPostEntity.getDealState())
                        .createdDate(dealPostEntity.getCreatedDate())
                        .modifiedDate(dealPostEntity.getModifiedDate())
                        .imageIds(dealPostEntity.getDealPostImages().stream()
                                .map(DealPostImage::getId).collect(Collectors.toList()))
                        .build())
                ).collect(Collectors.toList());

        boolean hasNext=false;
        if(content.size()>pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext=true;
        }
        return new SliceImpl<>(content,pageable,hasNext);
    }
}
