package com.around.wmmarket.domain.user;

import com.around.wmmarket.common.QueryDslUtil;
import com.around.wmmarket.controller.dto.user.UserGetResponseDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
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

import static com.around.wmmarket.domain.user.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Repository
public class UserQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Slice<UserGetResponseDto> findByFilter(Map<String,String> filter){
        Pageable pageable= QueryDslUtil.toPageable(filter.get("page"), filter.get("size"), filter.get("sort"));
        List<UserGetResponseDto> content=queryFactory
                .select(Projections.constructor(UserGetResponseDto.class,
                        user.id,
                        user.email,
                        user.city_1,
                        user.town_1,
                        user.city_2,
                        user.town_2,
                        user.isAuth,
                        user.createdDate,
                        user.modifiedDate))
                .from(user)
                .where(
                        emailEq(filter.get("email")),
                        emailCt(filter.get("email")),
                        nicknameEq(filter.get("nickname")),
                        nicknameCt(filter.get("nickname")),
                        city_1Eq(filter.get("city_1")),
                        city_1Ct(filter.get("city_1")),
                        town_1Eq(filter.get("town_1")),
                        town_1Ct(filter.get("town_1")),
                        city_2Eq(filter.get("city_2")),
                        city_2Ct(filter.get("city_2")),
                        town_2Eq(filter.get("town_2")),
                        town_2Ct(filter.get("town_2")),
                        isAuthEq(filter.get("isAuth")),
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .orderBy(QueryDslUtil.getOrderSpecifiers(filter.get("sort"),user)
                        .toArray(new OrderSpecifier[0]))
                .fetch();
        boolean hasNext=false;
        if(content.size()>pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext=true;
        }

        return new SliceImpl<>(content,pageable,hasNext);
    }
    // BooleanExpression
    private BooleanExpression emailEq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return user.email.eq(op);
            if(!op.equals("eq")) continue;
            String val=oper_tk.nextToken();
            return user.email.eq(val);
        }
        return null;
    }
    private BooleanExpression emailCt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!op.equals("ct")) continue;
            String val=oper_tk.nextToken();
            return user.email.contains(val);
        }
        return null;
    }
    private BooleanExpression nicknameEq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return user.nickname.eq(op);
            if(!op.equals("eq")) continue;
            String val=oper_tk.nextToken();
            return user.nickname.eq(val);
        }
        return null;
    }
    private BooleanExpression nicknameCt(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!op.equals("ct")) continue;
            String val=oper_tk.nextToken();
            return user.nickname.contains(val);
        }
        return null;
    }
    private BooleanExpression city_1Eq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return user.city_1.eq(op);
            if(!op.equals("eq")) continue;
            String val=oper_tk.nextToken();
            return user.city_1.eq(val);
        }
        return null;
    }
    private BooleanExpression city_1Ct(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!op.equals("ct")) continue;
            String val=oper_tk.nextToken();
            return user.city_1.contains(val);
        }
        return null;
    }
    private BooleanExpression town_1Eq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return user.town_1.eq(op);
            if(!op.equals("eq")) continue;
            String val=oper_tk.nextToken();
            return user.town_1.eq(val);
        }
        return null;
    }
    private BooleanExpression town_1Ct(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!op.equals("ct")) continue;
            String val=oper_tk.nextToken();
            return user.town_1.contains(val);
        }
        return null;
    }
    private BooleanExpression city_2Eq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return user.city_2.eq(op);
            if(!op.equals("eq")) continue;
            String val=oper_tk.nextToken();
            return user.city_2.eq(val);
        }
        return null;
    }
    private BooleanExpression city_2Ct(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!op.equals("ct")) continue;
            String val=oper_tk.nextToken();
            return user.city_2.contains(val);
        }
        return null;
    }
    private BooleanExpression town_2Eq(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) return user.town_2.eq(op);
            if(!op.equals("eq")) continue;
            String val=oper_tk.nextToken();
            return user.town_2.eq(val);
        }
        return null;
    }
    private BooleanExpression town_2Ct(String opers){
        if(!hasText(opers)) return null;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!op.equals("ct")) continue;
            String val=oper_tk.nextToken();
            return user.town_2.contains(val);
        }
        return null;
    }
    private BooleanExpression isAuthEq(String isAuth){
        return hasText(isAuth)?user.isAuth.eq(Integer.parseInt(isAuth)):null;
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
                return user.createdDate.eq(localDateTime);
            }
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            if(op.equals("eq")) return user.createdDate.eq(val);
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
            return user.createdDate.lt(val);
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
            return user.createdDate.loe(val);
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
            return user.createdDate.gt(val);
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
            return user.createdDate.goe(val);
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
                return user.modifiedDate.eq(localDateTime);
            }
            oper_tk.nextToken(); // pass ':'
            while(oper_tk.hasMoreTokens()) time.append(oper_tk.nextToken());
            LocalDateTime val= LocalDateTime.parse(time.toString());
            if(op.equals("eq")) return user.modifiedDate.eq(val);
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
            return user.modifiedDate.lt(val);
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
            return user.modifiedDate.loe(val);
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
            return user.modifiedDate.gt(val);
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
            return user.modifiedDate.goe(val);
        }
        return null;
    }
}
