package br.com.devl.mfc.auth.exception;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.devl.mfc.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public SecurityExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        ErrorResponse error = new ErrorResponse(
                401,
                "Não autenticado",
                "Token JWT ausente ou inválido"
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}