package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbNillable(true)
@JsonbPropertyOrder({ "adviseReference","lcReference", "lcTenor", "lcIssueDate", "lcExpiryDate", "applicantName", "issueBankName", "lcCurrency",
		"lcAmount", "goodsCategory", "confirmInstructions", "txnType", "lcStatus" })
public class ExportLCObjects {

	
	@JsonbProperty("AdviseReferenceNo")
	private String adviseReference;
	
	@JsonbProperty("LCReferenceNo")
	private String lcReference;

	@JsonbProperty("LCTenor")
	private String lcTenor;

	@JsonbProperty("LCIssueDate")
	private String lcIssueDate;

	@JsonbProperty("LCExpiryDate")
	private String lcExpiryDate;

	@JsonbProperty("ApplicantName")
	private String applicantName;

	@JsonbProperty("IssuingBankName")
	private String issueBankName;

	@JsonbProperty("LCCurrency")
	private String lcCurrency;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("LCAmount")
	private Double lcAmount;

	@JsonbProperty("GoodsCategory")
	private String goodsCategory;

	@JsonbProperty("ConfirmationInstructions")
	private String confirmInstructions;

	@JsonbProperty("TxnType")
	private String txnType;
	
	@JsonbProperty("LCStatus")
	private String lcStatus;

	public ExportLCObjects() {

		this.setAdviseReference(adviseReference);
		this.setLcReference(lcReference);
		this.setLcTenor(lcTenor);
		this.setLcIssueDate(lcIssueDate);
		this.setLcExpiryDate(lcExpiryDate);
		this.setApplicantName(applicantName);
		this.setIssueBankName(issueBankName);
		this.setLcCurrency(lcCurrency);
		this.setLcAmount(lcAmount);
		this.setGoodsCategory(goodsCategory);
		this.setConfirmInstructions(confirmInstructions);
		this.setTxnType(txnType);
		this.setLcStatus(lcStatus);

	}

	public String getAdviseReference() {
		return adviseReference;
	}

	public void setAdviseReference(String adviseReference) {
		this.adviseReference = adviseReference;
	}

	public String getLcReference() {
		return lcReference;
	}

	public void setLcReference(String lcReference) {
		this.lcReference = lcReference;
	}

	public String getLcTenor() {
		return lcTenor;
	}

	public void setLcTenor(String lcTenor) {
		this.lcTenor = lcTenor;
	}

	public String getLcIssueDate() {
		return lcIssueDate;
	}

	public void setLcIssueDate(String lcIssueDate) {
		this.lcIssueDate = lcIssueDate;
	}

	public String getLcExpiryDate() {
		return lcExpiryDate;
	}

	public void setLcExpiryDate(String lcExpiryDate) {
		this.lcExpiryDate = lcExpiryDate;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getIssueBankName() {
		return issueBankName;
	}

	public void setIssueBankName(String issueBankName) {
		this.issueBankName = issueBankName;
	}

	public String getLcCurrency() {
		return lcCurrency;
	}

	public void setLcCurrency(String lcCurrency) {
		this.lcCurrency = lcCurrency;
	}

	public Double getLcAmount() {
		return lcAmount;
	}

	public void setLcAmount(Double lcAmount) {
		this.lcAmount = lcAmount;
	}

	public String getGoodsCategory() {
		return goodsCategory;
	}

	public void setGoodsCategory(String goodsCategory) {
		this.goodsCategory = goodsCategory;
	}

	public String getConfirmInstructions() {
		return confirmInstructions;
	}

	public void setConfirmInstructions(String confirmInstructions) {
		this.confirmInstructions = confirmInstructions;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getLcStatus() {
		return lcStatus;
	}

	public void setLcStatus(String lcStatus) {
		this.lcStatus = lcStatus;
	}

	
}
