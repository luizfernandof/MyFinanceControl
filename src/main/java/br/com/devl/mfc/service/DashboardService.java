package br.com.devl.mfc.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.auth.repository.UserRepository;
import br.com.devl.mfc.dto.CategoryExpenseDTO;
import br.com.devl.mfc.dto.DashboardSummaryDTO;
import br.com.devl.mfc.repository.TransactionRepository;

@Service
public class DashboardService {

	private final TransactionRepository repository;
	private final UserRepository userRepository;

	public DashboardService(TransactionRepository repository, UserRepository userRepository) {
		this.repository = repository;
		this.userRepository = userRepository;
	}

	private User getAuthenticatedUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public DashboardSummaryDTO getSummary(int month, int year) {
		// 1. Pegar o usuário logado (Supondo que você use Spring Security)
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		// 2. Buscamos os valores do repositório PASSANDO O USUÁRIO
		BigDecimal rawIncome = repository.sumIncomeByMonth(user, month, year);
		BigDecimal rawExpense = repository.sumExpenseByMonth(user, month, year);

		BigDecimal income = (rawIncome != null) ? rawIncome : BigDecimal.ZERO;
		BigDecimal expense = (rawExpense != null) ? rawExpense : BigDecimal.ZERO;
		BigDecimal balance = income.subtract(expense);

		// 3. Lista por categoria com filtro de usuário
		List<Object[]> result = repository.sumExpensesByCategory(user, month, year);
		List<CategoryExpenseDTO> byCategory = (result == null) ? List.of()
				: result.stream().map(obj -> new CategoryExpenseDTO((String) obj[0],
						(BigDecimal) (obj[1] != null ? obj[1] : BigDecimal.ZERO))).toList();
		return new DashboardSummaryDTO(income, expense, balance, byCategory);

	}

	public List<CategoryExpenseDTO> getExpensesByCategory(int month, int year) {
		User user = getAuthenticatedUser();
		List<Object[]> results = repository.sumExpensesByCategory(user, month, year);
		return results.stream().map(result -> new CategoryExpenseDTO((String) result[0], (BigDecimal) result[1]))
				.toList();
	}
}
