package com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.RequestBody;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

import javax.validation.Valid;

public class GetAllTransactionCategoryGroupReqBody {
    @ValidFamilyId
    public int familyId;
}
