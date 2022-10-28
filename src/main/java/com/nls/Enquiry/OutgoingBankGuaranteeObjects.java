package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbNillable(true)
@JsonbPropertyOrder({ "guaranteeRef", "cbxReference", "guaranteeType", "issueDate", "expiryDate", "benefName", "guaranteeCurrency",
		"guaranteeAmt", "guaranteeStatus", "advisingBank", "transType" })
public class OutgoingBankGuaranteeObjects {

	@JsonbProperty("GuarReferenceNo")
	private String guaranteeRef;

	@JsonbProperty("CBXReferenceNo")
	private String cbxReference;

	@JsonbProperty("GuarType")
	private String guaranteeType;

	@JsonbProperty("IssueDate")
	private String issueDate;

	@JsonbProperty("ExpiryDate")
	private String expiryDate;

	@JsonbProperty("BeneficiaryName")
	private String benefName;

	@JsonbProperty("GuarCurrency")
	private String guaranteeCurrency;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("GuarAmount")
	private Double guaranteeAmt;

	@JsonbProperty("GuarStatus")
	private String guaranteeStatus;

	@JsonbProperty("AdvisingBank")
	private String advisingBank;

	@JsonbProperty("TranType")
	private String transType;

	public OutgoingBankGuaranteeObjects() {

		this.setGuaranteeRef(guaranteeRef);
		this.setCbxReference(cbxReference);
		this.setGuaranteeType(guaranteeType);
		this.setIssueDate(issueDate);
		this.setExpiryDate(expiryDate);
		this.setBenefName(benefName);
		this.setGuaranteeCurrency(guaranteeCurrency);
		this.setGuaranteeAmt(guaranteeAmt);
		this.setGuaranteeStatus(guaranteeStatus);
		this.setAdvisingBank(advisingBank);
		this.setTransType(transType);

	}

	public String getGuaranteeRef() {
		return guaranteeRef;
	}

	public void setGuaranteeRef(String guaranteeRef) {
		this.guaranteeRef = guaranteeRef;
	}

	public String getCbxReference() {
		return cbxReference;
	}

	public void setCbxReference(String cbxReference) {
		this.cbxReference = cbxReference;
	}

	public String getGuaranteeType() {
		return guaranteeType;
	}

	public void setGuaranteeType(String guaranteeType) {
		this.guaranteeType = guaranteeType;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getBenefName() {
		return benefName;
	}

	public void setBenefName(String benefName) {
		this.benefName = benefName;
	}

	public String getGuaranteeCurrency() {
		return guaranteeCurrency;
	}

	public void setGuaranteeCurrency(String guaranteeCurrency) {
		this.guaranteeCurrency = guaranteeCurrency;
	}

	public Double getGuaranteeAmt() {
		return guaranteeAmt;
	}

	public void setGuaranteeAmt(Double guaranteeAmt) {
		this.guaranteeAmt = guaranteeAmt;
	}

	public String getGuaranteeStatus() {
		return guaranteeStatus;
	}

	public void setGuaranteeStatus(String guaranteeStatus) {
		this.guaranteeStatus = guaranteeStatus;
	}

	public String getAdvisingBank() {
		return advisingBank;
	}

	public void setAdvisingBank(String advisingBank) {
		this.advisingBank = advisingBank;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	
}
