package com.example.demo.ExpensesAndIncomes.Transaction.RequestBody;

import com.example.demo.Validators.FamilyId.ValidFamilyId;

import java.math.BigDecimal;

public class CreateTransactionReqBody {
    @ValidFamilyId
    public int familyId;
    public String title;
    public String type;
    public String note;
    public int categoryId;
    public String repeatType;
    public String date;
    public BigDecimal cost;
    public String[] photos;
    public int occurrences;
}
