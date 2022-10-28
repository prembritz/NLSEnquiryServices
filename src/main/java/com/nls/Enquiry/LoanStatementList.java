package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({"errCode","errorDesc","unID","loanAccnumber","openBal","closingBal","lnList"})
public class LoanStatementList
{
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
    @JsonbProperty("ErrorMessage")
   private String errorDesc = "success";
    
    @JsonbProperty("UnitID")
    private String unID;   
    
    @JsonbProperty("LoanAccountNumber")
    private String loanAccnumber;
    
    @JsonbNumberFormat("###0.00")
    @JsonbProperty("OpeningBalance")
    private Double openBal;
    
    @JsonbNumberFormat("###0.00")
    @JsonbProperty("ClosingBalance")
    private Double closingBal;
    
   @JsonbProperty("LoanAccountList")
	private List<LoanStatementObject> lnList;
   
	
	public LoanStatementList() {
		lnList = new ArrayList<LoanStatementObject>();
	}
	
	public void addAccount(LoanStatementObject object) {
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

	public String getLoanAccnumber() {
		return loanAccnumber;
	}

	public void setLoanAccnumber(String loanAccnumber) {
		this.loanAccnumber = loanAccnumber;
	}

	public Double getOpenBal() {
		return openBal;
	}

	public void setOpenBal(Double openBal) {
		this.openBal = openBal;
	}

	
	public Double getClosingBal() {
		return closingBal;
	}

	public void setClosingBal(Double closingBal) {
		this.closingBal = closingBal;
	}

	public List<LoanStatementObject> getLnList() {
		return lnList;
	}

	public void setLnList(List<LoanStatementObject> lnList) {
		this.lnList = lnList;
	}
	
	
}
