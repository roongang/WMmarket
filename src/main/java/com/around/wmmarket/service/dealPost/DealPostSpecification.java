package com.around.wmmarket.service.dealPost;

import com.around.wmmarket.domain.deal_post.DealPost;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DealPostSpecification {
    public static Specification<DealPost> searchDealPost(Map<String,Object> filter){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates=new ArrayList<>();

            // ex) price,500
            filter.forEach((key,value)->{
                String likeValue="%"+value+"%";
                switch (key){
                    case "userId":
                        predicates.add(criteriaBuilder.like(root.get(key).as(String.class),likeValue));
                        break;
                    case "price":
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(key),likeValue));
                }
            });
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        };
    }
}
