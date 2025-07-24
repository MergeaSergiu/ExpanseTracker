package expense.tracker.repository;

import expense.tracker.entity.BudgetAlert;
import expense.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetAlertRepository extends JpaRepository<BudgetAlert, Long> {

    Optional<BudgetAlert> findByUserIdAndExpenseCategoryId(Long userId, Long categoryId);

    List<BudgetAlert> findByUser(User user);

}
