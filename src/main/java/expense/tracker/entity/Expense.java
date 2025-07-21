package expense.tracker.entity;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double amount;

    private LocalDate date;

    @ManyToOne
    private ExpenseCategory expenseCategory;

    private String documentURL;

    @ManyToOne
    private User user;

    public Expense(String name, Double amount, LocalDate date, ExpenseCategory expenseCategory, String documentURL, User user) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.expenseCategory = expenseCategory;
        this.documentURL = documentURL;
        this.user = user;
    }


    public Expense() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDocumentURL() {
        return documentURL;
    }

    public void setDocumentURL(String documentURL) {
        this.documentURL = documentURL;
    }

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
