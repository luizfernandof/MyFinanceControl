package br.com.devl.mfc.dto;

import br.com.devl.mfc.enums.CategoryType;

public record CategoryResponseDTO (
		Long id,
		String name,
		CategoryType type
) {}
