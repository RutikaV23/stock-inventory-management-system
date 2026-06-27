package com.rutika.inventory.repository;

import com.rutika.inventory.entity.StockIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockInRepository extends JpaRepository<StockIn, String> {

    List<StockIn> findByProductIdOrderByCreatedAtDesc(String productId);

    List<StockIn> findByStatus(String status);
}
