package com.inn.cafe.service;

import com.inn.cafe.wrapper.MenuResponseWrapper;
import org.springframework.http.ResponseEntity;

/**
 * Service interface for Menu business logic
 */
public interface MenuService {

    /**
     * Get complete menu with all categories and items
     * Items are grouped by category and sorted appropriately
     * 
     * @return ResponseEntity with complete menu data
     */
    ResponseEntity<MenuResponseWrapper> getMenu();

    /**
     * Get menu items for a specific category
     * 
     * @param categoryId Category ID to filter by
     * @return ResponseEntity with menu data for the category
     */
    ResponseEntity<MenuResponseWrapper> getMenuByCategory(Integer categoryId);

    /**
     * Get paginated menu items
     * Useful for large menus or mobile views
     * 
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @return ResponseEntity with paginated menu data
     */
    ResponseEntity<MenuResponseWrapper> getMenuPaginated(Integer page, Integer size);

    /**
     * Generate menu PDF
     * 
     * @param showVegOnly Filter to show only vegetarian items
     * @return ResponseEntity containing PDF as byte array
     */
    ResponseEntity<byte[]> generateMenuPdf(Boolean showVegOnly);
}
