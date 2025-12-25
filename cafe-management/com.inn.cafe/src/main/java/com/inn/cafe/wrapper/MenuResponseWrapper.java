package com.inn.cafe.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for complete menu response
 * Includes all categories with their items and metadata
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponseWrapper {

    private List<MenuCategoryWrapper> categories;
    private Integer totalCategories;
    private Integer totalItems;
    private PaginationInfo pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private Integer page;
        private Integer size;
        private Integer totalPages;
        private Long totalElements;
    }
}
