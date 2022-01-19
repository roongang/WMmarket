package com.around.wmmarket.domain.deal_post;

import com.around.wmmarket.common.QueryDslUtil;
import com.around.wmmarket.controller.dto.dealPost.DealPostGetResponseDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final int default_page=0;
    private final int default_size=5;

    public Slice<DealPostGetResponseDto> findByFilter(Map<String,String> filter){
        // pageable 은 PageRequest 를 통해 생성
        Pageable pageable=toPageable(filter.get("page"), filter.get("size"), filter.get("sort"));
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
                .orderBy(getOrderSpecifiers(filter.get("sort"))
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
    private Pageable toPageable(String page,String size,String opers){
        List<Sort.Order> orderList=getOrderList(opers);
        Sort sort = (orderList!=null && !orderList.isEmpty())
                ? Sort.by(orderList)
                : Sort.unsorted();
        return PageRequest.of(
                hasText(page)?Integer.parseInt(page):default_page,
                hasText(size)?Integer.parseInt(size):default_size,
                sort);
    }

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
        StringTokenizer opers_tk=new StringTokenizer(opers,":");
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
        StringTokenizer opers_tk=new StringTokenizer(opers,":");
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
        StringTokenizer opers_tk=new StringTokenizer(opers,":");
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
        StringTokenizer opers_tk=new StringTokenizer(opers,":");
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
    private BooleanExpression createdDateEq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return dealPost.createdDate.eq(LocalDateTime.parse(op));
            LocalDateTime val= LocalDateTime.parse(oper_tk.nextToken());
            if(op.equals("eq")) return dealPost.createdDate.eq(val);
        }
        return null;
    }
    private BooleanExpression createdDateLt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            LocalDateTime val= LocalDateTime.parse(oper_tk.nextToken());
            if(op.equals("lt")) return dealPost.createdDate.lt(val);
        }
        return null;
    }
    private BooleanExpression createdDateLoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            LocalDateTime val= LocalDateTime.parse(oper_tk.nextToken());
            if(op.equals("loe")) return dealPost.createdDate.loe(val);
        }
        return null;
    }
    private BooleanExpression createdDateGt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            LocalDateTime val= LocalDateTime.parse(oper_tk.nextToken());
            if(op.equals("gt")) return dealPost.createdDate.gt(val);
        }
        return null;
    }
    private BooleanExpression createdDateGoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            LocalDateTime val= LocalDateTime.parse(oper_tk.nextToken());
            if(op.equals("goe")) return dealPost.createdDate.goe(val);
        }
        return null;
    }
    private BooleanExpression modifiedDateEq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return dealPost.modifiedDate.eq(LocalDateTime.parse(op));
            LocalDateTime val= LocalDateTime.parse(oper_tk.nextToken());
            if(op.equals("eq")) return dealPost.modifiedDate.eq(val);
        }
        return null;
    }
    private BooleanExpression modifiedDateLt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            LocalDateTime val= LocalDateTime.parse(oper_tk.nextToken());
            if(op.equals("lt")) return dealPost.modifiedDate.lt(val);
        }
        return null;
    }
    private BooleanExpression modifiedDateLoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            LocalDateTime val= LocalDateTime.parse(oper_tk.nextToken());
            if(op.equals("loe")) return dealPost.modifiedDate.loe(val);
        }
        return null;
    }
    private BooleanExpression modifiedDateGt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            LocalDateTime val= LocalDateTime.parse(oper_tk.nextToken());
            if(op.equals("gt")) return dealPost.modifiedDate.gt(val);
        }
        return null;
    }
    private BooleanExpression modifiedDateGoe(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            LocalDateTime val= LocalDateTime.parse(oper_tk.nextToken());
            if(op.equals("goe")) return dealPost.modifiedDate.lt(val);
        }
        return null;
    }
    private List<Sort.Order> getOrderList(String opers){
        if(!hasText(opers)) return null;
        // sort=price:desc,createdDate:asc
        List<Sort.Order> orderList=new ArrayList<>();
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) orderList.add(new Sort.Order(Sort.DEFAULT_DIRECTION,op));
            else{
                String val=oper_tk.nextToken();
                Sort.Direction direction=val.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
                orderList.add(new Sort.Order(direction,op));
            }
        }
        return orderList;
    }
    private List<OrderSpecifier> getOrderSpecifiers(String opers){
        // sort=price:desc,createdDate:asc
        List<OrderSpecifier> orderSpecifierList=new ArrayList<>();
        if(!hasText(opers)) return orderSpecifierList;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String fieldName=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) orderSpecifierList.add(QueryDslUtil.getSortedColumn(Order.ASC,dealPost,fieldName));
            else{
                String val=oper_tk.nextToken();
                Order order=val.equalsIgnoreCase("desc")
                        ? Order.DESC
                        : Order.ASC;
                orderSpecifierList.add(QueryDslUtil.getSortedColumn(order,dealPost,fieldName));
            }
        }
        return orderSpecifierList;
    }
}
