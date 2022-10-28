package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({"errCode","errorDesc","unId","loanAccnumber","lnList"})
public class LoanRepaymentList
{
	LoanRepaymentRequest id;
	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
    @JsonbProperty("ErrorMessage")
   private String errorDesc = "success";
    
    @JsonbProperty("UnitID")
    private String unId;  
    
    @JsonbProperty("LoanAccountNumber")
    private String loanAccnumber;
    
   @JsonbProperty("LoanAccountList")
	private List<LoanRepaymentObject> lnList;
   
	
	public LoanRepaymentList() {
		lnList = new ArrayList<LoanRepaymentObject>();
	}
	
	public void addAccount(LoanRepaymentObject object) {
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

	public String getUnId() {
		return unId;
	}

	public void setUnId(String unId) {
		this.unId = unId;
	}

	public String getLoanAccnumber() {
		return loanAccnumber;
	}

	public void setLoanAccnumber(String loanAccnumber) {
		this.loanAccnumber = loanAccnumber;
	}

	public List<LoanRepaymentObject> getLnList() {
		return lnList;
	}

	public void setLnList(List<LoanRepaymentObject> lnList) {
		this.lnList = lnList;
	}

	
}
