package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({"errCode","errorDesc","unitID","accNumber",
	"fromDate","toDate","accName","currCode","openingBal","closingBal","totalTxn","accList"})
public class AccountStatementList
{
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
    @JsonbProperty("ErrorMessage")
   public String errorDesc = "success";
    
    @JsonbProperty("UnitID")
    public String unitID;   
    
    @JsonbProperty("AccountNumber")
    public String accNumber;
    
   //@JsonbDateFormat("yyyy-MM-dd")
    @JsonbProperty("StartDate")
    public String fromDate;
    
   // @JsonbDateFormat("yyyy-MM-dd")
    @JsonbProperty("EndDate")
    public String toDate;
    
    @JsonbProperty("AccountName")
    public String accName;
    
    @JsonbProperty("CurrencyCode")
    public String currCode;
    
    @JsonbNumberFormat("###0.00")
    @JsonbProperty("OpeningBalance")
    public Double openingBal;
    
    @JsonbNumberFormat("###0.00")
    @JsonbProperty("ClosingBalance")
    public Double closingBal;

	@JsonbProperty("TotalTxn")
    public Integer totalTxn;
    
   @JsonbProperty("AccountList")
	public List<AccountStatementObject> accList;

	public AccountStatementList() {
		accList = new ArrayList<AccountStatementObject>();
	}
	
	public void addAccount(AccountStatementObject object) {
		this.accList.add(object);
	}
}
