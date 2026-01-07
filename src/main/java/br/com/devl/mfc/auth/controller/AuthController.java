package br.com.devl.mfc.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.devl.mfc.auth.dto.LoginRequest;
import br.com.devl.mfc.auth.dto.LoginResponse;
import br.com.devl.mfc.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/login")
	public LoginResponse loginResponse(@RequestBody LoginRequest request) {
		String token = authService.login(request.getEmail(), request.getPassword());
		return new LoginResponse(token);
	}

}
