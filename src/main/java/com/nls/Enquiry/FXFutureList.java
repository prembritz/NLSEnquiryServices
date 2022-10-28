package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode","errorDesc","referenceNum", "unitId","accList"})
public class FXFutureList {

	@JsonbProperty("FXFutureList")
	private List<FXFutureObject> accList;
	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";
	
	@JsonbProperty("ReferenceNumber")
	public String referenceNum;

	@JsonbProperty("UnitID")
	public String unitId;


	public FXFutureList() {
		accList = new ArrayList<FXFutureObject>();
	}

	public void addAccount(FXFutureObject object) {
		this.accList.add(object);
	}

	public List<FXFutureObject> getAccList() {
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

	public void setAccList(List<FXFutureObject> accList) {
		this.accList = accList;
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

}
