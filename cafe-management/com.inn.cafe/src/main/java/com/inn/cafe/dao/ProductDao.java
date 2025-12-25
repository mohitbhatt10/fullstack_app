package com.inn.cafe.dao;

import com.inn.cafe.POJO.Category;
import com.inn.cafe.POJO.Product;
import com.inn.cafe.wrapper.ProductWrapper;
import com.inn.cafe.wrapper.MenuItemWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductDao extends JpaRepository<Product, Integer> {

    List<ProductWrapper> getAllProduct();

    @Modifying
    @Transactional
    Integer updateProductStatus(@Param("status") String status, @Param("id") Integer id);

    List<ProductWrapper> getProductByCategory(@Param("id") Integer id);

    ProductWrapper getProductById(@Param("id") Integer id);

    Optional<Product> findByNameAndCategory(String name, Category category);

    // Menu-specific queries
    List<MenuItemWrapper> getMenuItems();

    List<MenuItemWrapper> getMenuItemsByCategory(@Param("categoryId") Integer categoryId);
}
