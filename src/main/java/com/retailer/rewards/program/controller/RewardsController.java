package com.retailer.rewards.program.controller;

import com.retailer.rewards.program.model.Transaction;
import com.retailer.rewards.program.model.TransactionDto;
import com.retailer.rewards.program.repository.RewardsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class RewardsController {

    private RewardsRepository rewardsRepository;

    public RewardsController(RewardsRepository rewardsRepository) {
        this.rewardsRepository = rewardsRepository;
    }

    @Operation(summary = "Post a Transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posted a transaction",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Transaction.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content)})
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

    @Operation(summary = "Find all transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction List returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class)) })})
    @GetMapping(value = "/transaction")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(rewardsRepository.findAll());
    }

    @Operation(summary = "Find total rewards earned")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return total rewards earned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class)) })})
    @GetMapping(value = "/total-rewards-earned")
    public ResponseEntity<Integer> getRewardsEarned() {
        List<Transaction> transactionList = rewardsRepository.findAll();
        return transactionList.size()>0 ? ResponseEntity.ok(transactionList.stream().mapToInt(Transaction::getRewards).sum()) : ResponseEntity.ok(0);
    }

    @Operation(summary = "Find total rewards earned per month")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return total rewards earned per month",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class)) })})
    @GetMapping(value = "/total-rewards-earned-per-month")
    public ResponseEntity<Integer> getRewardsEarnedPerMonth() {
        Instant fromDate = Instant.now().minus(30, ChronoUnit.DAYS);
        Instant toDate = Instant.now();
        List<Transaction> transactionList = rewardsRepository.findByCreatedDateAfterAndCreatedDateBefore(fromDate, toDate);
        return transactionList.size()>0 ? ResponseEntity.ok(transactionList.stream().mapToInt(Transaction::getRewards).sum()) : ResponseEntity.ok(0);
    }
}
