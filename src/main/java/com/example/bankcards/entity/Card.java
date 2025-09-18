package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String encryptedNumber;

    @Column(length = 4)
    private String last4;

    private LocalDate expiry;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal balance = BigDecimal.ZERO;

    @Version
    private Long version;

    public enum Status {
        ACTIVE, BLOCKED, EXPIRED
    }
}
