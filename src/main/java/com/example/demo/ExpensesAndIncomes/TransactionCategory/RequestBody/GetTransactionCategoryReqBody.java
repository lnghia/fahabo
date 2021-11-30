package com.example.demo.ExpensesAndIncomes.TransactionCategory.RequestBody;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class GetTransactionCategoryReqBody {
    @ValidFamilyId
    public int familyId;
    public String type;
}
