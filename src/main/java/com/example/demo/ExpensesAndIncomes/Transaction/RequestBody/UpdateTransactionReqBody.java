package com.example.demo.ExpensesAndIncomes.Transaction.RequestBody;

import java.math.BigDecimal;

public class UpdateTransactionReqBody {
    public int transactionId;
    public boolean updateAll = false;
    public String note;
    public Integer categoryId;
    public String date;
    public String[] photos;
    public int[] deletePhotos;
    public Integer occurrences;
    public String repeatType;
    public String title;
    public String type;
    public BigDecimal cost;
}
