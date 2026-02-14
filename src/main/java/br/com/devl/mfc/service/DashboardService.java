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
		
	
	public DashboardSummaryDTO getSummary(int month, int year) {
	    BigDecimal income = repository.sumIncomeByMonth(month, year);
	    BigDecimal expense = repository.sumExpenseByMonth(month, year);

	    BigDecimal balance = income.subtract(expense);

	    List<CategoryExpenseDTO> byCategory = repository
	        .sumExpensesByCategory(month, year)
	        .stream()
	        .map(obj -> new CategoryExpenseDTO(
	                (String) obj[0],
	                (BigDecimal) obj[1]
	        ))
	        .toList();

	    return new DashboardSummaryDTO(income, expense, balance, byCategory);
	}

}
