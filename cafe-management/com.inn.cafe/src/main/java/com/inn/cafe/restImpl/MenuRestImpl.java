package com.inn.cafe.restImpl;

import com.inn.cafe.rest.MenuRest;
import com.inn.cafe.service.MenuService;
import com.inn.cafe.wrapper.MenuResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller implementation for Menu API
 * Handles HTTP requests for menu operations
 */
@RestController
public class MenuRestImpl implements MenuRest {

    @Autowired
    private MenuService menuService;

    /**
     * GET /menu/get
     * Returns complete menu with all categories and items
     */
    @Override
    public ResponseEntity<MenuResponseWrapper> getMenu() {
        return menuService.getMenu();
    }

    /**
     * GET /menu/getByCategory?categoryId={id}
     * Returns menu items for a specific category
     */
    @Override
    public ResponseEntity<MenuResponseWrapper> getMenuByCategory(Integer categoryId) {
        return menuService.getMenuByCategory(categoryId);
    }

    /**
     * GET /menu/getPaginated?page={page}&size={size}
     * Returns paginated menu items
     */
    @Override
    public ResponseEntity<MenuResponseWrapper> getMenuPaginated(Integer page, Integer size) {
        return menuService.getMenuPaginated(page, size);
    }

    /**
     * GET /menu/generatePdf?showVegOnly={true|false}
     * Generates and returns menu as PDF
     */
    @Override
    public ResponseEntity<byte[]> generateMenuPdf(Boolean showVegOnly) {
        return menuService.generateMenuPdf(showVegOnly);
    }
}
