package br.com.devl.mfc.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryDTO(
		BigDecimal totalIncome,
		BigDecimal totalExpense,
		BigDecimal balance,
		List<CategoryExpenseDTO> expenseByCategory
) {}