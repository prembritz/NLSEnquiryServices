package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbNillable(true)
@JsonbPropertyOrder({"valDate","transDate","refNo","credAmt","debtAmt",
	"runningBal","crfType","transseqNo","narr","exchangeRate","theirRef",
	"swiftInd","ftsInd","custID","transReference","chequeimgFlg","transCode",
	"description","currency","chequeimgID"})
public class AccountStatementObject 
{
	
	//@JsonbDateFormat("yyyy-MM-dd")
	@JsonbProperty("ValueDate")   
	private String valDate;
	
	//@JsonbDateFormat("yyyy-MM-dd")
	@JsonbProperty("TransactionDate")
	private String transDate;
	
	@JsonbProperty("UniqueTransactionLegNo")
	private String refNo;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("CreditAmount")
	private Double credAmt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DebitAmount")
	private Double debtAmt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("RunningBalance")
	private Double runningBal;
	
	@JsonbProperty("CreditDebitFlag")
	private String crfType;
	
	@JsonbProperty("TransactionSeqNo")
	private Integer transseqNo;
	
	@JsonbProperty("Narrative")
	private String narr;
	
	@JsonbProperty("CurrencyConversionRate")
	private Integer exchangeRate;
	
	@JsonbProperty("AdditionalReference")
	private String theirRef;
	
	@JsonbProperty("SwiftInd")
	private String swiftInd;
	
	@JsonbProperty("FTSInd")
	private String ftsInd;
	
	@JsonbProperty("ConsumerNumber")
	private String custID;
	
	@JsonbProperty("InstrumentReferenceNumber")
	private String transReference;
	
	@JsonbProperty("ChequeImageFlag")
	private String chequeimgFld;
	
	@JsonbProperty("TransactionCode")
	private String transCode;
	
	@JsonbProperty("TransactionCodeDescription")
	private String description;
	
	@JsonbProperty("TransactionCurrency")
	private String currency;
	
	@JsonbProperty("ChequeImageId")
	private String chequeimgID;
	
	
	public AccountStatementObject() {

		this.setTransseqNo(transseqNo);
		this.setRefNo(refNo);
		this.setCustID(custID);
		this.setValDate(valDate);
		this.setTransDate(transDate);
		this.setDebtAmt(debtAmt);
		this.setCredAmt(credAmt);
		this.setRunningBal(runningBal);
		this.setTransCode(transCode);
		this.setDescription(description);
		this.setCurrency(currency);
		this.setCrfType(crfType);
		this.setNarr(narr);
		this.setExchangeRate(exchangeRate);
		this.setTheirRef(theirRef);
		this.setSwiftInd(swiftInd);
		this.setFtsInd(ftsInd);
		this.setTransReference(transReference);
		this.setChequeimgFld(chequeimgFld);
		this.setChequeimgID(chequeimgID);

	}


	public String getValDate() {
		return valDate;
	}


	public void setValDate(String valDate) {
		this.valDate = valDate;
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


	public Double getRunningBal() {
		return runningBal;
	}


	public void setRunningBal(Double runningBal) {
		this.runningBal = runningBal;
	}


	public String getCrfType() {
		return crfType;
	}


	public void setCrfType(String crfType) {
		this.crfType = crfType;
	}


	public Integer getTransseqNo() {
		return transseqNo;
	}


	public void setTransseqNo(Integer transseqNo) {
		this.transseqNo = transseqNo;
	}


	public String getNarr() {
		return narr;
	}


	public void setNarr(String narr) {
		this.narr = narr;
	}


	public Integer getExchangeRate() {
		return exchangeRate;
	}


	public void setExchangeRate(Integer exchangeRate) {
		this.exchangeRate = exchangeRate;
	}


	public String getTheirRef() {
		return theirRef;
	}


	public void setTheirRef(String theirRef) {
		this.theirRef = theirRef;
	}


	public String getSwiftInd() {
		return swiftInd;
	}


	public void setSwiftInd(String swiftInd) {
		this.swiftInd = swiftInd;
	}


	public String getFtsInd() {
		return ftsInd;
	}


	public void setFtsInd(String ftsInd) {
		this.ftsInd = ftsInd;
	}


	public String getCustID() {
		return custID;
	}


	public void setCustID(String custID) {
		this.custID = custID;
	}


	public String getTransReference() {
		return transReference;
	}


	public void setTransReference(String transReference) {
		this.transReference = transReference;
	}


	public String getChequeimgFld() {
		return chequeimgFld;
	}


	public void setChequeimgFld(String chequeimgFld) {
		this.chequeimgFld = chequeimgFld;
	}


	public String getTransCode() {
		return transCode;
	}


	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getChequeimgID() {
		return chequeimgID;
	}


	public void setChequeimgID(String chequeimgID) {
		this.chequeimgID = chequeimgID;
	}
	
}
