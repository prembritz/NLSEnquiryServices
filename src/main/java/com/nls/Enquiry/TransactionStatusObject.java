package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbNillable(true)
@JsonbPropertyOrder({ "erCode", "erMsg", "transactionId","coreReference" })
public class TransactionStatusObject {

	@JsonbProperty("TransactionId")
	private String transactionId;

	@JsonbProperty("CoreReference")
	private String coreReference;
	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE erCode = ERROR_CODE.SUCCESSFUL;
	
	@JsonbProperty("ErrorMessage")
    private String erMsg = "success";
	
	public TransactionStatusObject() {
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getCoreReference() {
		return coreReference;
	}

	public void setCoreReference(String coreReference) {
		this.coreReference = coreReference;
	}

	public ERROR_CODE getErCode() {
		return erCode;
	}

	public void setErCode(ERROR_CODE erCode) {
		this.erCode = erCode;
	}

	public String getErMsg() {
		return erMsg;
	}

	public void setErMsg(String erMsg) {
		this.erMsg = erMsg;
	}

	
}
