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
                        user.nickname,
                        user.role,
                        user.city_1,
                        user.town_1,
                        user.city_2,
                        user.town_2,
                        user.isAuth,
                        user.code,
                        user.createdDate,
                        user.modifiedDate))
                .from(user)
                .where(
                        emailEq(filter.get("email"))
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
            String val=oper_tk.nextToken();
            if(op.equals("eq")) return user.email.eq(val);
        }
        return null;
    }
}
