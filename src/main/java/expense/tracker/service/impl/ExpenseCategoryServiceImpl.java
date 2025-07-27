package expense.tracker.service.impl;

import expense.tracker.configuration.UtilsMethod;
import expense.tracker.dto.ExpenseCategoryDto;
import expense.tracker.dto.ExpenseCategoryResponse;
import expense.tracker.dto.ResponseMessage;
import expense.tracker.entity.ExpenseCategory;
import expense.tracker.entity.User;
import expense.tracker.repository.BudgetAlertRepository;
import expense.tracker.repository.ExpenseCategoryRepository;
import expense.tracker.repository.ExpenseRepository;
import expense.tracker.service.ExpenseCategoryService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final UtilsMethod utilsMethod;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetAlertRepository budgetAlertRepository;

    public ExpenseCategoryServiceImpl(UtilsMethod utilsMethod, ExpenseCategoryRepository expenseCategoryRepository, ExpenseRepository expenseRepository, BudgetAlertRepository budgetAlertRepository) {
        this.utilsMethod = utilsMethod;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.expenseRepository = expenseRepository;
        this.budgetAlertRepository = budgetAlertRepository;
    }

    @Override
    public void createExpenseCategory(ExpenseCategoryDto expenseCategoryDto, String authorization) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authorization);
        ExpenseCategory expenseCategory = expenseCategoryRepository.findByNameAndUserId(expenseCategoryDto.name(), user.getId()).orElse(null);
        if(expenseCategory != null) {
            throw new EntityExistsException("This category already exists in your list");
        }
        expenseCategory = new ExpenseCategory(expenseCategoryDto.name(), expenseCategoryDto.budgetLimit(), user);
        expenseCategoryRepository.save(expenseCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseCategoryResponse> getExpenseCategories(String authorization) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authorization);
        return  expenseCategoryRepository.findByUserId(user.getId())
                .stream()
                .map(category -> new ExpenseCategoryResponse(category.getId(), category.getName(), category.getBudgetLimit()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseMessage deleteExpenseCategory(String authorization, Long categoryId) {
        User user = utilsMethod.extractUsernameFromAuthorizationHeader(authorization);
        ExpenseCategory expenseCategory = expenseCategoryRepository.findByIdAndUserId(categoryId, user.getId()).orElse(null);
        if(expenseCategory == null) throw new EntityNotFoundException("This category does not exist in your list");
        budgetAlertRepository.deleteAllByExpenseCategoryId(expenseCategory.getId());
        expenseRepository.deleteAllByExpenseCategoryId(expenseCategory.getId());
        expenseCategoryRepository.deleteById(expenseCategory.getId());
        return new ResponseMessage("The category " +  expenseCategory.getName() + " and all related expenses were removed.");
    }

    @Override
    public void updateExpenseCategory(Long categoryId, ExpenseCategoryDto expenseCategoryDto) {
        ExpenseCategory expenseCategory = expenseCategoryRepository.findById(categoryId).orElse(null);
        if(expenseCategory == null) throw new EntityNotFoundException("This category does not exist in your list");
        expenseCategory.setName(expenseCategoryDto.name());
        expenseCategory.setBudgetLimit(expenseCategoryDto.budgetLimit());
        expenseCategoryRepository.save(expenseCategory);
    }
}
