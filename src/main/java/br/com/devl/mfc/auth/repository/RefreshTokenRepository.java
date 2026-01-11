package br.com.devl.mfc.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import br.com.devl.mfc.auth.entity.RefreshToken;
import br.com.devl.mfc.auth.entity.User;
import jakarta.transaction.Transactional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	
	Optional<RefreshToken> findByToken(String token);

	@Modifying
	@Transactional
	void deleteByUser(User user);
}
