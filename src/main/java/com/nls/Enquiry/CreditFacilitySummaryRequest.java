package com.nls.Enquiry;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "procode", "creditData" })
@Schema(name = "CreditFacilitySummaryRequest", description = "Request object with Unit Id,Prodcode & CIF")
public class CreditFacilitySummaryRequest {

	@JsonbProperty("ProCode")
	@Schema(required = false, example = "AAA4343434", description = "Product Code")
	public String procode;
	
	public static class CreditFacility{
		@Schema(required =true , example="KE0010001" , description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
		@JsonbProperty("UnitID")
		public String unitId;
		
		@Schema(required =true , example="15454" , description = "Customer Id")
		@JsonbProperty("CIF")
		public String cif;
		
	}
	
	@JsonbProperty("CreditFaclitySummaryData")
	public List<CreditFacility> creditData;
	
	

}
