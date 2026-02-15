package br.com.devl.mfc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.dto.CategoryRequestDTO;
import br.com.devl.mfc.dto.CategoryResponseDTO;
import br.com.devl.mfc.entity.Category;
import br.com.devl.mfc.exception.BusinessException;
import br.com.devl.mfc.repository.CategoryRepository;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public CategoryResponseDTO create(CategoryRequestDTO dto, User user) {

		if (categoryRepository.existsByNameAndUser(dto.name(), user)) {
			throw new BusinessException("A categoria " + dto.name() + " já existe!");
		}

		Category category = new Category();
		category.setName(dto.name());
		category.setType(dto.type());
		category.setUser(user);

		Category saved = categoryRepository.save(category);

		return toResponseDTO(saved);
	}

	public List<CategoryResponseDTO> list(User user) {
		return categoryRepository.findByUser(user).stream().map(this::toResponseDTO).toList();
	}

	public CategoryResponseDTO findById(Long id, User user) {

		Category category = categoryRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new BusinessException("Categoria não encontrada"));

		return toResponseDTO(category);

	}

	public CategoryResponseDTO update(Long id, CategoryRequestDTO dto, User user) {

		Category category = categoryRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new BusinessException("Categoria não encontrada"));

		category.setName(dto.name());
		category.setType(dto.type());

		Category updated = categoryRepository.save(category);

		return toResponseDTO(updated);

	}

	public void delete(Long id, User user) {
		Category category = categoryRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new BusinessException("Categoria não encontrada"));
		categoryRepository.delete(category);
	}

	private CategoryResponseDTO toResponseDTO(Category category) {
		return new CategoryResponseDTO(category.getId(), category.getName(), category.getType());
	}
}
