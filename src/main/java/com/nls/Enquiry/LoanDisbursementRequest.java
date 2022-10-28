package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;


@Schema(name="LoanDisbursementRequest",description="Request object with Unit Id,Account Number")
@JsonbPropertyOrder({"proCode","unId","loanAccnumber"})
public class LoanDisbursementRequest {
	
	
	@Schema(required =false , example="PRDT003" , description = "Product Code")
	@JsonbProperty("ProcCode")
	public String proCode;
	
	@Schema(required =true , example="KE0010001" , description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unId;
	
	@Schema(required =true , example="7519350046" , description = "Account Number")
	@JsonbProperty("LoanAccountNo")
	public String loanAccnumber;
}
