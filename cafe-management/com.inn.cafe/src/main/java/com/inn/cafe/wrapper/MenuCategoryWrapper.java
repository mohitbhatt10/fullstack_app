package com.inn.cafe.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for grouping menu items by category
 * Used for menu display with category sections
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryWrapper {

    private Integer categoryId;
    private String categoryName;
    private Integer displayOrder;
    private List<MenuItemWrapper> items;

    public MenuCategoryWrapper(Integer categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
