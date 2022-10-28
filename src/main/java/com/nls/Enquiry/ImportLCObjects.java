package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbNillable(true)
@JsonbPropertyOrder({ "lcReference", "lcTenor", "lcIssueDate", "lcExpiryDate", "benefName", "adviseBank", "lcCurrency",
		"lcosAmt", "goodsCategory", "txnType", "revolvingLC", "lcStatus" })
public class ImportLCObjects {

	@JsonbProperty("LCReferenceNo")
	private String lcReference;

	@JsonbProperty("LCTenor")
	private String lcTenor;

	@JsonbProperty("LCIssueDate")
	private String lcIssueDate;

	@JsonbProperty("LCExpiryDate")
	private String lcExpiryDate;

	@JsonbProperty("BeneficiaryName")
	private String benefName;

	@JsonbProperty("AdviseBank")
	private String adviseBank;

	@JsonbProperty("LCCurrency")
	private String lcCurrency;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("LCOSAmount")
	private Double lcosAmt;

	@JsonbProperty("GoodsCategory")
	private String goodsCategory;

	@JsonbProperty("TxnType")
	private String txnType;

	@JsonbProperty("RevolvingLC")
	private String revolvingLC;

	@JsonbProperty("LCStatus")
	private String lcStatus;

	public ImportLCObjects() {

		this.setLcReference(lcReference);
		this.setLcTenor(lcTenor);
		this.setLcIssueDate(lcIssueDate);
		this.setLcExpiryDate(lcExpiryDate);
		this.setBenefName(benefName);
		this.setAdviseBank(adviseBank);
		this.setLcCurrency(lcCurrency);
		this.setLcosAmt(lcosAmt);
		this.setGoodsCategory(goodsCategory);
		this.setTxnType(txnType);
		this.setRevolvingLC(revolvingLC);
		this.setLcStatus(lcStatus);

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

	public String getBenefName() {
		return benefName;
	}

	public void setBenefName(String benefName) {
		this.benefName = benefName;
	}

	public String getAdviseBank() {
		return adviseBank;
	}

	public void setAdviseBank(String adviseBank) {
		this.adviseBank = adviseBank;
	}

	public String getLcCurrency() {
		return lcCurrency;
	}

	public void setLcCurrency(String lcCurrency) {
		this.lcCurrency = lcCurrency;
	}

	public Double getLcosAmt() {
		return lcosAmt;
	}

	public void setLcosAmt(Double lcosAmt) {
		this.lcosAmt = lcosAmt;
	}

	public String getGoodsCategory() {
		return goodsCategory;
	}

	public void setGoodsCategory(String goodsCategory) {
		this.goodsCategory = goodsCategory;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getRevolvingLC() {
		return revolvingLC;
	}

	public void setRevolvingLC(String revolvingLC) {
		this.revolvingLC = revolvingLC;
	}

	public String getLcStatus() {
		return lcStatus;
	}

	public void setLcStatus(String lcStatus) {
		this.lcStatus = lcStatus;
	}

}
