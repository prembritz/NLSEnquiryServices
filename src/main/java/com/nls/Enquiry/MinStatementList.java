package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({"errCode","errorDesc","unitID","accNumber","stmtList"})
public class MinStatementList
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
       
   @JsonbProperty("MiniStatementList")
	public List<MiniStatementObject> stmtList;

	public MinStatementList() {
		stmtList = new ArrayList<MiniStatementObject>();
	}
	
	public void addAccount(MiniStatementObject object) {
		this.stmtList.add(object);
	}
}
