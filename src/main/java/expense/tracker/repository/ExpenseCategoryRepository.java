package expense.tracker.repository;

import expense.tracker.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    List<ExpenseCategory> findByUserId(Long userId);
    Optional<ExpenseCategory> findByNameAndUserId(String categoryName, Long userId);

    Optional<ExpenseCategory> findByIdAndUserId(Long expenseCategoryId, Long userId);
}
