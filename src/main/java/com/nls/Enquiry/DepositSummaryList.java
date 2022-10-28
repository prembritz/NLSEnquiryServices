package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode","errorDesc","unitId","accList"})
public class DepositSummaryList {

	@JsonbProperty("UnitID")
	private String unitId;
	
	/*@JsonbProperty("DepositAccountNumber")
	private String depAccNumber;*/
	
	@JsonbProperty("DepositSummaryList")
	private List<DepositSummaryObject> accList;
	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	public DepositSummaryList() {
		accList = new ArrayList<DepositSummaryObject>();
	}

	public void addAccount(DepositSummaryObject object) {
		this.accList.add(object);
	}

	public List<DepositSummaryObject> getAccList() {
		return accList;
	}

	public void setAccList(List<DepositSummaryObject> accList) {
		this.accList = accList;
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

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	/*public String getDepAccNumber() {
		return depAccNumber;
	}

	public void setDepAccNumber(String depAccNumber) {
		this.depAccNumber = depAccNumber;
	}*/
	
}
