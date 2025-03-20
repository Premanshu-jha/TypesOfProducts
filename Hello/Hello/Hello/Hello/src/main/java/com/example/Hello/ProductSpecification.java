package com.example.Hello;

import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> nameContains(String keyword){
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%" + keyword + "%");
    }

    public static Specification<Product> priceGreaterThanOrEqualTo(Double minPrice){
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"),minPrice);
    }

    public static Specification<Product> priceLessThanOrEqualTo(Double maxPrice){
        return (root,query,criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"),maxPrice);
    }

}
