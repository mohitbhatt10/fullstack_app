package com.inn.cafe.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Menu Item responses
 * Used to transfer menu item data from backend to frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemWrapper {

    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private String productName;
    private String description;
    private Integer price;
    private Boolean isVeg;
    private Boolean isActive;

    // Constructor for basic menu display (without category info)
    public MenuItemWrapper(Integer id, String productName, String description, Integer price, Boolean isVeg) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.isVeg = isVeg;
        this.isActive = true;
    }

    // Constructor with category information
    public MenuItemWrapper(Integer id, Integer categoryId, String categoryName,
            String productName, String description, Integer price, Boolean isVeg) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.isVeg = isVeg;
        this.isActive = true;
    }
}
