package com.example.demo.ExpensesAndIncomes.TransactionCategory.RequestBody;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class CreateTransactionCategoryReqBody {
    @ValidFamilyId
    public int familyId;

    public String name;

    public String icon;

    public String parentId;

    public String type;
}
