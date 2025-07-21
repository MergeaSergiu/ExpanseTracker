package expense.tracker.repository;

import expense.tracker.entity.Expense;
import expense.tracker.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Expense findByUserIdAndName(Long userId, String expenseName);

    Expense findByUserIdAndId(Long userId, Long id);

    List<Expense> findByUser(User user);

    @Query("SELECT e FROM Expense e WHERE e.user = :user ORDER BY e.amount DESC")
    List<Expense> findTopExpenses(@Param("user") User user, Pageable pageable);
}
