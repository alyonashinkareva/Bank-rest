package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.DepositRequest;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    @Operation(
            summary = "Создать карту",
            description = "Создаёт карту для пользователя"
    )
    @ApiResponse(responseCode = "200", description = "Карта успешно создана")
    public ResponseEntity<CardDto> createCard(@RequestBody CreateCardRequest request) {
        return ResponseEntity.ok(cardService.createCard(request));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Список карт пользователя",
            description = "Возвращается список всех карт данного пользователя"
    )
    @ApiResponse(responseCode = "200", description = "Список карт успешно показан")
    public ResponseEntity<List<CardDto>> getCardsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(cardService.getCardsByUser(userId));
    }

    @PutMapping("/{cardId}/block")
    @Operation(
            summary = "Заблокировать карту",
            description = "Блокирует данную карту"
    )
    @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована")
    public ResponseEntity<CardDto> blockCard(@PathVariable UUID cardId) {
        return ResponseEntity.ok(cardService.blockCard(cardId));
    }

    @DeleteMapping("/{cardId}")
    @Operation(
            summary = "Удалить карту",
            description = "Удаляет данную карту"
    )
    @ApiResponse(responseCode = "200", description = "Карта успешно удалена")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cardId}/deposit")
    @Operation(
            summary = "Изменить баланс карты",
            description = "Изменяет баланс карты на данную сумму"
    )
    @ApiResponse(responseCode = "200", description = "Баланс успешно изменен")
    public ResponseEntity<CardDto> deposit(
            @PathVariable UUID cardId,
            @RequestBody DepositRequest request) {
        return ResponseEntity.ok(cardService.deposit(cardId, request.amount()));
    }
}