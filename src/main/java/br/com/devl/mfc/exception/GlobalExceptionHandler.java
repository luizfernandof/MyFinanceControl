package br.com.devl.mfc.exception;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Regra de Negócio", ex.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Credenciais inválidas",
				"Email ou senha incorretos");

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(PropertyReferenceException.class)
	public ResponseEntity<ErrorResponse> handlePropertyReference(PropertyReferenceException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Parâmetros inválidos",
				"Ordenação inválida: " + ex.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(InvalidDataAccessApiUsageException.class)
	public ResponseEntity<ErrorResponse> handleInvalidDataAccess(InvalidDataAccessApiUsageException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Parâmetros inválidos",
				ex.getMostSpecificCause().getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		String msg = ex.getBindingResult().getFieldErrors().stream()
				.map(f -> f.getField() + ": " + f.getDefaultMessage())
				.reduce((a, b) -> a + "; " + b).orElse("Erro de validação");
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Parâmetros inválidos", msg);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno",
				ex.getMessage());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
