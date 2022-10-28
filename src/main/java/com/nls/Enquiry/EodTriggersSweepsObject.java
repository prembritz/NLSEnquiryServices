package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode", "errorDesc", "unitId", "processId", "referenceNumber", "eodStage", "status",
		"systemdatetime" })
public class EodTriggersSweepsObject {

	@JsonbProperty("UnitID")
	private String unitId;

	@JsonbProperty("ProcessID")
	private String processId;

	@JsonbProperty("ReferenceNumber")
	private String referenceNumber;

	@JsonbProperty("EODStage")
	private String eodStage;

	@JsonbProperty("SystemDateTime")
	private String systemDateTime;

	@JsonbProperty("Status")
	private ERROR_CODE status;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	public EodTriggersSweepsObject() {
		this.setUnitId(unitId);
		this.setProcessId(processId);
		this.setReferenceNumber(referenceNumber);
		this.setEodStage(eodStage);
		this.setSystemDateTime(systemDateTime);
		this.setStatus(status);
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getEodStage() {
		return eodStage;
	}

	public void setEodStage(String eodStage) {
		this.eodStage = eodStage;
	}

	public String getSystemDateTime() {
		return systemDateTime;
	}

	public void setSystemDateTime(String systemDateTime) {
		this.systemDateTime = systemDateTime;
	}

	public ERROR_CODE getStatus() {
		return status;
	}

	public void setStatus(ERROR_CODE successful) {
		this.status = successful;
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
