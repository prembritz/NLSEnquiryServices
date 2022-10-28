package com.nls.Enquiry;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="CustomerOnBoardingStatusRequest",description="Request object with Unit Id,Account Number and CIF")
@JsonbPropertyOrder({"proCode","unId","cif","accts"})
public class CustomerOnBoardingStatusRequest {

   public static class Accounts
   
   {  
	   @Schema(required =true , example="1711460022" , description = "Account Number")
        @JsonbProperty("AccountNo")	
        public String accNumber;
   
	   @Schema(required =true , example="KE0010001" , description = "Unit Id", enumeration = {"KE0010001","TZ0010001","UG0010001"})
		@JsonbProperty("UnitID")
		public String unitId;
   
	}

	@Schema(required =false , example="PRDT004" , description = "Product Code")
	@JsonbProperty("ProCode")
	public String proCode;
	
	@Schema(required =true , example="KE0010001" , description = "Unit Id", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unId;
	
	@Schema(required =true , example="172234" , description = "Customer Number")
	@JsonbProperty("CIF")
	public String cif;
	
	@JsonbProperty("Accounts")
	public List<Accounts> accts;

}


