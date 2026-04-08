package br.com.devl.mfc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

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

		// Determina o número de iterações (parcelas ou ocorrências recorrentes)
		int iterations = 1;
		if (dto.installments() != null && dto.installments() > 1) {
			iterations = dto.installments();
		} else if (dto.recurring() && dto.occurrences() != null && dto.occurrences() > 1) {
			iterations = dto.occurrences();
		}

		// Gera um ID único para o grupo se houver repetição
		String groupId = iterations > 1 ? UUID.randomUUID().toString() : null;

		// CÁLCULO DO VALOR:
		// Se for parcelamento, divide o total. Se for recorrência (mensalidade), mantém
		// o valor cheio.
		BigDecimal valuePerEntry = (dto.installments() != null && dto.installments() > 1)
				? dto.amount().divide(BigDecimal.valueOf(iterations), 2, RoundingMode.HALF_UP)
				: dto.amount();

		Transaction firstSaved = null;

		for (int i = 0; i < iterations; i++) {
			Transaction transaction = new Transaction();

			// Define a descrição com sufixo apropriado
			String suffix = "";
			if (dto.installments() != null && dto.installments() > 1) {
				suffix = " (" + (i + 1) + "/" + iterations + ")";
			} else if (dto.recurring()) {
				suffix = " [Recorrente]";
			}

			transaction.setDescription(dto.description() + suffix);
			transaction.setAmount(valuePerEntry);
			transaction.setDate(dto.date().plusMonths(i));
			transaction.setType(dto.type());
			transaction.setCategory(category);
			transaction.setUser(user);
			transaction.setGroupId(groupId);

			Transaction saved = transactionRepository.save(transaction);

			if (i == 0) {
				firstSaved = saved;
			}
		}

		return toResponseDTO(firstSaved);
	}

	public Page<TransactionResponseDTO> list(User user, int month, int year, Pageable pageable) {
		return transactionRepository.findByUserAndMonthAndYear(user, month, year, pageable).map(this::toResponseDTO);
	}

	public TransactionResponseDTO findById(Long id, User user) {
		Transaction transaction = transactionRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new BusinessException("Transação não encontrada"));
		return toResponseDTO(transaction);
	}

	@Transactional
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

	@Transactional
	public void delete(Long id, User user) {
		Transaction transaction = transactionRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new BusinessException("Transação não encontrada"));

		// Exclui todo o grupo (parcelas ou recorrências) se existir um groupId
		if (transaction.getGroupId() != null) {
			transactionRepository.deleteByGroupIdAndUser(transaction.getGroupId(), user);
		} else {
			transactionRepository.delete(transaction);
		}
	}

	@Transactional
	public void deleteRecurrentForward(Long id, User user) {
		Transaction transaction = transactionRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new BusinessException("Transação não encontrada"));

		if (transaction.getGroupId() == null) {
			throw new BusinessException("Transação não pertence a um grupo recorrente");
		}

		transactionRepository.deleteRecurrentFromGroupOnwards(transaction.getGroupId(), user, transaction.getDate());
	}

	private TransactionResponseDTO toResponseDTO(Transaction transaction) {
		return new TransactionResponseDTO(transaction.getId(), transaction.getDescription(), transaction.getAmount(),
				transaction.getDate(), transaction.getType(), transaction.getCategory().getName(),
				transaction.getGroupId());
	}

	private void validateTransaction(TransactionRequestDTO dto, Category category) {
		if (!category.getType().toString().equals(dto.type().toString())) {
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