package br.com.devl.mfc.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.devl.mfc.enums.TransactionType;

public record TransactionRequestDTO(
	    String description,
	    BigDecimal amount,
	    LocalDate date,
	    Long categoryId,
	    TransactionType type, // Deve ser o Enum (EXPENSE/INCOME)
	    Integer installments,
	    boolean recurring,
	    Integer occurrences
	) {}