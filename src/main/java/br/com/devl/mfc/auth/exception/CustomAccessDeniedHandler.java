package br.com.devl.mfc.auth.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.devl.mfc.exception.ErrorResponse;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException)
			throws IOException, JsonProcessingException, java.io.IOException {

		ErrorResponse error = new ErrorResponse(403, "Acesso negado",
				"Você não tem permissão para acessar este recurso");

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json");
		response.getWriter().write(objectMapper.writeValueAsString(error));
	}
}
