package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode", "errorDesc", "unitId", "cif","ref","faciltyList" })
public class CreditFacilitySummaryList {
	
	@JsonbProperty("UnitID")
	private String unitId;

	@JsonbProperty("CIF")
	private String cif;

	@JsonbProperty("ReferenceNumber")
	private String ref;

	@JsonbProperty("CreditFacilitySummaryList")
	private List<CreditFacilitySummaryObject> faciltyList;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	public CreditFacilitySummaryList() {
		faciltyList = new ArrayList<CreditFacilitySummaryObject>();
	}

	public void addAccount(CreditFacilitySummaryObject object) {
		this.faciltyList.add(object);
	}

	public List<CreditFacilitySummaryObject> getfaciltyList() {
		return faciltyList;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public List<CreditFacilitySummaryObject> getFaciltyList() {
		return faciltyList;
	}

	public void setFaciltyList(List<CreditFacilitySummaryObject> faciltyList) {
		this.faciltyList = faciltyList;
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

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

}
