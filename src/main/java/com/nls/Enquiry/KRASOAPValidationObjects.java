package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;

import org.w3c.dom.Element;

public class KRASOAPValidationObjects {

	@JsonbProperty("Status")
	public String status;

	@JsonbProperty("TaxpayerFullName")
	public String taxPayerFullname;

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
	
	public KRASOAPValidationObjects(Element resultElem) {
		this.status = SOAPUtility.getTextValue(resultElem, "", "Status");
		System.out.println("status="+status);
        this.taxPayerFullname = SOAPUtility.getTextValue(resultElem, "", "TaxpayerFullName");
        this.paymentAdvicedate = SOAPUtility.getTextValue(resultElem, "", "PaymentAdviceDate");
        this.taxPayerPin = SOAPUtility.getTextValue(resultElem, "", "TaxpayerPin");
        this.totalAmount = SOAPUtility.getTextValue(resultElem, "", "TotalAmount");
        this.currency = SOAPUtility.getTextValue(resultElem, "", "Currency");
        this.taxCode = SOAPUtility.getTextValue(resultElem, "", "TaxCode");
        this.taxHead = SOAPUtility.getTextValue(resultElem, "", "TaxHead");
        this.taxComponent = SOAPUtility.getTextValue(resultElem, "", "TaxComponent");
        this.amountPerTax = SOAPUtility.getTextValue(resultElem, "", "AmountPerTax");
        this.taxPeriod = SOAPUtility.getTextValue(resultElem, "", "TaxPeriod");
	}

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

}
