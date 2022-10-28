package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({"errCode","errorDesc","unID","loanAccno","lnList"})
public class LoanDisbursementList
{
	LoanDisbursementRequest id=new LoanDisbursementRequest();
	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
    @JsonbProperty("ErrorMessage")
   private String errorDesc = "success";
    
    @JsonbProperty("UnitID")
    private String unID; 
    
    @JsonbProperty("LoanAccountNumber")
    private String loanAccno;
    
   @JsonbProperty("LoanAccountList")
	private List<LoanDisbursementObject> lnList;
   
	
	public LoanDisbursementList() {
		lnList = new ArrayList<LoanDisbursementObject>();
	}
	
	public void addAccount(LoanDisbursementObject object) {
		this.lnList.add(object);
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

	public String getUnID() {
		return unID;
	}

	public void setUnID(String unID) {
		this.unID = unID;
	}

	public String getLoanAccno() {
		return loanAccno;
	}

	public void setLoanAccno(String loanAccno) {
		this.loanAccno = loanAccno;
	}

	public List<LoanDisbursementObject> getLnList() {
		return lnList;
	}

	public void setLnList(List<LoanDisbursementObject> lnList) {
		this.lnList = lnList;
	}

	
}
