package br.com.devl.mfc.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	// Refatorado: Adicionada Query para extrair mês e ano da data na paginação
	@Query("""
			SELECT t FROM Transaction t
			WHERE t.user = :user
			AND MONTH(t.date) = :month
			AND YEAR(t.date) = :year
			""")
	Page<Transaction> findByUserAndMonthAndYear(@Param("user") User user, @Param("month") int month,
			@Param("year") int year, Pageable pageable);

	List<Transaction> findByUser(User user);

	Optional<Transaction> findByIdAndUser(Long id, User user);

	@Query("""
			SELECT t from Transaction t
			WHERE t.user = :user
			AND t.date BETWEEN :start AND :end
			""")
	List<Transaction> findByUserAndDateBetween(@Param("user") User user, @Param("start") LocalDate start,
			@Param("end") LocalDate end);

	@Query("""
			SELECT SUM(t.amount)
			FROM Transaction t
			WHERE t.type = 'INCOME'
			AND t.user = :user
			AND MONTH(t.date) = :month
			AND YEAR(t.date) = :year
			""")
	BigDecimal sumIncomeByMonth(@Param("user") User user, @Param("month") int month, @Param("year") int year);

	@Query("""
			SELECT SUM(t.amount)
			FROM Transaction t
			WHERE t.type = 'EXPENSE'
			AND t.user = :user
			AND MONTH(t.date) = :month
			AND YEAR(t.date) = :year
			""")
	BigDecimal sumExpenseByMonth(@Param("user") User user, @Param("month") int month, @Param("year") int year);

	@Query("""
			SELECT t.category.name, SUM(t.amount)
			FROM Transaction t
			WHERE t.type = 'EXPENSE'
			AND t.user = :user
			AND MONTH(t.date) = :month
			AND YEAR(t.date) = :year
			GROUP BY t.category.name
			""")
	List<Object[]> sumExpensesByCategory(@Param("user") User user, @Param("month") int month, @Param("year") int year);

}