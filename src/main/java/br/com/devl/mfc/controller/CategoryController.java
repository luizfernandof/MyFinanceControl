package br.com.devl.mfc.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.dto.CategoryRequestDTO;
import br.com.devl.mfc.dto.CategoryResponseDTO;
import br.com.devl.mfc.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	private User getAuthenticatedUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@PostMapping
	public ResponseEntity<CategoryResponseDTO> create(@RequestBody CategoryRequestDTO dto) {
		User user = getAuthenticatedUser();
		return ResponseEntity.ok(categoryService.create(dto, user));
	}
	
	@GetMapping
	public ResponseEntity<List<CategoryResponseDTO>> list() {
		User user = getAuthenticatedUser();
		return ResponseEntity.ok(categoryService.list(user));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponseDTO> findById(@PathVariable Long id) {
		User user = getAuthenticatedUser();
		return ResponseEntity.ok(categoryService.findById(id, user));
	} 
	
	@PutMapping("/{id}")
	public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @RequestBody CategoryRequestDTO dto) {
		User user = getAuthenticatedUser();
		return ResponseEntity.ok(categoryService.update(id, dto, user));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		User user = getAuthenticatedUser();
		categoryService.delete(id, user);
		return ResponseEntity.noContent().build();
	}
	
}
