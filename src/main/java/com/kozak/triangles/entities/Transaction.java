package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.TransferTypes;

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
	@Column(name = "ID")
    @GeneratedValue
    private Integer id;

	@Column(name = "DESCRIPTION", length = 100)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TRANSACT_DATE")
    private Date transactDate;

	@Column(name = "SUMMA")
    private long summa;

    // тип движения - приход или расход
	@Column(name = "TRANSFER_TYPE")
    @Enumerated(EnumType.STRING)
    private TransferTypes transferType;

    // чьи деньги
	@Column(name = "USER_ID")
    private int userId;

    // остаток на конец
	@Column(name = "BALANCE")
    private long balance;

    // статья движения денежных средств
	@Column(name = "ARTICLE_CASH_FLOW")
    @Enumerated(EnumType.STRING)
    private ArticleCashFlow articleCashFlow;

    // ///////////////////////////// constructors
    public Transaction() {
    }

    public Transaction(String description, Date transactDate, long sum, TransferTypes transferType, int userId,
			long newBalance, ArticleCashFlow articleCashFlow) {
        this.description = description;
        this.transactDate = transactDate;
        this.summa = sum;
        this.transferType = transferType;
        this.userId = userId;
		this.balance = newBalance;
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

    public TransferTypes getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferTypes transferType) {
        this.transferType = transferType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ArticleCashFlow getArticleCashFlow() {
        return articleCashFlow;
    }

    public void setArticleCashFlow(ArticleCashFlow articleCashFlow) {
        this.articleCashFlow = articleCashFlow;
    }
}
