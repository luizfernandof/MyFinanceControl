package br.com.devl.mfc.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.dto.TransactionRequestDTO;
import br.com.devl.mfc.dto.TransactionResponseDTO;
import br.com.devl.mfc.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

	private final TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	private User getAuthenticatedUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@PostMapping
	public ResponseEntity<TransactionResponseDTO> create(@RequestBody TransactionRequestDTO dto) {
		User user = getAuthenticatedUser();
		return ResponseEntity.ok(transactionService.create(dto, user));
	}

	@GetMapping
	public ResponseEntity<Page<TransactionResponseDTO>> list(@RequestParam int month, @RequestParam int year,
			@PageableDefault(size = 10, sort = "date") Pageable pageable) {
		User user = getAuthenticatedUser();
		return ResponseEntity.ok(transactionService.list(user, month, year, pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TransactionResponseDTO> findById(@PathVariable Long id) {
		User user = getAuthenticatedUser();
		return ResponseEntity.ok(transactionService.findById(id, user));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TransactionResponseDTO> update(@PathVariable Long id,
			@RequestBody TransactionRequestDTO dto) {
		User user = getAuthenticatedUser();
		return ResponseEntity.ok(transactionService.update(id, dto, user));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		User user = getAuthenticatedUser();
		transactionService.delete(id, user);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}/recurrent-forward")
	public ResponseEntity<Void> deleteFromDateForward(@PathVariable Long id) {
		User user = getAuthenticatedUser();
		transactionService.deleteRecurrentForward(id, user);
		return ResponseEntity.noContent().build();
	}

}