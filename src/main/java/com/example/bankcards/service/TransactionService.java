package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.TransactionLog;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;

    public TransactionDto transfer(TransactionRequest request) {
        Optional<Card> fromOpt = cardRepository.findById(request.fromCardId());
        Optional<Card> toOpt = cardRepository.findById(request.toCardId());
        if (fromOpt.isEmpty() || toOpt.isEmpty()) throw new RuntimeException(fromOpt.isEmpty() ? "Source card not found" : "Destination card not found");

        Card from = fromOpt.get();
        Card to = toOpt.get();
        if (from.getStatus() != Card.Status.ACTIVE || to.getStatus() != Card.Status.ACTIVE) {
            return logTransaction(from, to, request.amount(), TransactionLog.TransactionStatus.FAILED);
        }

        if (from.getBalance().compareTo(request.amount()) < 0) {
            return logTransaction(from, to, request.amount(), TransactionLog.TransactionStatus.FAILED);
        }

        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));

        cardRepository.save(from);
        cardRepository.save(to);

        return logTransaction(from, to, request.amount(), TransactionLog.TransactionStatus.SUCCESS);
    }

    public List<TransactionDto> getTransactionsByUser(UUID userId) {
        return transactionRepository.findAll().stream()
                .filter(tx -> (tx.getFromCard() != null && tx.getFromCard().getOwner().getId().equals(userId)) ||
                        (tx.getToCard() != null && tx.getToCard().getOwner().getId().equals(userId)))
                .map(this::mapToDto)
                .toList();
    }

    private TransactionDto logTransaction(Card from, Card to, BigDecimal amount, TransactionLog.TransactionStatus status) {
        TransactionLog log = TransactionLog.builder()
                .fromCard(from)
                .toCard(to)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .status(status)
                .build();

        transactionRepository.save(log);
        return mapToDto(log);
    }

    private TransactionDto mapToDto(TransactionLog transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getFromCard() != null ? transaction.getFromCard().getId() : null,
                transaction.getToCard() != null ? transaction.getToCard().getId() : null,
                transaction.getAmount(),
                transaction.getTimestamp(),
                transaction.getStatus().name()
        );
    }
}
