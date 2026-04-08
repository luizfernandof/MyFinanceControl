package br.com.devl.mfc.auth.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.devl.mfc.auth.dto.AuthResponse;
import br.com.devl.mfc.auth.dto.LoginRequest;
import br.com.devl.mfc.auth.dto.LogoutRequest;
import br.com.devl.mfc.auth.dto.RefreshTokenRequest;
import br.com.devl.mfc.auth.dto.RefreshTokenResponse;
import br.com.devl.mfc.auth.dto.RegisterRequest;
import br.com.devl.mfc.auth.entity.RefreshToken;
import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.auth.repository.UserRepository;
import br.com.devl.mfc.auth.service.JwtService;
import br.com.devl.mfc.auth.service.RefreshTokenService;
import br.com.devl.mfc.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints referentes a autenticação")
public class AuthController {

	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private final UserRepository userRepository;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
			RefreshTokenService refreshTokenService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("Este email já foi cadastrado!");
		}
		User user = new User();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		User savedUser = userRepository.save(user);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(savedUser.getId()).toUri();
		return ResponseEntity.created(location).build();
	}

	@Operation(summary = "Realiza autenticação do usuário", description = "Retorna um JWT de accessToken e um refreshToken")
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new BusinessException("Usuário não encontrado!"));
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
				.orElseThrow(() -> new BusinessException("RefreshToken inválido!"));
		User user = refreshToken.getUser();
		String newAccessToken = jwtService.generateToken(user);
		return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken));
	}

}
