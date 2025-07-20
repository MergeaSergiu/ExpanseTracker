package expense.tracker.repository;

import expense.tracker.entity.Expense;
import expense.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Expense findByUserIdAndName(Long userId, String expenseName);

    List<Expense> findByUser(User user);
}
