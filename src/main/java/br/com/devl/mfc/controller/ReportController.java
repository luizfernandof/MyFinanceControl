package br.com.devl.mfc.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reports", description = "Relatórios PDF")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

	private final ReportService reportService;

	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	private User getAuthenticatedUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@GetMapping(value = "/transactions/monthly", produces = MediaType.APPLICATION_PDF_VALUE)
	@Operation(summary = "Gerar relatório mensal de transações em PDF")
	public ResponseEntity<byte[]> generateMonthlyTransactionsPdf(
			@Parameter(description = "Mês (1-12)", example = "4") @RequestParam int month,
			@Parameter(description = "Ano", example = "2026") @RequestParam int year) {
		User user = getAuthenticatedUser();
		byte[] pdfBytes = reportService.generateMonthlyTransactionsPdf(user, month, year);
		return ResponseEntity.ok().header("Content-Disposition",
				"attachment; filename=transacoes_" + month + "_" + year + ".pdf").body(pdfBytes);
	}
}
