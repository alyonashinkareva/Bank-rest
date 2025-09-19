package com.example.bankcards.repository;

import com.example.bankcards.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionLog, UUID> {
}
