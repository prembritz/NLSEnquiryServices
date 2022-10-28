package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode","errorDesc","unitId","creditList"})
public class CreditCardStatementList {

	
	@JsonbProperty("UnitID")
	private String unitId;
	
	@JsonbProperty("CreditCardStatementList")
	private List<CreditCardStatementObject> creditList;
	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorDescription")
	public String errorDesc = "success";

	public CreditCardStatementList() {
		creditList = new ArrayList<CreditCardStatementObject>();
	}

	public void addAccount(CreditCardStatementObject object) {
		this.creditList.add(object);
	}

	public List<CreditCardStatementObject> getcreditList() {
		return creditList;
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

	public void setcreditList(List<CreditCardStatementObject> creditList) {
		this.creditList = creditList;
	}
	
	public String getUnitid() {
		return unitId;
	}

	public void setUnitid(String unitId) {
		this.unitId = unitId;
	}
	
	

}
