package com.rutika.inventory.repository;

import com.rutika.inventory.entity.StockIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockInRepository extends JpaRepository<StockIn, String> {

    List<StockIn> findByProductIdOrderByCreatedAtDesc(String productId);

    List<StockIn> findByStatus(String status);

    @Query("SELECT s FROM StockIn s JOIN s.product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(COALESCE(s.referenceNumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(COALESCE(s.notes, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<StockIn> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
