package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode", "errorDesc", "unitId", "referenceNum", "customerId", "statementsCount", "accList" })
public class AccountCardEstatementList {

	@JsonbProperty("UnitID")
	public String unitId;

	@JsonbProperty("ReferenceNumber")
	public String referenceNum;

	@JsonbProperty("CustomerId")
	public String customerId;

	@JsonbProperty("StatementsCount")
	public int statementsCount;

	@JsonbProperty("AccountCardEstatementList")
	public List<AccountCardEstatementObject> accList;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	public AccountCardEstatementList() {
		accList = new ArrayList<AccountCardEstatementObject>();
	}

	public void addAccount(AccountCardEstatementObject object) {
		this.accList.add(object);
	}

	public List<AccountCardEstatementObject> getAccList() {
		return accList;
	}

	public void setAccList(List<AccountCardEstatementObject> accList) {
		this.accList = accList;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public int getStatementsCount() {
		return statementsCount;
	}

	public void setStatementsCount(int statementsCount) {
		this.statementsCount = statementsCount;
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

}
