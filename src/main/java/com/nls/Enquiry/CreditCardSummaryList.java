package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({"errCode","errorDesc","unId","creditcardList"})
public class CreditCardSummaryList
{
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
   @JsonbProperty("ErrorDescription")
   private String errorDesc = "success";
   
   @JsonbProperty("CreditCardList")
	private List<CreditCardSummaryObject> creditcardList;

   @JsonbProperty("UnitID")
	private String unId;
	
	public CreditCardSummaryList() {
		creditcardList = new ArrayList<CreditCardSummaryObject>();
	}
	
	public void addLoanAccounts(CreditCardSummaryObject object) {
		this.creditcardList.add(object);
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

	public List<CreditCardSummaryObject> getCreditcardList() {
		return creditcardList;
	}

	public void setCreditcardList(List<CreditCardSummaryObject> creditcardList) {
		this.creditcardList = creditcardList;
	}
   
}
