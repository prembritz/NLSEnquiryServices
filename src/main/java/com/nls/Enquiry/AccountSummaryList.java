package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({"errCode","errorDesc","unId","accList"})
public class AccountSummaryList
{
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
    @JsonbProperty("ErrorMessage")
    private String errorDesc = "success";
   
    @JsonbProperty("UnitID")
    private String unId;
    
   @JsonbProperty("AccountList")
	private List<AccountSummaryObject> accList;
	
	public AccountSummaryList() {
		accList = new ArrayList<AccountSummaryObject>();
		
	}
	
	public void addAccount(AccountSummaryObject object) {
		this.accList.add(object);
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

	public List<AccountSummaryObject> getAccList() {
		return accList;
	}

	public void setAccList(List<AccountSummaryObject> accList) {
		this.accList = accList;
	}

	
}
