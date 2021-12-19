package com.example.demo.ExpensesAndIncomes.Transaction.Entity;

import com.example.demo.ExpensesAndIncomes.Transaction.IdClass.TransactionGroupIdClass;

import javax.persistence.*;

@Entity
@Table(name = "transaction_groups")
@IdClass(TransactionGroupIdClass.class)
public class TransactionGroups {
    @Id
    @Column(name = "head_id")
    private int headId;

    @Id
    @Column(name = "sub_id")
    private int subId;

    @ManyToOne
    @JoinColumn(name = "head", referencedColumnName = "id")
    private Transaction head;

    @OneToOne
    @JoinColumn(name = "sub", referencedColumnName = "id")
    private Transaction sub;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public TransactionGroups() {
    }

    public TransactionGroups(Transaction head, Transaction sub, boolean isDeleted) {
        this.head = head;
        this.sub = sub;
        this.isDeleted = isDeleted;
    }

    public Transaction getSub() {
        return sub;
    }

    public Transaction getHead() {
        return head;
    }

    public void setHead(Transaction head) {
        this.head = head;
        this.headId = head.getId();
    }

    public void setSub(Transaction sub) {
        this.sub = sub;
        this.subId = sub.getId();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
