package com.example.demo.ExpensesAndIncomes.TransactionCategoryGroup.Entity;

import javax.persistence.*;
import javax.print.attribute.HashAttributeSet;
import java.util.HashMap;

@Entity
@Table(name = "transaction_category_groups")
public class TransactionCategoryGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "translated")
    private boolean translated = true;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public TransactionCategoryGroup() {
    }

    public TransactionCategoryGroup(String title) {
        this.title = title;
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

    public boolean isTranslated() {
        return translated;
    }

    public void setTranslated(boolean translated) {
        this.translated = translated;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public HashMap<String, Object> getJson(){
        return new HashMap<>(){{
            put("id", id);
           put("title", title);
           put("translated", translated);
        }};
    }
}
