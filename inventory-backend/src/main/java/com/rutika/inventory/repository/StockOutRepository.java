package com.rutika.inventory.repository;

import com.rutika.inventory.entity.StockOut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface StockOutRepository extends JpaRepository<StockOut, String> {

    List<StockOut> findByProductIdOrderByCreatedAtDesc(String productId);

    @Query("SELECT COALESCE(SUM(so.quantity), 0) FROM StockOut so WHERE so.product.id = :productId")
    Integer sumQuantityByProductId(@Param("productId") String productId);

    @Query("SELECT COALESCE(SUM(so.quantity), 0) FROM StockOut so WHERE so.product.id = :productId " +
           "AND (:dateFrom IS NULL OR so.createdAt >= :dateFrom) " +
           "AND (:dateTo IS NULL OR so.createdAt <= :dateTo)")
    Integer sumQuantityByProductIdAndDateBetween(@Param("productId") String productId,
                                                  @Param("dateFrom") Instant dateFrom,
                                                  @Param("dateTo") Instant dateTo);

    List<StockOut> findByStatus(String status);

    @Query("SELECT s FROM StockOut s JOIN s.product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(COALESCE(s.performedBy, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(COALESCE(s.reason, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<StockOut> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
