package com.example.demo.ExpensesAndIncomes.TransactionCategory.Entity;

import com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Entity.TransactionCategoryGroup;

import javax.persistence.*;
import java.util.HashMap;

@Entity
@Table(name = "transaction_categories")
public class TransactionCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "family_id")
    private Integer familyId;

    @Column(name = "icon")
    private String icon;

    @Column(name = "type")
    private String type;

//    @ManyToOne
//    @JoinColumn(name = "category_group", referencedColumnName = "id")
//    private TransactionCategoryGroup transactionCategoryGroup;

    @Column(name = "translated")
    private boolean translated = false;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public TransactionCategory() {
    }

    public TransactionCategory(String title, int familyId, String icon) {
        this.title = title;
        this.familyId = familyId;
        this.icon = icon;
//        this.transactionCategoryGroup = transactionCategoryGroup;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }



//    public TransactionCategoryGroup getTransactionCategoryGroup() {
//        return transactionCategoryGroup;
//    }
//
//    public void setTransactionCategoryGroup(TransactionCategoryGroup transactionCategoryGroup) {
//        this.transactionCategoryGroup = transactionCategoryGroup;
//    }

    public boolean isTranslated() {
        return translated;
    }

    public void setTranslated(boolean translated) {
        this.translated = translated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public HashMap<String, Object> getJson(String iconUrl){
        return new HashMap<>(){{
           put("title", title);
           put("translated", translated);
           put("categoryId", id);
           put("type", type);
//           put("categoryGroup", transactionCategoryGroup.getJson());
           put("icon", (iconUrl != null) ? iconUrl : icon);
        }};
    }
}
