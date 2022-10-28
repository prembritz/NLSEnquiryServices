package com.nls.Enquiry;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="AccountSummaryRequest",description="Request object with Unit Id and Account Number & Type")
@JsonbPropertyOrder({"proCode","unId","acctData"})
public class AccountSummaryRequest {
	
	public static class AccountData{
	
		@Schema(required =true , example="7519350046" , description = "Account Number")
		@JsonbProperty("AccountNo")
		public String AccNumber;
		
		@Schema(required =true , example="1" , description = "Account Type", title="Account Types are ACCOUNTS,DEPOSITS,LENDING", enumeration = {"1","2","3"})
		@JsonbProperty("AccountType")
		public String AccType;
		
	}

	@Schema(required =false , example="PRDT002" , description = "Product Code")
	@JsonbProperty("ProcCode")
	public String proCode;
	
	@Schema(required =true , example="KE0010001" , description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unId;
	
	@JsonbProperty("AcctData")
	public List<AccountData> acctData;
	
	
	
}



