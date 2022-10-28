package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "transactionDate", "narrative", "referenceNo", "debitAmount", "creditAmount", "runningBalance",
		"txnSeqNo" })
@JsonbNillable
public class DepositTransactionObject {

	@JsonbProperty("TransactionDate")
	private String transactionDate;

	@JsonbProperty("Narrative")
	private String narrative;

	@JsonbProperty("ReferenceNo")
	private String referenceNo;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DebitAmount")
	private Double debitAmount;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("CreditAmount")
	private Double creditAmount;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("RunningBalance")
	private Double runningBalance;

	@JsonbProperty("TxnSeqNo")
	private Integer txnSeqNo;

	public DepositTransactionObject() {
		this.setTransactionDate(transactionDate);
		this.setNarrative(narrative);
		this.setReferenceNo(referenceNo);
		this.setDebitAmount(debitAmount);
		this.setCreditAmount(creditAmount);
		this.setRunningBalance(runningBalance);
		this.setTxnSeqNo(txnSeqNo);
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public Double getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(Double debitAmount) {
		this.debitAmount = debitAmount;
	}

	public Double getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Double creditAmount) {
		this.creditAmount = creditAmount;
	}

	public Double getRunningBalance() {
		return runningBalance;
	}

	public void setRunningBalance(Double runningBalance) {
		this.runningBalance = runningBalance;
	}

	public Integer getTxnSeqNo() {
		return txnSeqNo;
	}

	public void setTxnSeqNo(Integer txnSeqNo) {
		this.txnSeqNo = txnSeqNo;
	}

}
