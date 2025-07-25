package expense.tracker.service;

import expense.tracker.dto.BudgetAlertDto;
import expense.tracker.dto.BudgetAlertResponse;

import java.util.List;


public interface BudgetAlertService {
    void createOrUpdateAlert(String authorization, BudgetAlertDto budgetDto);

   List<BudgetAlertResponse> getUserAlerts(String authorization);
}
