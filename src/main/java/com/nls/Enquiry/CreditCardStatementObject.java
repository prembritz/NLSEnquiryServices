package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbNillable(true)
@JsonbPropertyOrder({ "transactionProcessingDate", "reference", "transactionAmount", "transactionCode",
		"debitCreditIndicator", "transactionCurrency", "vatAmount" })
public class CreditCardStatementObject {

	@JsonbProperty("TransactionProcessingDate")
	private String transactionProcessingDate;

	@JsonbProperty("Reference")
	private String reference;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("TransactionAmount")
	private double transactionAmount;

	@JsonbProperty("TransactionCode")
	private String transactionCode;

	@JsonbProperty("DebitCreditIndicator")
	private String debitCreditIndicator;

	@JsonbProperty("TransactionCurrency")
	private String transactionCurrency;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("VatAmount")
	private double vatAmount;

	public String getTransactionProcessingDate() {
		return transactionProcessingDate;
	}

	public void setTransactionProcessingDate(String transactionProcessingDate) {
		this.transactionProcessingDate = transactionProcessingDate;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public String getDebitCreditIndicator() {
		return debitCreditIndicator;
	}

	public void setDebitCreditIndicator(String debitCreditIndicator) {
		this.debitCreditIndicator = debitCreditIndicator;
	}

	public String getTransactionCurrency() {
		return transactionCurrency;
	}

	public void setTransactionCurrency(String transactionCurrency) {
		this.transactionCurrency = transactionCurrency;
	}

	public double getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(double vatAmount) {
		this.vatAmount = vatAmount;
	}

	public CreditCardStatementObject() {
		this.setTransactionProcessingDate(transactionProcessingDate);
		this.setReference(reference);
		this.setTransactionAmount(transactionAmount);
		this.setTransactionCode(transactionCode);
		this.setDebitCreditIndicator(debitCreditIndicator);
		this.setTransactionCurrency(transactionCurrency);
		this.setVatAmount(vatAmount);
	}

}
