package com.example.demo.ExpensesAndIncomes.Transaction.RequestBody;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

public class StatisticReqBody {
    @ValidFamilyId
    public int familyId;
    public int month;
    public int year;
    public String type;
}
