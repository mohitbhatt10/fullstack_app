package com.inn.cafe.rest;

import com.inn.cafe.wrapper.MenuResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * REST API interface for Menu operations
 * Provides endpoints for fetching restaurant menu items
 */
@RequestMapping(path = "/menu")
public interface MenuRest {

    /**
     * Get complete menu grouped by categories
     * 
     * @return ResponseEntity containing menu data with all categories and items
     */
    @GetMapping(path = "/get")
    ResponseEntity<MenuResponseWrapper> getMenu();

    /**
     * Get menu items filtered by category
     * 
     * @param categoryId Category ID to filter by
     * @return ResponseEntity containing menu data for specific category
     */
    @GetMapping(path = "/getByCategory")
    ResponseEntity<MenuResponseWrapper> getMenuByCategory(@RequestParam Integer categoryId);

    /**
     * Get paginated menu
     * 
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @return ResponseEntity containing paginated menu data
     */
    @GetMapping(path = "/getPaginated")
    ResponseEntity<MenuResponseWrapper> getMenuPaginated(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size);

    /**
     * Generate menu PDF
     * 
     * @param showVegOnly Filter to show only vegetarian items
     * @return ResponseEntity containing PDF as byte array
     */
    @GetMapping(path = "/generatePdf")
    ResponseEntity<byte[]> generateMenuPdf(@RequestParam(defaultValue = "false") Boolean showVegOnly);
}
