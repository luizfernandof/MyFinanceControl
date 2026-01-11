package br.com.devl.mfc.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.devl.mfc.auth.dto.AuthResponse;
import br.com.devl.mfc.auth.dto.LoginRequest;
import br.com.devl.mfc.auth.dto.LogoutRequest;
import br.com.devl.mfc.auth.dto.RefreshTokenRequest;
import br.com.devl.mfc.auth.dto.RefreshTokenResponse;
import br.com.devl.mfc.auth.entity.RefreshToken;
import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.auth.repository.UserRepository;
import br.com.devl.mfc.auth.service.JwtService;
import br.com.devl.mfc.auth.service.RefreshTokenService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private final UserRepository userRepository;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
			RefreshTokenService refreshTokenService, UserRepository userRepository) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
		this.userRepository = userRepository;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

		String accessToken = jwtService.generateToken(user);

		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

		return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {

		refreshTokenService.findByToken(request.getRefreshToken()).ifPresent(token -> {
			refreshTokenService.deleteByUser(token.getUser());
		});

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/refresh")
	public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		String requestRefreshToken = refreshTokenRequest.getRefreshToken();

		RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
				.map(refreshTokenService::verifyExpiration)
				.orElseThrow(() -> new RuntimeException("Refresh Token Inválido!"));

		User user = refreshToken.getUser();

		String newAccessToken = jwtService.generateToken(user);

		return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken));
	}

}
