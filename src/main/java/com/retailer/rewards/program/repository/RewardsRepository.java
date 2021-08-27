package com.retailer.rewards.program.repository;

import com.retailer.rewards.program.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface RewardsRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByCreatedDateAfterAndCreatedDateBefore(Instant startDate, Instant endDate);
}
