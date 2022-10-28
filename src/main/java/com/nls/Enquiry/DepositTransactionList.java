package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode","errorDesc","unitId","accNo","accList"})
public class DepositTransactionList {

	@JsonbProperty("DepositTransactionList")
	private List<DepositTransactionObject> accList;
	
	@JsonbProperty("UnitID")
	private String unitId;
	
	@JsonbProperty("AccountNumber")
	private String accNo;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public DepositTransactionList() {
		accList = new ArrayList<DepositTransactionObject>();
	}

	public void addAccount(DepositTransactionObject object) {
		this.accList.add(object);
	}

	public List<DepositTransactionObject> getAccList() {
		return accList;
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

	public void setAccList(List<DepositTransactionObject> accList) {
		this.accList = accList;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	
	
	
}
