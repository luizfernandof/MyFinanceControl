package br.com.devl.mfc.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByUser(User user);

	Optional<Transaction> findByIdAndUser(Long id, User user);

	@Query("""
			SELECT t from Transaction t
			WHERE t.user = :user
			AND t.date BETWEEN :start AND :end
			""")
	List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

	@Query("""
			SELECT SUM(t.amount)
			FROM Transaction t
			WHERE t.type = 'INCOME'
			AND MONTH(t.date) = :month
			AND YEAR(t.date) = :year
			""")
	BigDecimal sumIncomeByMonth(int month, int year);

	@Query("""
			SELECT SUM(t.amount)
			FROM Transaction t
			WHERE t.type = 'EXPENSE'
			AND MONTH(t.date) = :month
			AND YEAR(t.date) = :year
			""")
	BigDecimal sumExpenseByMonth(int month, int year);

	@Query("""
			    SELECT t.category.name, SUM(t.amount)
			    FROM Transaction t
			    WHERE t.type = 'EXPENSE'
			    AND MONTH(t.date) = :month
			    AND YEAR(t.date) = :year
			    GROUP BY t.category.name
			""")
	List<Object[]> sumExpensesByCategory(int month, int year);

}
