package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="CreditCardDetailsRequest",description="Request object with Unit Id, CC Account Number")
@JsonbPropertyOrder({"proCode","unId","ccAccountNumber"})
public class CreditCardDetailsRequest {

	@Schema(required =true , example="PRDT001" , description = "Product Code")
	@JsonbProperty("ProductCode")
	public String proCode;
	
	@Schema(required =true , example="KE0010001" , description = "Unit ID")
	@JsonbProperty("UnitID")
	public String unId;
	
	@Schema(required =true , example="1711630011" , description = "CC Account Number")
	@JsonbProperty("CreditCardAccountNumber")
	public String ccAccountNumber;

}


