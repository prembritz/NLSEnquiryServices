package com.nls.Enquiry;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="TransactionStatusRequest",description="Request object with Unit Id and Transaction Id")
@JsonbPropertyOrder({"unId","transData"})
public class TransactionStatusRequest {
	
	public static class AccountData{
	
		@Schema(required =true , example="20208ABC" , description = "Transaction Id")
		@JsonbProperty("TransactionId")
		public String transactionId;
		
	}

	@Schema(required =true , example="KE0010001" , description = "Unit ID")
	@JsonbProperty("UnitID")
	public String unId;
	
	@JsonbProperty("TransactionData")
	public List<AccountData> transData;
	
	
	
}



