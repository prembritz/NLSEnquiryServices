package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;


@Schema(name="CustomerOnboardingRequest",description="Request object with Unit Id and Customer ID")
@JsonbPropertyOrder({"prodCode","unId","cif"})
public class CustomerOnboardingRequest {

	@Schema(required =false , example="PRDT001" , description = "Product Code")
	@JsonbProperty("ProductCode")
	public String prodCode;
	
	@Schema(required =true , example="KE0010001" , description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unId;
	
	@Schema(required =true , example="171173" , description = "Customer ID")
	@JsonbProperty("CustomerID")
	public String cif;
	
}
