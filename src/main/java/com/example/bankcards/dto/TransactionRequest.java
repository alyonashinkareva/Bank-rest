package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(UUID fromCardId, UUID toCardId, BigDecimal amount) {}
