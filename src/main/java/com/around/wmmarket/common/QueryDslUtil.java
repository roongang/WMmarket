package com.around.wmmarket.common;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static org.springframework.util.StringUtils.hasText;

public class QueryDslUtil {
    public static Pageable toPageable(String page, String size, String order){
        List<Sort.Order> orderList=getOrderList(order);
        Sort sort = (orderList!=null && !orderList.isEmpty())
                ? Sort.by(orderList)
                : Sort.unsorted();
        return PageRequest.of(
                hasText(page)?Integer.parseInt(page):Constants.DEFAULT_PAGE_INDEX,
                hasText(size)?Integer.parseInt(size):Constants.DEFAULT_PAGE_SIZE,
                sort);
    }

    public static OrderSpecifier<?> getSortedColumn(Order order, Path<?> parent,String fieldName){
        Path<Object> fieldPath= Expressions.path(Object.class,parent,fieldName);
        return new OrderSpecifier(order,fieldPath);
    }

    public static <T> List<OrderSpecifier> getOrderSpecifiers(String opers, Path<?> parent){
        // sort=price:desc,createdDate:asc
        List<OrderSpecifier> orderSpecifierList=new ArrayList<>();
        if(!hasText(opers)) return orderSpecifierList;
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String fieldName=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) orderSpecifierList.add(QueryDslUtil.getSortedColumn(Order.ASC,parent,fieldName));
            else{
                String val=oper_tk.nextToken();
                Order order=val.equalsIgnoreCase("desc")
                        ? Order.DESC
                        : Order.ASC;
                orderSpecifierList.add(QueryDslUtil.getSortedColumn(order,parent,fieldName));
            }
        }
        return orderSpecifierList;
    }

    private static List<Sort.Order> getOrderList(String opers){
        if(!hasText(opers)) return null;
        // sort=price:desc,createdDate:asc
        List<Sort.Order> orderList=new ArrayList<>();
        StringTokenizer opers_tk=new StringTokenizer(opers,",");
        while(opers_tk.hasMoreTokens()){
            StringTokenizer oper_tk=new StringTokenizer(opers_tk.nextToken(),":");
            String op=oper_tk.nextToken();
            if(!oper_tk.hasMoreTokens()) orderList.add(new Sort.Order(Sort.Direction.ASC,op));
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
}
