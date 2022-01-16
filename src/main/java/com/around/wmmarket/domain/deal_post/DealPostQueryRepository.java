package com.around.wmmarket.domain.deal_post;

import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.around.wmmarket.domain.deal_post_image.DealPostImage;
import com.around.wmmarket.domain.user.User;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.around.wmmarket.domain.deal_post.QDealPost.dealPost;
import static com.around.wmmarket.domain.user.QUser.user;
import static org.springframework.util.StringUtils.hasText;

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
    public Slice<DealPostGetResponseDto> findByFilter(Map<String,Object> filter){
        // pageable 은 PageRequest 를 통해 생성
        Pageable pageable=toPageable((Integer)filter.get("page"),(Integer)filter.get("size"));
        List<DealPostGetResponseDto> content=queryFactory
                .select(Projections.constructor(DealPostGetResponseDto.class,
                        dealPost.id,
                        dealPost.user.id,
                        dealPost.category,
                        dealPost.title,
                        dealPost.price,
                        dealPost.content,
                        dealPost.dealState,
                        dealPost.createdDate,
                        dealPost.modifiedDate))
                .from(dealPost)
                .leftJoin(dealPost.user,user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .where(
                        userIdEq((Integer)filter.get("userId")),
                        categoryEq((String)filter.get("category")),
                        titleEq((String)filter.get("title")),
                        priceEq((Integer)filter.get("price")),
                        dealStateEq((String)filter.get("dealState"))
                )
                .fetch();
        boolean hasNext=false;
        if(content.size()>pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext=true;
        }

        return new SliceImpl<>(content,pageable,hasNext);
    }
    private Pageable toPageable(Integer page,Integer size){
        // TODO : sort 구현
        // page, size 없으면 0,5으로
        page=page==null?0:page;
        size=size==null?10:size;
        return PageRequest.of(page,size);
    }

    private BooleanExpression userIdEq(Integer userId){
        return userId!=null?dealPost.user.id.eq(userId):null;
    }
    private BooleanExpression categoryEq(String category) {
        return hasText(category)?dealPost.category.eq(Category.valueOf(category)):null;
    }
    private BooleanExpression titleEq(String title){
        return hasText(title)?dealPost.title.eq(title):null;
    }
    private BooleanExpression priceEq(Integer price){
        return price!=null?dealPost.price.eq(price):null;
    }
    private BooleanExpression priceLt(Integer price){
        return price!=null?dealPost.price.lt(price):null;
    }
    private BooleanExpression priceLoe(Integer price){
        return price!=null?dealPost.price.loe(price):null;
    }
    private BooleanExpression priceGt(Integer price){
        return price!=null?dealPost.price.gt(price):null;
    }
    private BooleanExpression priceGoe(Integer price){
        return price!=null?dealPost.price.goe(price):null;
    }
    private BooleanExpression dealStateEq(String dealState){
        return hasText(dealState)?dealPost.dealState.eq(DealState.valueOf(dealState)):null;
    }
}
