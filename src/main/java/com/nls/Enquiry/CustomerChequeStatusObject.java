package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbNillable(true)
@JsonbPropertyOrder({"accountNumber", "accountName", "chequeCurrency", "chequeNumber", "chequeAmount",
		"chequeDate", "chequeReceivedDate", "chequePaidDate", "chequeStatus", "chequePayeeName", "seqNo",
		"chequeType" })
public class CustomerChequeStatusObject {

	@JsonbProperty("AccountNumber")
	private String accountNumber;

	@JsonbProperty("AccountName")
	private String accountName;

	@JsonbProperty("ChequeCurrency")
	private String chequeCurrency;

	@JsonbProperty("ChequeNumber")
	private String chequeNumber;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("ChequeAmount")
	private double chequeAmount;

	@JsonbProperty("ChequeDate")
	private String chequeDate;

	@JsonbProperty("ChequeReceivedDate")
	private String chequeReceivedDate;

	@JsonbProperty("ChequePaidDate")
	private String chequePaidDate;

	@JsonbProperty("ChequeStatus")
	private String chequeStatus;

	@JsonbProperty("ChequePayeeName")
	private String chequePayeeName;

	@JsonbProperty("SeqNo")
	private String seqNo;

	@JsonbProperty("ChequeType")
	private String chequeType;


	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getChequeCurrency() {
		return chequeCurrency;
	}

	public void setChequeCurrency(String chequeCurrency) {
		this.chequeCurrency = chequeCurrency;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	public double getChequeAmount() {
		return chequeAmount;
	}

	public void setChequeAmount(double chequeAmount) {
		this.chequeAmount = chequeAmount;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getChequeReceivedDate() {
		return chequeReceivedDate;
	}

	public void setChequeReceivedDate(String chequeReceivedDate) {
		this.chequeReceivedDate = chequeReceivedDate;
	}

	public String getChequePaidDate() {
		return chequePaidDate;
	}

	public void setChequePaidDate(String chequePaidDate) {
		this.chequePaidDate = chequePaidDate;
	}

	public String getChequeStatus() {
		return chequeStatus;
	}

	public void setChequeStatus(String chequeStatus) {
		this.chequeStatus = chequeStatus;
	}

	public String getChequePayeeName() {
		return chequePayeeName;
	}

	public void setChequePayeeName(String chequePayeeName) {
		this.chequePayeeName = chequePayeeName;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getChequeType() {
		return chequeType;
	}

	public void setChequeType(String chequeType) {
		this.chequeType = chequeType;
	}

	public CustomerChequeStatusObject() {
		this.setAccountNumber(accountNumber);
		this.setAccountName(accountName);
		this.setChequeCurrency(chequeCurrency);
		this.setChequeNumber(chequeNumber);
		this.setChequeAmount(chequeAmount);
		this.setChequeDate(chequeDate);
		this.setChequeReceivedDate(chequeReceivedDate);
		this.setChequePaidDate(chequePaidDate);
		this.setChequeStatus(chequeStatus);
		this.setChequePayeeName(chequePayeeName);
		this.setSeqNo(seqNo);
		this.setChequeType(chequeType);
	}
	
	
	
	
	
}
