package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbNillable(true)
@JsonbPropertyOrder({"creditcardNumber","custName","currency","cardType","cardCategory","creditcardLimit",
	"dueAmt","minDueAmt","dueDate","status","availBalance","expiryDate","ccAccountNumber","nextStmtDate",
	"respErrorCode","respErrorMsg"})
public class CreditCardSummaryObject {
   
@JsonbProperty("CreditCardNumber")
   private String creditcardNumber;
   
   @JsonbProperty("Currency")
   private String currency;
   
   @JsonbProperty("CustomerName")
   private String custName;
   
   @JsonbProperty("CardCategory")
   private String cardCategory;
   
   @JsonbNumberFormat("###0.00")
   @JsonbProperty("CreditCardLimit")
   private Double creditcardLimit;
   
   @JsonbNumberFormat("###0.00")
   @JsonbProperty("DueAmount")
   private Double dueAmt;
   
   @JsonbNumberFormat("###0.00")
   @JsonbProperty("MinDueAmount")
   private String minDueAmt;
   
  // @JsonbDateFormat("yyyy-MM-dd")
   @JsonbProperty("DueDate")
   private String dueDate;
   
   @JsonbProperty("CardType")
   private String cardType;
   
   @JsonbProperty("Status")
   private String status;
   
   @JsonbNumberFormat("###0.00")
   @JsonbProperty("AvailableBalance")
   private Double availBalance;
   
   @JsonbProperty("ExpiryDate")
   private String expiryDate;
   
   @JsonbProperty("CCAccountNumber")
   private String ccAccountNumber;
   
   @JsonbProperty("NextStatementDate")
   private String nextStmtDate;
   
   @JsonbProperty("RespErrorCode")
   private String respErrorCode;
   
   @JsonbProperty("RespErrorMessage")
   private String respErrorMsg;


	public CreditCardSummaryObject() {
		
		this.setCreditcardNumber(creditcardNumber);
		this.setCustName(custName);
		this.setCurrency(currency);
		this.setCardType(cardType);
		this.setCardCategory(cardCategory);
		this.setCreditcardLimit(creditcardLimit);
		this.setDueAmt(dueAmt);
		this.setMinDueAmt(minDueAmt);
		
		this.setDueDate(dueDate);
		this.setStatus(status);
		this.setAvailBalance(availBalance);
		this.setExpiryDate(expiryDate);
		this.setCcAccountNumber(ccAccountNumber);
		this.setNextStmtDate(nextStmtDate);
		this.setRespErrorCode(respErrorCode);
		this.setRespErrorMsg(respErrorMsg);
	}


	public String getCreditcardNumber() {
		return creditcardNumber;
	}


	public void setCreditcardNumber(String creditcardNumber) {
		this.creditcardNumber = creditcardNumber;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getCustName() {
		return custName;
	}


	public void setCustName(String custName) {
		this.custName = custName;
	}


	public String getCardCategory() {
		return cardCategory;
	}


	public void setCardCategory(String cardCategory) {
		this.cardCategory = cardCategory;
	}


	public Double getCreditcardLimit() {
		return creditcardLimit;
	}


	public void setCreditcardLimit(Double creditcardLimit) {
		this.creditcardLimit = creditcardLimit;
	}


	public Double getDueAmt() {
		return dueAmt;
	}


	public void setDueAmt(Double dueAmt) {
		this.dueAmt = dueAmt;
	}


	public String getMinDueAmt() {
		return minDueAmt;
	}


	public void setMinDueAmt(String minDueAmt) {
		this.minDueAmt = minDueAmt;
	}


	public String getDueDate() {
		return dueDate;
	}


	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}


	public String getCardType() {
		return cardType;
	}


	public void setCardType(String cardType) {
		this.cardType = cardType;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Double getAvailBalance() {
		return availBalance;
	}


	public void setAvailBalance(Double availBalance) {
		this.availBalance = availBalance;
	}


	public String getExpiryDate() {
		return expiryDate;
	}


	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}


	public String getCcAccountNumber() {
		return ccAccountNumber;
	}


	public void setCcAccountNumber(String ccAccountNumber) {
		this.ccAccountNumber = ccAccountNumber;
	}


	public String getNextStmtDate() {
		return nextStmtDate;
	}


	public void setNextStmtDate(String nextStmtDate) {
		this.nextStmtDate = nextStmtDate;
	}


	public String getRespErrorCode() {
		return respErrorCode;
	}


	public void setRespErrorCode(String respErrorCode) {
		this.respErrorCode = respErrorCode;
	}


	public String getRespErrorMsg() {
		return respErrorMsg;
	}


	public void setRespErrorMsg(String respErrorMsg) {
		this.respErrorMsg = respErrorMsg;
	}

       
}
