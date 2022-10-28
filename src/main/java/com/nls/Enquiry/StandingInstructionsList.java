package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode", "errorDesc", "unitId", "referenceNum", "accNo","instructionsList" })
public class StandingInstructionsList {

	@JsonbProperty("UnitID")
	private String unitId;

	@JsonbProperty("ReferenceNumber")
	public String referenceNum;
	
	@JsonbProperty("AccountNumber")
	public String accNo;

	@JsonbProperty("StandingInstructionsList")
	private List<StandingInstructionsObject> instructionsList;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	public StandingInstructionsList() {
		instructionsList = new ArrayList<StandingInstructionsObject>();
	}

	public void addAccount(StandingInstructionsObject object) {
		this.instructionsList.add(object);
	}

	public List<StandingInstructionsObject> getinstructionsList() {
		return instructionsList;
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

	public List<StandingInstructionsObject> getInstructionsList() {
		return instructionsList;
	}

	public void setInstructionsList(List<StandingInstructionsObject> instructionsList) {
		this.instructionsList = instructionsList;
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

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	
	

}
