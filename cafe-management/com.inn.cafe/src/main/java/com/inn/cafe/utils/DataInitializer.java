package com.inn.cafe.utils;

import com.inn.cafe.POJO.Category;
import com.inn.cafe.POJO.Product;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.dao.ProductDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@ConditionalOnProperty(value = "startup.entities.insert", havingValue = "true")
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ProductDao productDao;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization from restaurant_menu.csv");
        loadMenuData();
        log.info("Data initialization completed");
    }

    private void loadMenuData() {
        try {
            ClassPathResource resource = new ClassPathResource("restaurant_menu.csv");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            // Map to store category name -> Category object
            Map<String, Category> categoryMap = new HashMap<>();

            // Counter for category display order
            int categoryDisplayOrder = 1;

            // Skip header line
            String line = reader.readLine();

            int categoriesAdded = 0;
            int productsAdded = 0;
            int productsSkipped = 0;

            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = parseCsvLine(line);

                if (data.length < 5) {
                    log.warn("Skipping invalid line (expected 5 columns): {}", line);
                    continue;
                }

                String categoryName = data[0].trim();
                String productName = data[1].trim();
                String priceStr = data[2].trim();
                String description = data[3].trim();
                String isVegStr = data[4].trim();

                // Process category
                Category category;
                if (categoryMap.containsKey(categoryName)) {
                    category = categoryMap.get(categoryName);
                } else {
                    // Check if category exists in database
                    Optional<Category> existingCategory = categoryDao.findByName(categoryName);
                    if (existingCategory.isPresent()) {
                        category = existingCategory.get();
                        log.info("Category '{}' already exists with ID: {}", categoryName, category.getId());
                    } else {
                        // Create new category with display order
                        category = new Category();
                        category.setName(categoryName);
                        category.setDisplayOrder(categoryDisplayOrder++);
                        category = categoryDao.save(category);
                        categoriesAdded++;
                        log.info("Added new category: {} with ID: {} and display order: {}",
                                categoryName, category.getId(), category.getDisplayOrder());
                    }
                    categoryMap.put(categoryName, category);
                }

                // Check if product exists
                Optional<Product> existingProduct = productDao.findByNameAndCategory(productName, category);
                if (existingProduct.isPresent()) {
                    productsSkipped++;
                    log.debug("Product '{}' already exists, skipping", productName);
                } else {
                    // Create new product
                    Product product = new Product();
                    product.setName(productName);
                    product.setCategory(category);
                    product.setDescription(description);

                    try {
                        product.setPrice(Integer.parseInt(priceStr));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid price for product '{}': {}, setting to 0", productName, priceStr);
                        product.setPrice(0);
                    }

                    // Parse and set isVeg field
                    try {
                        product.setIsVeg(Boolean.parseBoolean(isVegStr));
                    } catch (Exception e) {
                        log.warn("Invalid isVeg value for product '{}': {}, setting to false", productName, isVegStr);
                        product.setIsVeg(false);
                    }

                    product.setStatus("true"); // Set as active by default

                    productDao.save(product);
                    productsAdded++;
                    log.info("Added new product: {} in category: {} with price: {} and isVeg: {}",
                            productName, categoryName, priceStr, product.getIsVeg());
                }
            }

            reader.close();

            log.info("Data initialization summary - Categories added: {}, Products added: {}, Products skipped: {}",
                    categoriesAdded, productsAdded, productsSkipped);

        } catch (Exception e) {
            log.error("Error loading menu data from CSV: {}", e.getMessage(), e);
        }
    }

    /**
     * Parse CSV line handling quoted fields that may contain commas
     */
    private String[] parseCsvLine(String line) {
        // Simple CSV parser that handles quoted fields
        String[] result = new String[5];
        int fieldIndex = 0;
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length() && fieldIndex < 5; i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result[fieldIndex++] = currentField.toString().trim();
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        // Add the last field
        if (fieldIndex < 5) {
            result[fieldIndex] = currentField.toString().trim();
        }

        return result;
    }
}
