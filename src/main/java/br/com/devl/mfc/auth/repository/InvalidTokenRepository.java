package br.com.devl.mfc.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.devl.mfc.auth.entity.InvalidToken;

public interface InvalidTokenRepository extends JpaRepository<InvalidToken, Long> {
	boolean existsByToken(String token);
}
