package com.retailer.rewards.program.controller;

import com.retailer.rewards.program.model.TransactionDto;
import com.retailer.rewards.program.model.Transaction;
import com.retailer.rewards.program.repository.RewardsRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/rewards")
@CrossOrigin(origins = "http://localhost:8080")
public class RewardsController {

    private RewardsRepository rewardsRepository;

    public RewardsController(RewardsRepository rewardsRepository) {
        this.rewardsRepository = rewardsRepository;
    }

    @PostMapping(value = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transaction> addTransaction(@RequestBody TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setPrice(transactionDto.getPrice());
        transaction.setRewards(calculateRewards(transactionDto.getPrice()));
        transaction.setCreatedDate(Instant.now());
        return ResponseEntity.ok(rewardsRepository.save(transaction));
    }

    private Integer calculateRewards(Integer price){
        return (price >= 50 && price < 100) ? price-50 : (price>100) ? (2 * (price - 100) + 50) : 0;
    }

    @GetMapping(value = "/transaction")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(rewardsRepository.findAll());
    }

    @GetMapping(value = "/total-rewards-earned")
    public ResponseEntity<Integer> getRewardsEarned() {
        List<Transaction> transactionList = rewardsRepository.findAll();
        return transactionList.size()>0 ? ResponseEntity.ok(transactionList.stream().mapToInt(Transaction::getRewards).sum()) : ResponseEntity.ok(0);
    }

    @GetMapping(value = "/total-rewards-earned-per-month")
    public ResponseEntity<Integer> getRewardsEarnedPerMonth() {
        Instant fromDate = Instant.now().minus(30, ChronoUnit.DAYS);
        Instant toDate = Instant.now();
        List<Transaction> transactionList = rewardsRepository.findByCreatedDateAfterAndCreatedDateBefore(fromDate, toDate);
        return transactionList.size()>0 ? ResponseEntity.ok(transactionList.stream().mapToInt(Transaction::getRewards).sum()) : ResponseEntity.ok(0);
    }

}
