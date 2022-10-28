package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;


@JsonbPropertyOrder({ "errCode","errorDesc","unitId","referenceNumber","custId","accList"})
public class ExportLCBillCollectionList {

	@JsonbProperty("UnitId")
	private String unitId;
	
	@JsonbProperty("ReferenceNumber")
	private String referenceNumber;
	
	@JsonbProperty("CustomerId")
	private String custId;
	
	@JsonbProperty("ExportLCBillCollectionList")
	private List<ExportLCBillCollectionObject> accList;
	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorDescription")
	public String errorDesc = "success";

	public ExportLCBillCollectionList() {
		accList = new ArrayList<ExportLCBillCollectionObject>();
	}

	public void addAccount(ExportLCBillCollectionObject object) {
		this.accList.add(object);
	}

	public List<ExportLCBillCollectionObject> getAccList() {
		return accList;
	}

	public void setAccList(List<ExportLCBillCollectionObject> accList) {
		this.accList = accList;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
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
