package expense.tracker.repository;

import expense.tracker.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {

    boolean existsByBudgetAlertIdAndSentAtAfter(Long budgetId, LocalDateTime after);
}
