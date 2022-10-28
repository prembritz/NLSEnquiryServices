package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "proCode", "unitId","depositAccNo","depositType"})
@Schema(name = "DepositTransactionRequest", description = "Request object with ProCode,Unit Id ,DepositType & Deposit AccountNumber")
public class DepositDetailsRequest {

	@Schema(required = false, example = "PRDT001", description = "Pro Code")
	@JsonbProperty("ProCode")
	public String proCode;

	@Schema(required = true, example = "KE0010001", description = "Unit Id", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitId;

	@Schema(required = true, example = "saving", description = "Deposit Type")
	@JsonbProperty("DepositType")
	public String depositType;

	@Schema(required = true, example = "1711460011", description = "Deposit Account Number")
	@JsonbProperty("DepositAccountNo")
	public String depositAccNo;
}
