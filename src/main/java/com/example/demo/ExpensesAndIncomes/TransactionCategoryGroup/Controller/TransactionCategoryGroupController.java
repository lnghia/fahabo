package com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Controller;

import com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Entity.TransactionCategoryGroup;
import com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.RequestBody.GetAllTransactionCategoryGroupReqBody;
import com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Service.TransactionCategoryGroupService;
import com.example.demo.ResponseFormat.Response;
import com.example.demo.Service.Family.FamilyService;
import com.example.demo.domain.CustomUserDetails;
import com.example.demo.domain.Family.Family;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transaction_category_group")
public class TransactionCategoryGroupController {
    @Autowired
    private TransactionCategoryGroupService transactionCategoryGroupService;

    @Autowired
    private FamilyService familyService;

    @PostMapping
    public ResponseEntity<Response> getAll() {
        ArrayList<TransactionCategoryGroup> transactionCategoryGroups = transactionCategoryGroupService.findAll();

        return ResponseEntity.ok(
                new Response(
                        transactionCategoryGroups.stream().map(transactionCategoryGroup -> transactionCategoryGroup.getJson()).collect(Collectors.toList()),
                        new ArrayList<>()));
    }
}
