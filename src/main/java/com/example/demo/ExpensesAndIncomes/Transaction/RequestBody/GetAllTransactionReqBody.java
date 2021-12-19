package com.example.demo.ExpensesAndIncomes.Transaction.RequestBody;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class GetAllTransactionReqBody {
    @ValidFamilyId
    public int familyId;
    public String searchText;
    public String from;
    public String to;
}
