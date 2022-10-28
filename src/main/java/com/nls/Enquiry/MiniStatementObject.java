package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbNillable(true)
@JsonbPropertyOrder({"valDate","transCode","transAmt","crfType","currency",
	"transReference","narr"})
public class MiniStatementObject 
{
	
	@JsonbProperty("ValueDate")   
	private String valDate;
	
	@JsonbProperty("TransactionCode")
	private String transCode;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("TransactionAmount")
	private Double transAmt;
	
	@JsonbProperty("CreditDebitFlag")
	private String crfType;
	
	@JsonbProperty("TransactionCurrency")
	private String currency;
	
	@JsonbProperty("TransactionReference")
	private String transReference;
	
	@JsonbProperty("Narrative")
	private String narr;

	public String getValDate() {
		return valDate;
	}

	public void setValDate(String valDate) {
		this.valDate = valDate;
	}

	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	public Double getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(Double transAmt) {
		this.transAmt = transAmt;
	}

	public String getCrfType() {
		return crfType;
	}

	public void setCrfType(String crfType) {
		this.crfType = crfType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTransReference() {
		return transReference;
	}

	public void setTransReference(String transReference) {
		this.transReference = transReference;
	}

	public String getNarr() {
		return narr;
	}

	public void setNarr(String narr) {
		this.narr = narr;
	}
}
