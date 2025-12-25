package com.inn.cafe.serviceImpl;

import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.service.MenuService;
import com.inn.cafe.utils.MenuPdfGenerator;
import com.inn.cafe.wrapper.MenuCategoryWrapper;
import com.inn.cafe.wrapper.MenuItemWrapper;
import com.inn.cafe.wrapper.MenuResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for Menu operations
 * Handles business logic for fetching and organizing menu data
 */
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private ProductDao productDao;

    /**
     * Get complete menu grouped by categories
     * All active products are fetched and organized by their categories
     */
    @Override
    public ResponseEntity<MenuResponseWrapper> getMenu() {
        try {
            log.info("Fetching complete menu");

            // Fetch all active menu items
            List<MenuItemWrapper> allItems = productDao.getMenuItems();

            if (allItems == null || allItems.isEmpty()) {
                log.warn("No menu items found");
                return new ResponseEntity<>(new MenuResponseWrapper(), HttpStatus.OK);
            }

            // Group items by category
            Map<Integer, MenuCategoryWrapper> categoryMap = new LinkedHashMap<>();

            for (MenuItemWrapper item : allItems) {
                Integer categoryId = item.getCategoryId();

                if (!categoryMap.containsKey(categoryId)) {
                    MenuCategoryWrapper categoryWrapper = new MenuCategoryWrapper();
                    categoryWrapper.setCategoryId(categoryId);
                    categoryWrapper.setCategoryName(item.getCategoryName());
                    categoryWrapper.setItems(new ArrayList<>());
                    categoryMap.put(categoryId, categoryWrapper);
                }

                categoryMap.get(categoryId).getItems().add(item);
            }

            // Build response
            List<MenuCategoryWrapper> categories = new ArrayList<>(categoryMap.values());

            MenuResponseWrapper response = new MenuResponseWrapper();
            response.setCategories(categories);
            response.setTotalCategories(categories.size());
            response.setTotalItems(allItems.size());
            response.setPagination(null); // No pagination for full menu

            log.info("Menu fetched successfully: {} categories, {} items",
                    categories.size(), allItems.size());

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error fetching menu", ex);
            return new ResponseEntity<>(new MenuResponseWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get menu items for a specific category
     */
    @Override
    public ResponseEntity<MenuResponseWrapper> getMenuByCategory(Integer categoryId) {
        try {
            log.info("Fetching menu for category: {}", categoryId);

            if (categoryId == null) {
                log.warn("Category ID is null");
                return new ResponseEntity<>(new MenuResponseWrapper(), HttpStatus.BAD_REQUEST);
            }

            // Fetch items for the category
            List<MenuItemWrapper> items = productDao.getMenuItemsByCategory(categoryId);

            if (items == null || items.isEmpty()) {
                log.warn("No items found for category: {}", categoryId);
                return new ResponseEntity<>(new MenuResponseWrapper(), HttpStatus.OK);
            }

            // Create category wrapper
            MenuCategoryWrapper categoryWrapper = new MenuCategoryWrapper();
            categoryWrapper.setCategoryId(categoryId);
            categoryWrapper.setCategoryName(items.get(0).getCategoryName());
            categoryWrapper.setItems(items);

            // Build response
            List<MenuCategoryWrapper> categories = new ArrayList<>();
            categories.add(categoryWrapper);

            MenuResponseWrapper response = new MenuResponseWrapper();
            response.setCategories(categories);
            response.setTotalCategories(1);
            response.setTotalItems(items.size());
            response.setPagination(null);

            log.info("Menu fetched for category {}: {} items", categoryId, items.size());

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error fetching menu for category: {}", categoryId, ex);
            return new ResponseEntity<>(new MenuResponseWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get paginated menu
     * Pagination is applied to items across all categories
     */
    @Override
    public ResponseEntity<MenuResponseWrapper> getMenuPaginated(Integer page, Integer size) {
        try {
            log.info("Fetching paginated menu: page={}, size={}", page, size);

            // Fetch all items
            List<MenuItemWrapper> allItems = productDao.getMenuItems();

            if (allItems == null || allItems.isEmpty()) {
                log.warn("No menu items found");
                return new ResponseEntity<>(new MenuResponseWrapper(), HttpStatus.OK);
            }

            // Calculate pagination
            int totalItems = allItems.size();
            int totalPages = (int) Math.ceil((double) totalItems / size);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalItems);

            // Validate page number
            if (startIndex >= totalItems) {
                log.warn("Page {} exceeds total pages {}", page, totalPages);
                return new ResponseEntity<>(new MenuResponseWrapper(), HttpStatus.BAD_REQUEST);
            }

            // Get items for current page
            List<MenuItemWrapper> pageItems = allItems.subList(startIndex, endIndex);

            // Group items by category
            Map<Integer, MenuCategoryWrapper> categoryMap = new LinkedHashMap<>();

            for (MenuItemWrapper item : pageItems) {
                Integer categoryId = item.getCategoryId();

                if (!categoryMap.containsKey(categoryId)) {
                    MenuCategoryWrapper categoryWrapper = new MenuCategoryWrapper();
                    categoryWrapper.setCategoryId(categoryId);
                    categoryWrapper.setCategoryName(item.getCategoryName());
                    categoryWrapper.setItems(new ArrayList<>());
                    categoryMap.put(categoryId, categoryWrapper);
                }

                categoryMap.get(categoryId).getItems().add(item);
            }

            // Build response
            List<MenuCategoryWrapper> categories = new ArrayList<>(categoryMap.values());

            MenuResponseWrapper.PaginationInfo paginationInfo = new MenuResponseWrapper.PaginationInfo(page, size,
                    totalPages, (long) totalItems);

            MenuResponseWrapper response = new MenuResponseWrapper();
            response.setCategories(categories);
            response.setTotalCategories(categories.size());
            response.setTotalItems(pageItems.size());
            response.setPagination(paginationInfo);

            log.info("Paginated menu fetched: page {}/{}, {} items",
                    page + 1, totalPages, pageItems.size());

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error fetching paginated menu", ex);
            return new ResponseEntity<>(new MenuResponseWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Generate menu PDF
     */
    @Override
    public ResponseEntity<byte[]> generateMenuPdf(Boolean showVegOnly) {
        try {
            log.info("Generating menu PDF, showVegOnly: {}", showVegOnly);

            // Fetch all active menu items
            List<MenuItemWrapper> allItems = productDao.getMenuItems();

            if (allItems == null || allItems.isEmpty()) {
                log.warn("No menu items found for PDF generation");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // Group items by category
            Map<Integer, MenuCategoryWrapper> categoryMap = new LinkedHashMap<>();

            for (MenuItemWrapper item : allItems) {
                Integer categoryId = item.getCategoryId();

                if (!categoryMap.containsKey(categoryId)) {
                    MenuCategoryWrapper category = new MenuCategoryWrapper();
                    category.setCategoryId(categoryId);
                    category.setCategoryName(item.getCategoryName());
                    category.setItems(new ArrayList<>());
                    categoryMap.put(categoryId, category);
                }

                categoryMap.get(categoryId).getItems().add(item);
            }

            List<MenuCategoryWrapper> categories = new ArrayList<>(categoryMap.values());

            // Generate PDF
            byte[] pdfBytes = MenuPdfGenerator.generateMenuPdf(categories, showVegOnly);

            // Set headers for PDF response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "menu.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            log.info("Menu PDF generated successfully");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error generating menu PDF", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
