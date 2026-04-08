package br.com.devl.mfc.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

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

	/**
	 * Deleta todas as transações de um usuário que pertencem ao mesmo grupo
	 * (parcelamento).
	 */
	@Modifying
	@Transactional
	@Query("DELETE FROM Transaction t WHERE t.groupId = :groupId AND t.user = :user")
	void deleteByGroupIdAndUser(@Param("groupId") String groupId, @Param("user") User user);

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

	@Query("""
			SELECT t from Transaction t
			WHERE t.user = :user
			AND MONTH(t.date) = :month
			AND YEAR(t.date) = :year
			ORDER BY t.date ASC
			""")
	List<Transaction> findByUserAndMonthAndYearOrderByDate(@Param("user") User user, @Param("month") int month,
			@Param("year") int year);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query("DELETE FROM Transaction t WHERE t.groupId = :groupId AND t.user = :user AND t.date >= :startDate")
	void deleteRecurrentFromGroupOnwards(@Param("groupId") String groupId, @Param("user") User user, @Param("startDate") java.time.LocalDate startDate);

}