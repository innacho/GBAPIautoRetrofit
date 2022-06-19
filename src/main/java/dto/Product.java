package dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
    private Integer id;
    private String title;
    private Integer price;
    private String categoryTitle;

    public Product() {
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getPrice() {
        return price;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public Product addTitle(String title){
        this.title = title;
        return this;
    }

    public Product addPrice(Integer price){
        this.price = price;
        return this;
    }

    public Product addCategoryTitle(String categoryTitle){
        this.categoryTitle = categoryTitle;
        return this;
    }
}
