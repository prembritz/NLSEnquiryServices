package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbNillable(true)
@JsonbPropertyOrder({ "errCode","errorDesc","unId","cardAccnumber","currency","embossingName","cardType","cardAccLimit",
	"totDueAmount","minDueAmt","paymentDueDate","cardAccStatus","cardAccAvailableBal","cardExpiryDate","maskedCardNumber",
	"overDueAmount","utilizedAmt","nextStmtDate" })
public class CreditCardDetailsObject 
{
	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
	@JsonbProperty("ErrorDescription")
	public String errorDesc = "success";
	
	@JsonbProperty("UnitID")
	public String unId;
	
	@JsonbProperty("CardAccountNumber")
	private String cardAccnumber;
	
	@JsonbProperty("Currency")
	private String currency;
	
	@JsonbProperty("EmbossingName")
	private String embossingName;
	
	@JsonbProperty("CardType")
	private String cardType;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("CardAccountLimit")
	private Double cardAccLimit;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("TotalDueAmount")
	private Double totDueAmount;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("MinDueAmount")
	private Double minDueAmt;
	
	@JsonbProperty("PaymentDueDate")
	private String paymentDueDate;

	@JsonbProperty("CardAccountStatus")
	private String cardAccStatus;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("CardAccountAvailableBalance")
	private Double cardAccAvailableBal;
	
	@JsonbProperty("CardExpiryDate")
	private String cardExpiryDate;
	
	//@JsonbDateFormat("yyyy-MM-dd")
	@JsonbProperty("MaskedCardNumber")
	private String maskedCardNumber;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("OverDueAmount")
	private String overDueAmount;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("UtilizedAmount")
	private Double utilizedAmt;

	@JsonbProperty("NextStatementDate")
	private String nextStmtDate;
	
	public CreditCardDetailsObject() {

		this.setErrCode(errCode);
		this.setErrorDesc(errorDesc);
		this.setUnId(unId);
		this.setCardAccnumber(cardAccnumber);
		this.setCurrency(currency);
		this.setEmbossingName(embossingName);
		this.setCardType(cardType);
		this.setCardAccLimit(cardAccLimit);
		this.setTotDueAmount(totDueAmount);
		this.setMinDueAmt(minDueAmt);
		this.setPaymentDueDate(paymentDueDate);
		this.setCardAccStatus(cardAccStatus);
		this.setCardAccAvailableBal(cardAccAvailableBal);
		this.setCardExpiryDate(cardExpiryDate);
		this.setMaskedCardNumber(maskedCardNumber);
		this.setOverDueAmount(overDueAmount);
		this.setUtilizedAmt(utilizedAmt);		
		this.setNextStmtDate(nextStmtDate);
	}

	public ERROR_CODE getErrCode() {
		return errCode;
	}

	public void setErrCode(ERROR_CODE errCode) {
		this.errCode = errCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getUnId() {
		return unId;
	}

	public void setUnId(String unId) {
		this.unId = unId;
	}

	public String getCardAccnumber() {
		return cardAccnumber;
	}

	public void setCardAccnumber(String cardAccnumber) {
		this.cardAccnumber = cardAccnumber;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getEmbossingName() {
		return embossingName;
	}

	public void setEmbossingName(String embossingName) {
		this.embossingName = embossingName;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public Double getCardAccLimit() {
		return cardAccLimit;
	}

	public void setCardAccLimit(Double cardAccLimit) {
		this.cardAccLimit = cardAccLimit;
	}

	public Double getTotDueAmount() {
		return totDueAmount;
	}

	public void setTotDueAmount(Double totDueAmount) {
		this.totDueAmount = totDueAmount;
	}

	public Double getMinDueAmt() {
		return minDueAmt;
	}

	public void setMinDueAmt(Double minDueAmt) {
		this.minDueAmt = minDueAmt;
	}

	public String getPaymentDueDate() {
		return paymentDueDate;
	}

	public void setPaymentDueDate(String paymentDueDate) {
		this.paymentDueDate = paymentDueDate;
	}

	public String getCardAccStatus() {
		return cardAccStatus;
	}

	public void setCardAccStatus(String cardAccStatus) {
		this.cardAccStatus = cardAccStatus;
	}

	public Double getCardAccAvailableBal() {
		return cardAccAvailableBal;
	}

	public void setCardAccAvailableBal(Double cardAccAvailableBal) {
		this.cardAccAvailableBal = cardAccAvailableBal;
	}

	public String getCardExpiryDate() {
		return cardExpiryDate;
	}

	public void setCardExpiryDate(String cardExpiryDate) {
		this.cardExpiryDate = cardExpiryDate;
	}

	public String getMaskedCardNumber() {
		return maskedCardNumber;
	}

	public void setMaskedCardNumber(String maskedCardNumber) {
		this.maskedCardNumber = maskedCardNumber;
	}

	public String getOverDueAmount() {
		return overDueAmount;
	}

	public void setOverDueAmount(String overDueAmount) {
		this.overDueAmount = overDueAmount;
	}

	public Double getUtilizedAmt() {
		return utilizedAmt;
	}

	public void setUtilizedAmt(Double utilizedAmt) {
		this.utilizedAmt = utilizedAmt;
	}

	public String getNextStmtDate() {
		return nextStmtDate;
	}

	public void setNextStmtDate(String nextStmtDate) {
		this.nextStmtDate = nextStmtDate;
	}

 
}

