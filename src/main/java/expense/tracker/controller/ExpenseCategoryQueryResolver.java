package expense.tracker.controller;

import expense.tracker.dto.ExpenseCategoryResponse;
import expense.tracker.service.ExpenseCategoryService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Controller
public class ExpenseCategoryQueryResolver {

    private final ExpenseCategoryService expenseCategoryService;


    public ExpenseCategoryQueryResolver(ExpenseCategoryService expenseCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
    }

//    @QueryMapping
//    public List<ExpenseCategoryResponse> getExpenseCategories(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
//        return expenseCategoryService.getExpenseCategories(authorization);
//    }
}
