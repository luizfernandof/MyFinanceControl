package br.com.devl.mfc.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.devl.mfc.dto.CategoryExpenseDTO;
import br.com.devl.mfc.dto.DashboardSummaryDTO;
import br.com.devl.mfc.repository.TransactionRepository;

@Service
public class DashboardService {
	
	private final TransactionRepository repository;
	
	public DashboardService(TransactionRepository repository) {
		this.repository = repository;
	}
		
	
//	public DashboardSummaryDTO getSummary(int month, int year) {
//	    BigDecimal income = repository.sumIncomeByMonth(month, year);
//	    BigDecimal expense = repository.sumExpenseByMonth(month, year);
//
//	    BigDecimal balance = income.subtract(expense);
//
//	    List<CategoryExpenseDTO> byCategory = repository
//	        .sumExpensesByCategory(month, year)
//	        .stream()
//	        .map(obj -> new CategoryExpenseDTO(
//	                (String) obj[0],
//	                (BigDecimal) obj[1]
//	        ))
//	        .toList();
//
//	    return new DashboardSummaryDTO(income, expense, balance, byCategory);
//	}

	public DashboardSummaryDTO getSummary(int month, int year) {
	    // 1. Buscamos os valores do repositório
	    BigDecimal rawIncome = repository.sumIncomeByMonth(month, year);
	    BigDecimal rawExpense = repository.sumExpenseByMonth(month, year);

	    // 2. TRATAMENTO DE NULOS: Se for null, vira BigDecimal.ZERO
	    BigDecimal income = (rawIncome != null) ? rawIncome : BigDecimal.ZERO;
	    BigDecimal expense = (rawExpense != null) ? rawExpense : BigDecimal.ZERO;

	    // 3. Agora o cálculo é seguro!
	    BigDecimal balance = income.subtract(expense);

	    // 4. Tratamento para a lista por categoria
	    List<Object[]> result = repository.sumExpensesByCategory(month, year);
	    List<CategoryExpenseDTO> byCategory = (result == null) ? List.of() : result.stream()
	        .map(obj -> new CategoryExpenseDTO(
	                (String) obj[0],
	                (BigDecimal) (obj[1] != null ? obj[1] : BigDecimal.ZERO)
	        ))
	        .toList();

	    return new DashboardSummaryDTO(income, expense, balance, byCategory);
	}
}
