package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.TransactionLog;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public CardDto createCard(CreateCardRequest request) {
        Optional<User> user = userRepository.findById(request.ownerId());
        if (user.isEmpty()) throw new RuntimeException("User not found.");

        String number = request.number();

        // TO DO

        String encrypted = request.number();

        Card card = Card.builder()
                .owner(user.get())
                .encryptedNumber(encrypted)
                .last4(number.substring(number.length() - 4))
                .expiry(request.expiry())
                .status(Card.Status.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();
        cardRepository.save(card);
        return mapToDto(card);
    }
    public List<CardDto> getCardsByUser(UUID ownerId) {
        return cardRepository.findByOwnerId(ownerId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public CardDto blockCard(UUID cardId) {
        Optional<Card> card = cardRepository.findById(cardId);
        if (card.isEmpty()) throw new RuntimeException("User not found.");
        card.get().setStatus(Card.Status.BLOCKED);
        return mapToDto(cardRepository.save(card.get()));
    }

    public void deleteCard(UUID cardId) {
        cardRepository.deleteById(cardId);
    }

    public CardDto deposit(UUID cardId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (card.getStatus() != Card.Status.ACTIVE) {
            throw new RuntimeException("Card is not active");
        }

        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);

        TransactionLog log = TransactionLog.builder()
                .fromCard(null)
                .toCard(card)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .status(TransactionLog.TransactionStatus.SUCCESS)
                .build();
        transactionRepository.save(log);

        return mapToDto(card);
    }

    private CardDto mapToDto(Card card) {
        return new CardDto(
                card.getId(),
                card.getOwner().getId(),
                "**** **** **** " + card.getLast4(),
                card.getExpiry(),
                card.getStatus().toString(),
                card.getBalance()
        );
    }
}