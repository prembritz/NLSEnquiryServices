package com.nls.Enquiry;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;


@Schema(name="CreditCardSummaryRequest",description="Request object with Unit Id,Account Number")
@JsonbPropertyOrder({"proCode","creditcardAcctdata"})
public class CreditCardSummaryRequest {
	
	
	public static class CreditCardAccountData{
		
		@JsonbProperty("CreditCardNumber")
		@Schema(required =true , example="7897XXXXXXXX5478" , description = "Credit Card Number")
		public String creditcardNumber;
		
		@JsonbProperty("UnitId")
		@Schema(required =true , example="KE0010001" , description = "Unit ID")
		public String uID;
		
	}
	
	@Schema(required =true , example="PRDT009" , description = "Product Code")
	@JsonbProperty("ProductCode")
	public String proCode;
	
	@JsonbProperty("CreditCardAccountData")
	public List<CreditCardAccountData> creditcardAcctdata;
}
