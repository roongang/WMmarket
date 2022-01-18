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
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import static com.around.wmmarket.domain.deal_post.QDealPost.dealPost;
import static com.around.wmmarket.domain.deal_post_image.QDealPostImage.dealPostImage;
import static com.around.wmmarket.domain.user.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Repository
public class DealPostQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Slice<DealPostGetResponseDto> findByFilter(Map<String,Object> filter){
        // pageable 은 PageRequest 를 통해 생성
        Pageable pageable=toPageable((String)filter.get("page"),(String)filter.get("size"));
        // query dealPost
        List<DealPost> dealPostList=queryFactory
                .select(dealPost)
                .from(dealPost)
                .leftJoin(dealPost.user,user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .where(
                        userIdEq((String)filter.get("userId")),
                        categoryEq((String)filter.get("category")),
                        titleEq((String)filter.get("title")),
                        priceEq((String)filter.get("price")),
                        priceLt((String)filter.get("price")),
                        priceLoe((String)filter.get("price")),
                        priceGt((String)filter.get("price")),
                        priceGoe((String)filter.get("price")),
                        dealStateEq((String)filter.get("dealState"))
                )
                .fetch();
        // content
        List<DealPostGetResponseDto> content=dealPostList.stream()
                .map(dealPostEntity-> DealPostGetResponseDto.builder()
                        .id(dealPostEntity.getId())
                        .userId(dealPostEntity.getUser()!=null?dealPostEntity.getUser().getId():null)
                        .category(dealPostEntity.getCategory())
                        .title(dealPostEntity.getTitle())
                        .price(dealPostEntity.getPrice())
                        .content(dealPostEntity.getContent())
                        .dealState(dealPostEntity.getDealState())
                        .createdDate(dealPostEntity.getCreatedDate())
                        .modifiedDate(dealPostEntity.getModifiedDate())
                        .imageIds(queryFactory
                                .select(dealPostImage.id)
                                .from(dealPostImage)
                                .innerJoin(dealPostImage.dealPost,dealPost)
                                .where(dealPostImage.dealPost.id.eq(dealPostEntity.getId()))
                                .fetch())
                        .build())
                .collect(Collectors.toList());
        boolean hasNext=false;
        if(content.size()>pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext=true;
        }

        return new SliceImpl<>(content,pageable,hasNext);
    }
    private Pageable toPageable(String page,String size){
        // TODO : sort 구현
        // page, size 없으면 0,5으로
        Integer default_page=0;
        Integer default_size=5;
        return PageRequest.of(
                hasText(page)?Integer.parseInt(page):default_page,
                hasText(size)?Integer.parseInt(size):default_size);
    }

    private BooleanExpression userIdEq(String userId){
        return hasText(userId)?dealPost.user.id.eq(Integer.parseInt(userId)):null;
    }
    private BooleanExpression categoryEq(String category) {
        return hasText(category)?dealPost.category.eq(Category.valueOf(category)):null;
    }
    private BooleanExpression titleEq(String title){
        return hasText(title)?dealPost.title.eq(title):null;
    }
    private BooleanExpression priceEq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()==1) return dealPost.price.eq(Integer.parseInt(oper_tk.nextToken()));
        }
        return null;
    }
    private BooleanExpression priceLt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            Integer val=Integer.parseInt(oper_tk.nextToken());
            if(op.equals("lt")) return dealPost.price.lt(val);
        }
        return null;
    }
    private BooleanExpression priceLoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            Integer val=Integer.parseInt(oper_tk.nextToken());
            if(op.equals("loe")) return dealPost.price.loe(val);
        }
        return null;
    }
    private BooleanExpression priceGt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            Integer val=Integer.parseInt(oper_tk.nextToken());
            if(op.equals("gt")) return dealPost.price.gt(val);
        }
        return null;
    }
    private BooleanExpression priceGoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            Integer val=Integer.parseInt(oper_tk.nextToken());
            if(op.equals("goe")) return dealPost.price.goe(val);
        }
        return null;
    }
    private BooleanExpression dealStateEq(String dealState){
        return hasText(dealState)?dealPost.dealState.eq(DealState.valueOf(dealState)):null;
    }
}
