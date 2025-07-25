package expense.tracker.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AlertHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private BudgetAlert budgetAlert;

    private LocalDateTime sentAt;

    // getters and setters
}