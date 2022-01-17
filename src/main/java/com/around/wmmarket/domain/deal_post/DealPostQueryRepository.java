package com.around.wmmarket.domain.deal_post;

import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
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

import static com.around.wmmarket.domain.deal_post.QDealPost.dealPost;
import static com.around.wmmarket.domain.user.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Repository
public class DealPostQueryRepository {
    private final JPAQueryFactory queryFactory;

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
        // TODO : dealPostImage 만 따로 쿼리를 날려서 DTO 에 SET 해야할듯 (아니면 Entity로 받아오고 DTO를 생성하던지)
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
