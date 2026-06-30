package com.rutika.inventory.repository;

import com.rutika.inventory.entity.Product;
import com.rutika.inventory.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatus(
            String name, String description, ProductStatus status, Pageable pageable);

    long countByStatus(ProductStatus status);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= p.minimumStock")
    long countLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity = 0")
    long countOutOfStockProducts();

    @Query("SELECT COALESCE(SUM(p.stockQuantity), 0) FROM Product p")
    long sumStockQuantity();
}
