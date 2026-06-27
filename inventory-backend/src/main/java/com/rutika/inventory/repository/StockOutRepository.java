package com.rutika.inventory.repository;

import com.rutika.inventory.entity.StockOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockOutRepository extends JpaRepository<StockOut, String> {

    List<StockOut> findByProductIdOrderByCreatedAtDesc(String productId);

    List<StockOut> findByStatus(String status);
}
