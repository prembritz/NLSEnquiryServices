package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "ESlipNumber"})
@Schema(name = "KRAEslipValidationRequest", description = "request object with ESlipNumber")
public class KRAEslipValidationRequest {
	
	@Schema(required = true, example = "83274398212", description = "E-Slip Number")
	@JsonbProperty("ESlipNumber")
	private String eslipNumber;

	public String getEslipNumber() {
		return eslipNumber;
	}

	public void setEslipNumber(String eslipNumber) {
		this.eslipNumber = eslipNumber;
	}

}
