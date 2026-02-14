package br.com.devl.mfc.dto;

import java.math.BigDecimal;

public record CategoryExpenseDTO(
		String category,
		BigDecimal total
) {}