package br.com.devl.mfc.auth.service;

import org.springframework.stereotype.Service;

import br.com.devl.mfc.auth.entity.InvalidToken;
import br.com.devl.mfc.auth.repository.InvalidTokenRepository;

@Service
public class LogoutService {

	private final InvalidTokenRepository repository;
	private final JwtService jwtService;
	
	public LogoutService(InvalidTokenRepository repository, JwtService jwtService) {
		this.repository = repository;
		this.jwtService = jwtService;
	}
	
	public void logout(String token) {
		if(repository.existsByToken(token)) {
			return;
		}
		InvalidToken invalidToken = new InvalidToken();
		invalidToken.setToken(token);
		invalidToken.setExpiration(jwtService.getExpirationToken(token));
		repository.save(invalidToken);
	}
	
}
