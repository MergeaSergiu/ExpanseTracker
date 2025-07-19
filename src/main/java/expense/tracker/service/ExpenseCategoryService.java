package expense.tracker.service;

import expense.tracker.dto.ExpenseCategoryDto;
import expense.tracker.dto.ExpenseCategoryResponse;

import java.util.List;

public interface ExpenseCategoryService {


    void createExpenseCategory(ExpenseCategoryDto expenseCategoryDto, String authorization);

    List<ExpenseCategoryResponse> getExpenseCategories(String authorization);
}
