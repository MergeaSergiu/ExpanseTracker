package expense.tracker.controller;


import expense.tracker.dto.ExpenseCategoryDto;
import expense.tracker.dto.ExpenseCategoryResponse;
import expense.tracker.service.ExpenseCategoryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    public ExpenseCategoryController(ExpenseCategoryService expenseCategoryService){
        this.expenseCategoryService = expenseCategoryService;
    }

    @PostMapping
    public ResponseEntity<String> createCategory(
            @RequestBody ExpenseCategoryDto expenseCategoryDto,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        expenseCategoryService.createExpenseCategory(expenseCategoryDto,authorization);
        return ResponseEntity.ok("Category created");
    }

    @GetMapping
    public List<ExpenseCategoryResponse> getExpenseCategories(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return expenseCategoryService.getExpenseCategories(authorization);
    }
}
