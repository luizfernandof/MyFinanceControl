package br.com.devl.mfc.dto;

import br.com.devl.mfc.enums.CategoryType;

public record CategoryRequestDTO (
		String name,
		CategoryType type
) {}
