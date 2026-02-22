package br.com.devl.mfc.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.devl.mfc.dto.CategoryExpenseDTO;
import br.com.devl.mfc.dto.DashboardSummaryDTO;
import br.com.devl.mfc.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
	
	private final DashboardService service;
	
	public DashboardController(DashboardService service) {
		this.service = service;
	}
	
	@GetMapping("/summary")
	public ResponseEntity<DashboardSummaryDTO> getSummary(@RequestParam int month, @RequestParam int year) {
		return ResponseEntity.ok(service.getSummary(month, year));
	}

	@GetMapping("/expenses-by-category")
    public ResponseEntity<List<CategoryExpenseDTO>> getExpensesByCategory(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(service.getExpensesByCategory(month, year));
    }
}
