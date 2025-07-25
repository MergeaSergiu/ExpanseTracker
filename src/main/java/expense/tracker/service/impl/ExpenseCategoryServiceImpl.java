package expense.tracker.service.impl;

import expense.tracker.configuration.UtilsMethod;
import expense.tracker.dto.ExpenseCategoryDto;
import expense.tracker.dto.ExpenseCategoryResponse;
import expense.tracker.entity.ExpenseCategory;
import expense.tracker.entity.User;
import expense.tracker.repository.ExpenseCategoryRepository;
import expense.tracker.service.ExpenseCategoryService;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final UtilsMethod utilsMethod;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseCategoryServiceImpl(UtilsMethod utilsMethod, ExpenseCategoryRepository expenseCategoryRepository) {
        this.utilsMethod = utilsMethod;
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    @Override
    public void createExpenseCategory(ExpenseCategoryDto expenseCategoryDto, String authorization) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authorization);
        ExpenseCategory expenseCategory = expenseCategoryRepository.findByNameAndUserId(expenseCategoryDto.name(), user.getId()).orElse(null);
        if(expenseCategory != null) {
            throw new EntityExistsException("This category already exists in your list");
        }
        expenseCategory = new ExpenseCategory(expenseCategoryDto.name(), user);
        expenseCategoryRepository.save(expenseCategory);
    }

    @Override
    public List<ExpenseCategoryResponse> getExpenseCategories(String authorization) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authorization);
        return  expenseCategoryRepository.findByUserId(user.getId())
                .stream()
                .map(category -> new ExpenseCategoryResponse(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }
}
