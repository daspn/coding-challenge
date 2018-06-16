package com.daspn.controller;

import com.daspn.service.StatisticsService;
import com.daspn.service.dto.Statistics;
import com.daspn.service.dto.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;

@Controller
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/statistics")
    public ResponseEntity<Statistics> getStatistics() {
        Statistics stats = statisticsService.getStatistics();
        if (stats == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().body(statisticsService.getStatistics());
        }
    }

    @PostMapping("/transactions")
    public ResponseEntity saveTransaction(@RequestBody Transaction payload) {

        boolean oldTransaction = isOldTransaction(payload.getTimestamp());

        Long transactionId = statisticsService.addTransaction(payload);

        if (oldTransaction) {
            return ResponseEntity.noContent().build();
        } else {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(transactionId).toUri();
            return ResponseEntity.created(location).build();
        }
    }

    private boolean isOldTransaction(long timestamp) {
        return Instant.ofEpochMilli(timestamp).isBefore(Instant.now().minusSeconds(60));
    }
}
