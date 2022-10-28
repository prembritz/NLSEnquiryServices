package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({"errCode","errorDesc","unId","lnList"})
public class LoanSummaryList
{
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
   @JsonbProperty("ErrorMessage")
   private String errorDesc = "success";
   
   @JsonbProperty("LoanList")
	private List<LoanSummaryObject> lnList;

   @JsonbProperty("UnitID")
	private String unId;
	
	public LoanSummaryList() {
		lnList = new ArrayList<LoanSummaryObject>();
	}
	
	public void addLoanAccounts(LoanSummaryObject object) {
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

	public List<LoanSummaryObject> getLnList() {
		return lnList;
	}

	public void setLnList(List<LoanSummaryObject> lnList) {
		this.lnList = lnList;
	}
	


}
