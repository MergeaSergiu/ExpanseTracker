package expense.tracker.entity;

import jakarta.persistence.*;


@Entity
public class BudgetAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ExpenseCategory expenseCategory;
    private Double budgetLimit;

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

    public Double getBudgetLimit() {
        return budgetLimit;
    }

    public void setBudgetLimit(Double budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
