package com.example.demo.ExpensesAndIncomes.Transaction.Entity;

import com.example.demo.ExpensesAndIncomes.TransactionCategory.Entity.TransactionCategory;
import com.example.demo.Helpers.Helper;
import com.example.demo.domain.Family.Family;
import liquibase.pro.packaged.C;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private TransactionCategory category;

    @ManyToOne
    @JoinColumn(name = "family_id", referencedColumnName = "id")
    private Family family;

    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;

    @Column(name = "note")
    private String note;

    @Column(name = "repeat_type")
    private String repeatType;

    @Column(name = "occurrences")
    private int occurrences;

    @Column(name = "date")
    private Date date;

    @Column(name = "cost")
    private BigDecimal cost;

    @Column(name = "is_deleted")
    private boolean is_deleted = false;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToOne(mappedBy = "transaction")
    private TransactionAlbum transactionAlbum;

//    @OneToOne(mappedBy = "sub")
//    private TransactionGroups transactionGroups;

    public Transaction() {
    }

    public Transaction(TransactionCategory category, Family family, String type, String title, String note, Date date, BigDecimal cost) {
        this.category = category;
        this.family = family;
        this.type = type;
        this.title = title;
        this.note = note;
        this.date = date;
        this.cost = cost;
    }

    public Transaction(TransactionCategory category, Family family, String type, String title, String note, String repeatType, int occurrences, Date date, BigDecimal cost) {
        this.category = category;
        this.family = family;
        this.type = type;
        this.title = title;
        this.note = note;
        this.repeatType = repeatType;
        this.occurrences = occurrences;
        this.date = date;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public TransactionAlbum getTransactionAlbum() {
        return transactionAlbum;
    }

    public void setTransactionAlbum(TransactionAlbum transactionAlbum) {
        this.transactionAlbum = transactionAlbum;
    }

//    public TransactionGroups getTransactionGroups() {
//        return transactionGroups;
//    }

//    public void setTransactionGroups(TransactionGroups transactionGroups) {
//        this.transactionGroups = transactionGroups;
//    }

    public HashMap<String, Object> getJson(String iconUrl) {
        return new HashMap<>() {{
            put("title", title);
            put("transactionId", id);
            put("type", type);
            put("date", Helper.getInstance().formatDateWithoutTime(date));
            put("cost", cost.toString());
            put("note", note);
            put("repeatType", repeatType);
            put("category", category.getJson((iconUrl != null) ? iconUrl : category.getIcon()));
        }};
    }
}
