package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.enums.TransferT;

/**
 * Транзакция. Отображает действия, которые приводят к изменению денежного баланса персонажа.
 * 
 * Экземпляр этой сущности - одна конкретная операция с деньгами
 * 
 * @author Roman
 */

@Entity(name = "Transac")
@Table(name = "Transac")
public class Transaction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "description", length = 100)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transactDate")
    private Date transactDate;

    @Column(name = "summa")
    private long summa;

    // тип движения - приход или расход
    @Column(name = "transferType")
    @Enumerated(EnumType.STRING)
    private TransferT transferType;

    // чьи деньги
    @Column(name = "user_id")
    private int userId;

    // остаток на конец
    @Column(name = "balance")
    private long balance;

    // статья движения денежных средств
    @Column(name = "article_cash_flow")
    @Enumerated(EnumType.STRING)
    private ArticleCashFlowT articleCashFlow;

    // ///////////////////////////// constructors
    public Transaction() {
    }

    public Transaction(String description, Date transactDate, long sum, TransferT transferType, int userId,
            long balance, ArticleCashFlowT articleCashFlow) {
        this.description = description;
        this.transactDate = transactDate;
        this.summa = sum;
        this.transferType = transferType;
        this.userId = userId;
        this.balance = balance;
        this.articleCashFlow = articleCashFlow;
    }

    // ///////////////////////////// getters and setters
    public Integer getId() {
        return id;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTransactDate() {
        return transactDate;
    }

    public void setTransactDate(Date transactDate) {
        this.transactDate = transactDate;
    }

    public long getSum() {
        return summa;
    }

    public void setSum(long sum) {
        this.summa = sum;
    }

    public TransferT getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferT transferType) {
        this.transferType = transferType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ArticleCashFlowT getArticleCashFlow() {
        return articleCashFlow;
    }

    public void setArticleCashFlow(ArticleCashFlowT articleCashFlow) {
        this.articleCashFlow = articleCashFlow;
    }
}
