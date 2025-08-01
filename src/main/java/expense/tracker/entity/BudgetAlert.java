package expense.tracker.entity;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
public class BudgetAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ExpenseCategory expenseCategory;

    private LocalDate lastSent;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getLastSent() {
        return lastSent;
    }

    public void setLastSent(LocalDate lastSent) {
        this.lastSent = lastSent;
    }
}
