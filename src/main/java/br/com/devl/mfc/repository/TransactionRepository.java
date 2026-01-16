package br.com.devl.mfc.repository;

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
	List<Transaction> findByUserAndDateBetween(
			User user,
			LocalDate start,
			LocalDate end
	);

}
