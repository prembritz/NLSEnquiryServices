package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({"transDate","refNo","narr","debtAmt",
	"credAmt","bal","transSeqno","crfType","transCode"})
public class LoanStatementObject 
{

	@JsonbProperty("TransactionDate")
	private String transDate;
	
	@JsonbProperty("TransactionDescription")
	private String transCode;
	
	@JsonbProperty("ReferenceNumber")
	private String refNo;
	
	@JsonbProperty("CreditDebitFlag")
	private String crfType;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("CreditAmount")
	private Double credAmt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DebitAmount")
	private Double debtAmt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("Balance")
	private Double bal;
	
	@JsonbProperty("TransactionSeqNo")
	private Integer transSeqno;
	
	@JsonbProperty("Narrative")
	private String narr;
	
	public LoanStatementObject() {

		this.setTransSeqno(transSeqno);
		this.setRefNo(refNo);
		this.setTransCode(transCode);
		this.setTransDate(transDate);
		this.setDebtAmt(debtAmt);
		this.setCredAmt(credAmt);
		this.setBal(bal);
		this.setNarr(narr);
	}


	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}


	public String getTransDate() {
		return transDate;
	}



	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}



	public String getRefNo() {
		return refNo;
	}



	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}


	public String getCrfType() {
		return crfType;
	}


	public void setCrfType(String crfType) {
		this.crfType = crfType;
	}


	public Double getCredAmt() {
		return credAmt;
	}



	public void setCredAmt(Double credAmt) {
		this.credAmt = credAmt;
	}



	public Double getDebtAmt() {
		return debtAmt;
	}



	public void setDebtAmt(Double debtAmt) {
		this.debtAmt = debtAmt;
	}



	public Double getBal() {
		return bal;
	}



	public void setBal(Double bal) {
		this.bal = bal;
	}



	public Integer getTransSeqno() {
		return transSeqno;
	}



	public void setTransSeqno(Integer transSeqno) {
		this.transSeqno = transSeqno;
	}



	public String getNarr() {
		return narr;
	}



	public void setNarr(String narr) {
		this.narr = narr;
	}

  	
}
