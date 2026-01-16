package br.com.devl.mfc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	List<Category> findByUser(User user);
	
	Optional<Category> findByIdAndUser(Long id, User user);
	
	boolean existsByNameAndUser(String name, User user);

}
