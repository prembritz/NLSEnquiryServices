package com.nls.Enquiry;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "ProCode", "accountValue" })
@Schema(name="AccountValueDateRequest",description="Request object with Unit Id and Account Number & ValueDate")
public class AccountValueDateRequest {

	@Schema(required = false, example = "PRDT002" , description = "ProCode")
	@JsonbProperty("ProcCode")
	public String procCode;

	public static class AccountValue{
		@Schema(required = true, example = "KE0010001", description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
		@JsonbProperty("UnitID")
		public String unitId;

		@Schema(required = true, example = "1711460011", description = "Account Number")
		@JsonbProperty("AccountNo")
		public String accountNo;

		@Schema(required = true, example = "05022022", description = "Value Date")
		@JsonbProperty("ValueDate")
		public String valueDate;
	}
	
	@JsonbProperty("AccountValue")
	public List<AccountValue> accountValue;

}
