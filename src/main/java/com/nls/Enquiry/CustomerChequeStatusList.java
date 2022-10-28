package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode", "errorDesc","unitId","accountNumber","ccsList" })
public class CustomerChequeStatusList {

	@JsonbProperty("UnitID")
	private String unitId;

	@JsonbProperty("AccountNumber")
	private String accountNumber; 

	@JsonbProperty("AccountName")
	private String accountName;

	@JsonbProperty("CustomerChequeStatusList")
	private List<CustomerChequeStatusObject> ccsList;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	public CustomerChequeStatusList() {
		ccsList = new ArrayList<CustomerChequeStatusObject>();
	}

	public void addAccount(CustomerChequeStatusObject object) {
		this.ccsList.add(object);
	}

	public List<CustomerChequeStatusObject> getccsList() {
		return ccsList;
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

	public void setccsList(List<CustomerChequeStatusObject> ccsList) {
		this.ccsList = ccsList;
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

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public List<CustomerChequeStatusObject> getCcsList() {
		return ccsList;
	}

	public void setCcsList(List<CustomerChequeStatusObject> ccsList) {
		this.ccsList = ccsList;
	}

}
