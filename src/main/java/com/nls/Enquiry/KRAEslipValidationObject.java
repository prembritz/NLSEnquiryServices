package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errorCode", "errorMessage", "status","eslipNumber","paymentAdvicedate", "taxPayerPin", "totalAmount",
	"currency","taxCode","taxHead","taxComponent","amountPerTax","taxPeriod"})
public class KRAEslipValidationObject {
	
	@JsonbProperty("EslipNumber")
	public String eslipNumber;
	
	@JsonbProperty("Status")
	public String status;

	@JsonbProperty("TaxpayerFullName")
	public String taxPayerFullname;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errorCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorMessage = "success";;

	@JsonbProperty("PaymentAdviceDate")
	public String paymentAdvicedate;

	@JsonbProperty("TaxpayerPin")
	public String taxPayerPin;

	@JsonbProperty("TotalAmount")
	public String totalAmount;
	
	@JsonbProperty("Currency")
	public String currency;

	@JsonbProperty("TaxCode")
	public String taxCode;

	@JsonbProperty("TaxHead")
	public String taxHead;
	
	@JsonbProperty("TaxComponent")
	public String taxComponent;

	@JsonbProperty("AmountPerTax")
	public String amountPerTax;

	@JsonbProperty("TaxPeriod")
	public String taxPeriod;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTaxPayerFullname() {
		return taxPayerFullname;
	}

	public void setTaxPayerFullname(String taxPayerFullname) {
		this.taxPayerFullname = taxPayerFullname;
	}

	public ERROR_CODE getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ERROR_CODE errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getPaymentAdvicedate() {
		return paymentAdvicedate;
	}

	public void setPaymentAdvicedate(String paymentAdvicedate) {
		this.paymentAdvicedate = paymentAdvicedate;
	}

	public String getTaxPayerPin() {
		return taxPayerPin;
	}

	public void setTaxPayerPin(String taxPayerPin) {
		this.taxPayerPin = taxPayerPin;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getTaxHead() {
		return taxHead;
	}

	public void setTaxHead(String taxHead) {
		this.taxHead = taxHead;
	}

	public String getTaxComponent() {
		return taxComponent;
	}

	public void setTaxComponent(String taxComponent) {
		this.taxComponent = taxComponent;
	}

	public String getAmountPerTax() {
		return amountPerTax;
	}

	public void setAmountPerTax(String amountPerTax) {
		this.amountPerTax = amountPerTax;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public String getEslipNumber() {
		return eslipNumber;
	}

	public void setEslipNumber(String eslipNumber) {
		this.eslipNumber = eslipNumber;
	}
	
}
