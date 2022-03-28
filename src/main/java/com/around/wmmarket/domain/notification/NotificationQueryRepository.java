package com.around.wmmarket.domain.notification;

import com.around.wmmarket.common.QueryDslUtil;
import com.around.wmmarket.controller.dto.notification.NotificationGetResponseDto;
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

import static com.around.wmmarket.domain.notification.QNotification.notification;
import static com.around.wmmarket.domain.user.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Repository
public class NotificationQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Slice<NotificationGetResponseDto> findByFilter(Map<String,String> filter){
        // make pageable
        Pageable pageable= QueryDslUtil.toPageable(filter.get("page"),filter.get("size"),filter.get("sort"));
        // query
        List<Notification> notificationList=queryFactory
                .selectFrom(notification)
                .leftJoin(notification.receiver,user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .where(
                        userIdEq(filter.get("userId")),
                        typeEq(filter.get("type")),
                        isReadEq(filter.get("isRead")),
                        // content
                        contentCt(filter.get("content")),
                        contentEq(filter.get("content")),
                        // createdDate
                        createdDateEq(filter.get("createdDate")),
                        createdDateLt(filter.get("createdDate")),
                        createdDateLoe(filter.get("createdDate")),
                        createdDateGt(filter.get("createdDate")),
                        createdDateGoe(filter.get("createdDate"))
                )
                .orderBy(QueryDslUtil.getOrderSpecifiers(filter.get("sort"),notification)
                        .toArray(new OrderSpecifier[0]))
                .fetch();
        // content
        List<NotificationGetResponseDto> content=notificationList.stream()
                .map(notificationEntity -> NotificationGetResponseDto.builder().build())
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
        return hasText(userId)?notification.receiver.id.eq(Integer.parseInt(userId)):null;
    }
    private BooleanExpression typeEq(String type){
        return hasText(type)?notification.type.eq(NotificationType.valueOf(type)):null;
    }
    private BooleanExpression isReadEq(String isRead){
        if(!hasText(isRead)) return null;
        isRead=isRead.toLowerCase();
        return notification.isRead.eq(Boolean.valueOf(isRead));
    }
    private BooleanExpression contentEq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return notification.content.eq(op);
            String val=oper_tk.nextToken();
            if(op.equals("eq")) return notification.content.contains(val);
        }
        return null;
    }
    private BooleanExpression contentCt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            if(oper_tk.countTokens()<2) continue;
            String op=oper_tk.nextToken();
            String val=oper_tk.nextToken();
            if(op.equals("ct")) return notification.content.contains(val);
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
                return notification.createdDate.eq(localDateTime);
            }
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            if(op.equals("eq")) return notification.createdDate.eq(val);
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
            return notification.createdDate.lt(val);
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
            return notification.createdDate.loe(val);
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
            return notification.createdDate.gt(val);
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
            return notification.createdDate.goe(val);
        }
        return null;
    }
}
