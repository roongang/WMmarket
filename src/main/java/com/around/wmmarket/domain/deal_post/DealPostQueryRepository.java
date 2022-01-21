package com.around.wmmarket.domain.deal_post;

import com.around.wmmarket.common.QueryDslUtil;
import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    public Slice<DealPostGetResponseDto> findByFilter(Map<String,String> filter){
        // pageable 은 PageRequest 를 통해 생성
        Pageable pageable=QueryDslUtil.toPageable(filter.get("page"), filter.get("size"), filter.get("sort"));
        // query dealPost
        List<DealPost> dealPostList=queryFactory
                .select(dealPost)
                .from(dealPost)
                .leftJoin(dealPost.user,user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .where(
                        userIdEq(filter.get("userId")),
                        categoryEq(filter.get("category")),
                        dealStateEq(filter.get("dealState")),
                        // title
                        titleEq(filter.get("title")),
                        titleCt(filter.get("title")),
                        // content
                        contentEq(filter.get("content")),
                        contentCt(filter.get("content")),
                        // price
                        priceEq(filter.get("price")),
                        priceLt(filter.get("price")),
                        priceLoe(filter.get("price")),
                        priceGt(filter.get("price")),
                        priceGoe(filter.get("price")),
                        // viewCnt
                        viewCntEq(filter.get("viewCnt")),
                        viewCntGt(filter.get("viewCnt")),
                        viewCntGoe(filter.get("viewCnt")),
                        viewCntLt(filter.get("viewCnt")),
                        viewCntLoe(filter.get("viewCnt")),
                        // createdDate
                        createdDateEq(filter.get("createdDate")),
                        createdDateLt(filter.get("createdDate")),
                        createdDateLoe(filter.get("createdDate")),
                        createdDateGt(filter.get("createdDate")),
                        createdDateGoe(filter.get("createdDate")),
                        // modifiedDate
                        modifiedDateEq(filter.get("modifiedDate")),
                        modifiedDateLt(filter.get("modifiedDate")),
                        modifiedDateLoe(filter.get("modifiedDate")),
                        modifiedDateGt(filter.get("modifiedDate")),
                        modifiedDateGoe(filter.get("modifiedDate"))
                )
                .orderBy(QueryDslUtil.getOrderSpecifiers(filter.get("sort"),dealPost)
                        .toArray(new OrderSpecifier[0]))
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
                                .on(dealPostImage.dealPost.id.eq(dealPostEntity.getId()))
                                .fetch())
                        .viewCnt(dealPostEntity.getViewCnt())
                        .build())
                .collect(Collectors.toList());
        boolean hasNext=false;
        if(content.size()>pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext=true;
        }

        return new SliceImpl<>(content,pageable,hasNext);
    }
    // BooleanExpression
    private BooleanExpression userIdEq(String userId){
        return hasText(userId)?dealPost.user.id.eq(Integer.parseInt(userId)):null;
    }
    private BooleanExpression categoryEq(String category) {
        return hasText(category)?dealPost.category.eq(Category.valueOf(category)):null;
    }
    private BooleanExpression dealStateEq(String dealState){
        return hasText(dealState)?dealPost.dealState.eq(DealState.valueOf(dealState)):null;
    }
    private BooleanExpression titleEq(String opers) {
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return dealPost.title.eq(op);
            String val=oper_tk.nextToken();
            if(op.equals("eq")) return dealPost.title.eq(val);
        }
        return null;
    }
    private BooleanExpression titleCt(String opers) {
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            String val=oper_tk.nextToken();
            if(op.equals("ct")) return dealPost.title.contains(val);
        }
        return null;
    }
    private BooleanExpression contentEq(String opers) {
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return dealPost.content.eq(op);
            String val=oper_tk.nextToken();
            if(op.equals("eq")) return dealPost.content.eq(val);
        }
        return null;
    }
    private BooleanExpression contentCt(String opers) {
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            String val=oper_tk.nextToken();
            if(op.equals("ct")) return dealPost.content.contains(val);
        }
        return null;
    }
    private BooleanExpression priceEq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return dealPost.price.eq(Integer.parseInt(op));
            Integer val=Integer.parseInt(oper_tk.nextToken());
            if(op.equals("eq")) return dealPost.price.eq(val);
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
    private BooleanExpression viewCntEq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return dealPost.viewCnt.eq(Integer.parseInt(op));
            Integer val=Integer.parseInt(oper_tk.nextToken());
            if(op.equals("eq")) return dealPost.viewCnt.eq(val);
        }
        return null;
    }
    private BooleanExpression viewCntLt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            Integer val=Integer.parseInt(oper_tk.nextToken());
            if(op.equals("lt")) return dealPost.viewCnt.lt(val);
        }
        return null;
    }
    private BooleanExpression viewCntLoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            Integer val=Integer.parseInt(oper_tk.nextToken());
            if(op.equals("loe")) return dealPost.viewCnt.loe(val);
        }
        return null;
    }
    private BooleanExpression viewCntGt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            Integer val=Integer.parseInt(oper_tk.nextToken());
            if(op.equals("gt")) return dealPost.viewCnt.gt(val);
        }
        return null;
    }
    private BooleanExpression viewCntGoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            Integer val=Integer.parseInt(oper_tk.nextToken());
            if(op.equals("goe")) return dealPost.viewCnt.goe(val);
        }
        return null;
    }
    private BooleanExpression createdDateEq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(), ":",true);
            String op=oper_tk.nextToken();
            if(!hasText(op)) return null;
            StringBuilder time=new StringBuilder();
            if(!op.equals("eq") && !op.equals("gt") && !op.equals("goe") && !op.equals("lt") && !op.equals("loe")){
                time.append(op);
                while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
                LocalDateTime localDateTime=LocalDateTime.parse(time.toString());
                return dealPost.createdDate.eq(localDateTime);
            }
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            if(op.equals("eq")) return dealPost.createdDate.eq(val);
        }
        return null;
    }
    private BooleanExpression createdDateLt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":",true);
            String op=oper_tk.nextToken();
            if(!op.equals("lt")) continue;
            StringBuilder time=new StringBuilder();
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            return dealPost.createdDate.lt(val);
        }
        return null;
    }
    private BooleanExpression createdDateLoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":",true);
            String op=oper_tk.nextToken();
            if(!op.equals("loe")) continue;
            StringBuilder time=new StringBuilder();
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            return dealPost.createdDate.loe(val);
        }
        return null;
    }
    private BooleanExpression createdDateGt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":",true);
            String op=oper_tk.nextToken();
            if(!op.equals("gt")) continue;
            StringBuilder time=new StringBuilder();
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            return dealPost.createdDate.gt(val);
        }
        return null;
    }
    private BooleanExpression createdDateGoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":",true);
            String op=oper_tk.nextToken();
            if(!op.equals("goe")) continue;
            StringBuilder time=new StringBuilder();
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            return dealPost.createdDate.goe(val);
        }
        return null;
    }
    private BooleanExpression modifiedDateEq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(), ":",true);
            String op=oper_tk.nextToken();
            if(!hasText(op)) return null;
            StringBuilder time=new StringBuilder();
            if(!op.equals("eq") && !op.equals("gt") && !op.equals("goe") && !op.equals("lt") && !op.equals("loe")){
                time.append(op);
                while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
                LocalDateTime localDateTime=LocalDateTime.parse(time.toString());
                return dealPost.modifiedDate.eq(localDateTime);
            }
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            if(op.equals("eq")) return dealPost.modifiedDate.eq(val);
        }
        return null;
    }
    private BooleanExpression modifiedDateLt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":",true);
            String op=oper_tk.nextToken();
            if(!op.equals("lt")) continue;
            StringBuilder time=new StringBuilder();
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            return dealPost.modifiedDate.lt(val);
        }
        return null;
    }
    private BooleanExpression modifiedDateLoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":",true);
            String op=oper_tk.nextToken();
            if(!op.equals("loe")) continue;
            StringBuilder time=new StringBuilder();
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            return dealPost.modifiedDate.loe(val);
        }
        return null;
    }
    private BooleanExpression modifiedDateGt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":",true);
            String op=oper_tk.nextToken();
            if(!op.equals("gt")) continue;
            StringBuilder time=new StringBuilder();
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            return dealPost.modifiedDate.gt(val);
        }
        return null;
    }
    private BooleanExpression modifiedDateGoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":",true);
            String op=oper_tk.nextToken();
            if(!op.equals("goe")) continue;
            StringBuilder time=new StringBuilder();
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            return dealPost.modifiedDate.goe(val);
        }
        return null;
    }
}