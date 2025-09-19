package com.example.bankcards.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CreateCardRequest(
        UUID ownerId,
        String number,
        LocalDate expiry
) {}