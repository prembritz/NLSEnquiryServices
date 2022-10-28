package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode", "errorDesc", "unitId", "date", "accountNumber", "refNumber", "name", "address",
		"postCode", "country", "primaryOwner", "description", "owner", "currency", "product", "depositAmount",
		"effectiveDate", "term", "startDate", "maturityDate", "nominalRate", "pridueDate", "pridueType", "pridueAmount",
		"depodueDate", "depodueType", "depodueAmount", "watxdueDate", "watxdueType", "watxdueAmount", "totalDue" })
public class DepositAdviseObject {

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

	@JsonbProperty("PrincipalDueDate")
	private String pridueDate;

	@JsonbProperty("PrincipalDueType")
	private String pridueType;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("PrincipalDueAmount")
	private String pridueAmount;

	@JsonbProperty("DepositDueDate")
	private String depodueDate;

	@JsonbProperty("DepositDueType")
	private String depodueType;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DepositDueAmount")
	private String depodueAmount;

	@JsonbProperty("WtaxDueDate")
	private String watxdueDate;

	@JsonbProperty("WtaxDueType")
	private String watxdueType;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("WtaxDueAmount")
	private String watxdueAmount;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "SUCCESS";

	@JsonbProperty("TotalDue")
	private String totalDue;

	@JsonbProperty("PostCode")
	private String postCode;

	@JsonbProperty("Country")
	private String country;

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

	public String getPridueDate() {
		return pridueDate;
	}

	public void setPridueDate(String pridueDate) {
		this.pridueDate = pridueDate;
	}

	public String getPridueType() {
		return pridueType;
	}

	public void setPridueType(String predueType) {
		this.pridueType = predueType;
	}

	public String getPridueAmount() {
		return pridueAmount;
	}

	public void setPridueAmount(String predueAmount) {
		this.pridueAmount = predueAmount;
	}

	public String getDepodueDate() {
		return depodueDate;
	}

	public void setDepodueDate(String depodueDate) {
		this.depodueDate = depodueDate;
	}

	public String getDepodueType() {
		return depodueType;
	}

	public void setDepodueType(String depodueType) {
		this.depodueType = depodueType;
	}

	public String getDepodueAmount() {
		return depodueAmount;
	}

	public void setDepodueAmount(String depodueAmount) {
		this.depodueAmount = depodueAmount;
	}

	public String getWatxdueDate() {
		return watxdueDate;
	}

	public void setWatxdueDate(String watxdueDate) {
		this.watxdueDate = watxdueDate;
	}

	public String getWatxdueType() {
		return watxdueType;
	}

	public void setWatxdueType(String watxdueType) {
		this.watxdueType = watxdueType;
	}

	public String getWatxdueAmount() {
		return watxdueAmount;
	}

	public void setWatxdueAmount(String watxdueAmount) {
		this.watxdueAmount = watxdueAmount;
	}

	public DepositAdviseObject() {
		this.setUnitId(unitId);
		this.setAccountNumber(accountNumber);
		this.setRefNumber(refNumber);
		this.setCurrency(currency);
		this.setProduct(product);
		this.setPrimaryOwner(primaryOwner);
		this.setDescription(description);
		this.setOwner(owner);
		this.setDepositAmount(depositAmount);
		this.setEffectiveDate(effectiveDate);
		this.setTerm(term);
		this.setStartDate(startDate);
		this.setMaturityDate(maturityDate);
		this.setNominalRate(nominalRate);
		this.setDate(date);
		this.setName(name);
		this.setAddress(address);
		this.setPridueDate(pridueDate);
		this.setPridueType(pridueType);
		this.setPridueAmount(pridueAmount);
		this.setDepodueDate(depodueDate);
		this.setDepodueType(depodueType);
		this.setDepodueAmount(depodueAmount);
		this.setWatxdueDate(watxdueDate);
		this.setWatxdueType(watxdueType);
		this.setWatxdueAmount(watxdueAmount);
		this.setErrCode(errCode);
		this.setErrorDesc(errorDesc);
		this.setTotalDue(totalDue);
		this.setPostCode(postCode);
		this.setCountry(country);
	}

}
