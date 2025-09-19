package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CardDto(
        UUID id,
        UUID ownerId,
        String maskedNumber,
        LocalDate expiry,
        String status,
        BigDecimal balance
) {}