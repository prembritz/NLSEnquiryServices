package com.nls.Enquiry;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;


@Schema(name="LoanSummaryRequests",description="Request object with Unit Id,Account Number and CIF")
@JsonbPropertyOrder({"proCode","loanAcctdata"})
public class LoanSummaryRequests {
	
	
	public static class LoanAccountData{
		
		@JsonbProperty("LoanAccountNo")
		@Schema(required =true , example="7519350046" , description = "Account Number")
		public String loanAccnumber;
		
		@JsonbProperty("CIF")
		@Schema(required =true , example="171154" , description = "Customer Id")
		public String cifRef;
		
		@JsonbProperty("UnitID")
		@Schema(required =true , example="KE0010001" , description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
		public String uID;
		
	}
	
	@Schema(required =false , example="PRDT009" , description = "Product Code")
	@JsonbProperty("ProcCode")
	public String proCode;
	
	@JsonbProperty("LoanAccountData")
	public List<LoanAccountData> loanAcctdata;
}
