package br.com.devl.mfc.auth.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.devl.mfc.auth.entity.RefreshToken;
import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.auth.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

	@Value("${jwt.refresh-expiration}")
	private Long refreshTokenDurationMs;
	
	private final RefreshTokenRepository refreshTokenRepository;
	
	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
	}
	
	public RefreshToken createRefreshToken(User user) {
		
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(user);
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setExpiryDate(
				Instant.now().plusMillis(refreshTokenDurationMs)
		);
		
		return refreshTokenRepository.save(refreshToken);	
	}
	
	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}
	
	public RefreshToken verifyExpiration(RefreshToken token) {
		if(token.getExpiryDate().isBefore(Instant.now())) {
			refreshTokenRepository.delete(token);
			throw new RuntimeException("Refresh Token Expirado. Autentique novamente!");
		}
		
		return token;
	}
	
	public void deleteByUser(User user) {
		refreshTokenRepository.deleteByUser(user);
	}
}
