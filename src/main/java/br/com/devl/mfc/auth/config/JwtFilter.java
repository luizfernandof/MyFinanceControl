package br.com.devl.mfc.auth.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.auth.repository.UserRepository;
import br.com.devl.mfc.auth.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	public JwtFilter(JwtService jwtService, UserRepository userRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.startsWith("/auth/")
		        || path.startsWith("/h2-console")
		        || path.startsWith("/swagger-ui")
		        || path.startsWith("/v3/api-docs");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			try {
				String email = jwtService.getEmail(token);
				User user = userRepository.findByEmail(email).orElse(null);

				if (user != null) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null,
							List.of());
					SecurityContextHolder.getContext().setAuthentication(auth);
					filterChain.doFilter(request, response);
					return;
				}
			} catch (ExpiredJwtException e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				response.getWriter().write("{\"error\": \"Token expirado\", \"message\": \"" + e.getMessage() + "\"}");
				return;
			} catch (JwtException e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				response.getWriter().write("{\"error\": \"Token inválido\", \"message\": \"" + e.getMessage() + "\"}");
				return;
			}
		}

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write("{\"error\": \"Token não fornecido\"}");
	}

}
