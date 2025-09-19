package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.dto.TransactionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(
            summary = "Сделать перевод",
            description = "Делает перевод денег с одной карты на другую"
    )
    @ApiResponse(responseCode = "200", description = "Деньги успешно переведены")
    public ResponseEntity<TransactionDto> transfer(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.transfer(request));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Список транзакций пользователя",
            description = "Возвращается список всех транзакций данного пользователя"
    )
    @ApiResponse(responseCode = "200", description = "Список транзакций успешно показан")
    public ResponseEntity<List<TransactionDto>> getUserTransactions(@PathVariable UUID userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUser(userId));
    }
}
