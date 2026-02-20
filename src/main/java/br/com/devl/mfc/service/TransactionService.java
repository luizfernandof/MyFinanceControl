package br.com.devl.mfc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.dto.TransactionRequestDTO;
import br.com.devl.mfc.dto.TransactionResponseDTO;
import br.com.devl.mfc.entity.Category;
import br.com.devl.mfc.entity.Transaction;
import br.com.devl.mfc.exception.BusinessException;
import br.com.devl.mfc.repository.CategoryRepository;
import br.com.devl.mfc.repository.TransactionRepository;

@Service
public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final CategoryRepository categoryRepository;

	public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
		this.transactionRepository = transactionRepository;
		this.categoryRepository = categoryRepository;
	}

	@Transactional
	public TransactionResponseDTO create(TransactionRequestDTO dto, User user) {
		Category category = categoryRepository.findByIdAndUser(dto.categoryId(), user)
				.orElseThrow(() -> new BusinessException("Categoria não encontrada"));

		validateTransaction(dto, category);

		int totalInstallments = (dto.installments() != null && dto.installments() > 1) ? dto.installments() : 1;

		// CÁLCULO DA PARCELA: Divide o valor total pela quantidade de parcelas
		BigDecimal installmentAmount = dto.amount().divide(BigDecimal.valueOf(totalInstallments), 2,
				RoundingMode.HALF_UP);

		Transaction firstSaved = null;

		for (int i = 0; i < totalInstallments; i++) {
			Transaction transaction = new Transaction();

			String description = totalInstallments > 1
					? dto.description() + " (" + (i + 1) + "/" + totalInstallments + ")"
					: dto.description();

			transaction.setDescription(description);

			// SALVA O VALOR FRACIONADO
			transaction.setAmount(installmentAmount);

			transaction.setDate(dto.date().plusMonths(i));
			transaction.setType(dto.type());
			transaction.setCategory(category);
			transaction.setUser(user);

			Transaction saved = transactionRepository.save(transaction);

			if (i == 0) {
				firstSaved = saved;
			}
		}

		return toResponseDTO(firstSaved);
	}

	/**
	 * Lista transações paginadas filtrando por usuário, mês e ano.
	 */
	public Page<TransactionResponseDTO> list(User user, int month, int year, Pageable pageable) {
		return transactionRepository.findByUserAndMonthAndYear(user, month, year, pageable).map(this::toResponseDTO);
	}

	public TransactionResponseDTO findById(Long id, User user) {
		Transaction transaction = transactionRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new BusinessException("Transação não encontrada"));
		return toResponseDTO(transaction);
	}

	public TransactionResponseDTO update(Long id, TransactionRequestDTO dto, User user) {

		Transaction transaction = transactionRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new BusinessException("Transação não encontrada"));

		Category category = categoryRepository.findByIdAndUser(dto.categoryId(), user)
				.orElseThrow(() -> new BusinessException("Categoria não encontrada"));

		validateTransaction(dto, category);

		transaction.setDescription(dto.description());
		transaction.setAmount(dto.amount());
		transaction.setDate(dto.date());
		transaction.setType(dto.type());
		transaction.setCategory(category);

		Transaction updated = transactionRepository.save(transaction);

		return toResponseDTO(updated);
	}

	public void delete(Long id, User user) {
		Transaction transaction = transactionRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new BusinessException("Transação não encontrada"));
		transactionRepository.delete(transaction);
	}

	private TransactionResponseDTO toResponseDTO(Transaction transaction) {
		return new TransactionResponseDTO(transaction.getId(), transaction.getDescription(), transaction.getAmount(),
				transaction.getDate(), transaction.getType(), transaction.getCategory().getName());
	}

	private void validateTransaction(TransactionRequestDTO dto, Category category) {
		if (!category.getType().name().equals(dto.type().name())) {
			throw new BusinessException("O tipo da transação deve ser igual ao tipo da categoria!");
		}

		if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BusinessException("O valor deve ser maior que zero(0)!");
		}

		if (dto.date() == null) {
			throw new BusinessException("Data é obrigatória!");
		}
	}
}