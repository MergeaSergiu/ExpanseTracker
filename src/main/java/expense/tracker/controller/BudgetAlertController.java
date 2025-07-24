package expense.tracker.controller;

import expense.tracker.dto.BudgetAlertDto;
import expense.tracker.dto.BudgetAlertResponse;
import expense.tracker.entity.BudgetAlert;
import expense.tracker.service.BudgetAlertService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budget")
public class BudgetAlertController {

    private final BudgetAlertService budgetAlertService;

    public BudgetAlertController(BudgetAlertService budgetAlertService) {
        this.budgetAlertService = budgetAlertService;
    }

    @PostMapping
    public ResponseEntity<String> addOrUpdateBudgetAlert(@RequestBody BudgetAlertDto budgetDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        budgetAlertService.createOrUpdateAlert(authorization, budgetDto);
        return ResponseEntity.ok("The alert was created successfully.");
    }

    @GetMapping
    public ResponseEntity<List<BudgetAlertResponse>> getUserAlerts(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.ok(budgetAlertService.getUserAlerts(authorization));
    }
}
