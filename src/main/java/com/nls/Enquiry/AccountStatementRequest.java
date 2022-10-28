package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="AccountStatementRequest",description="Request object with Unit Id and Account Number & Dates")
@JsonbPropertyOrder({"proCode","unId","accNumber","accType","fromDate","toDate"})
public class AccountStatementRequest {

	@Schema(required =false , example="PRDT003" , description = "Product Code")
	@JsonbProperty("ProcCode")
	public String proCode;
	
	@Schema(required =true , example="KE0010001" , description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unId;
	
	@Schema(required =true , example="7519350046" , description = "Account Number")
	@JsonbProperty("AccountNo")
	public String accNumber;
	
	@Schema(required =true , example="2" , description = "Account Type", title="Account Types are DEPOSITS,ACCOUNTS,LENDING", enumeration = {"2","1","3"})
	@JsonbProperty("AccountType")
	public String accType;

	//@JsonbDateFormat("yyyy-MM-dd")
	@Schema(required =true , example="26052022" , description = "Start Date")
	@JsonbProperty("FromDate")
	public String fromDate;
	
	//@JsonbDateFormat("yyyy-MM-dd")
	@Schema(required =true , example="05062022" , description = "End Date")
	@JsonbProperty("ToDate")   
	public String toDate;  
}
