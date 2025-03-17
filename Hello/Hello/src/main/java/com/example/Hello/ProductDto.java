package com.example.Hello;

public class ProductDto {
    private Long id;

    private String name;
    private double price;

    private CategoryDto category;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategoryDto(CategoryDto category) {
        this.category = category;
    }
}
