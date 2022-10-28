package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

@JsonbPropertyOrder({"proCode","unId","accountNo"})
@Schema(name="AccountDetailsRequest",description="Request object with Unit Id and Account Number")
public class AccountDetailsRequest {

	@JsonbProperty("ProcCode")
	@Schema(required = false,example="PRDT001" , description = "Product Code")
	public String proCode;
	
	@Schema(required = true,example="KE0010001" , description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unId;
	
	@Schema(required = true,example="7519350046" , description = "Account Number")
	@JsonbProperty("AccountNo")
	public String accountNo;

}


