package br.com.devl.mfc.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.devl.mfc.auth.dto.LoginRequest;
import br.com.devl.mfc.auth.dto.LoginResponse;
import br.com.devl.mfc.auth.service.AuthService;
import br.com.devl.mfc.auth.service.LogoutService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;
	private final LogoutService logoutService;
	
	public AuthController(AuthService authService, LogoutService logoutService) {
		this.authService = authService;
		this.logoutService = logoutService;
	}
	
	@PostMapping("/login")
	public LoginResponse loginResponse(@RequestBody LoginRequest request) {
		String token = authService.login(request.getEmail(), request.getPassword());
		return new LoginResponse(token);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
		String token = authHeader.substring(7);
		logoutService.logout(token);
		return ResponseEntity.noContent().build();
	} 

}
