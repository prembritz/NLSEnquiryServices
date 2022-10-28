package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode", "errorDesc", "unitId", "date", "accountNumber", "refNumber",
		"name", "address","postCode","country","primaryOwner","description","owner", 
		"currency", "product", "depositAmount", "effectiveDate",
		"term", "startDate", "maturityDate", "nominalRate","adviseList","totalDue"})
public class DepositAdviseList {

	@JsonbProperty("UnitID")
	private String unitId;

	@JsonbProperty("AccountNumber")
	private String accountNumber;

	@JsonbProperty("ReferenceNumber")
	private String refNumber;

	@JsonbProperty("Currency")
	private String currency;

	@JsonbProperty("Product")
	private String product;
	
	@JsonbProperty("PrimaryOwner")
	private String primaryOwner;

	@JsonbProperty("Description")
	private String description;

	@JsonbProperty("Owner")
	private String owner;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DepositAmount")
	private String depositAmount;

	@JsonbProperty("EffectiveDate")
	private String effectiveDate;

	@JsonbProperty("Term")
	private String term;

	@JsonbProperty("StartDate")
	private String startDate;

	@JsonbProperty("MaturityDate")
	private String maturityDate;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("NominalRate")
	private String nominalRate;

	@JsonbProperty("ReportDate")
	private String date;

	@JsonbProperty("Name")
	private String name;

	@JsonbProperty("Address")
	private String address;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "SUCCESS";
	
	@JsonbProperty("RepaymentDetails")
	private List<DepositAdviseObject> adviseList;
	
	@JsonbProperty("TotalDue")
	private String totalDue;
	
	@JsonbProperty("PostCode")
	private String postCode;
	
	@JsonbProperty("Country")
	private String country;
	
	
	public DepositAdviseList() {
		adviseList = new ArrayList<DepositAdviseObject>();
	}

	public void addAccount(DepositAdviseObject object) {
		this.adviseList.add(object);
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getRefNumber() {
		return refNumber;
	}

	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(String depositAmount) {
		this.depositAmount = depositAmount;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getNominalRate() {
		return nominalRate;
	}

	public void setNominalRate(String nominalRate) {
		this.nominalRate = nominalRate;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public List<DepositAdviseObject> getAdviseList() {
		return adviseList;
	}

	public void setAdviseList(List<DepositAdviseObject> adviseList) {
		this.adviseList = adviseList;
	}

	public String getTotalDue() {
		return totalDue;
	}

	public void setTotalDue(String totalDue) {
		this.totalDue = totalDue;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPrimaryOwner() {
		return primaryOwner;
	}

	public void setPrimaryOwner(String primaryOwner) {
		this.primaryOwner = primaryOwner;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
