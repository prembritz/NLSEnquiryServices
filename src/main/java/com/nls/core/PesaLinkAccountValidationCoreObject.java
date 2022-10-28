package com.nls.core;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

import com.nls.Enquiry.ERROR_CODE;

@JsonbPropertyOrder({ "respCode", "responseDesc", "accountId", "bankId", "uniqueId", "accountName",
	"MsgId"})
public class PesaLinkAccountValidationCoreObject {
	
	@JsonbProperty("accountId")
	public String accountId;

	@JsonbProperty("bankId")
	public String bankId;

	@JsonbProperty("responseCode")
	public String responseCode;

	@JsonbProperty("responseDesc")
	public String responseDesc;

	@JsonbProperty("accountName")
	public String accountName;

	@JsonbProperty("MsgId")
	public String MsgId;

	@JsonbProperty("uniqueId")
	public String uniqueId;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getResponseDesc() {
		return responseDesc;
	}

	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getMsgId() {
		return MsgId;
	}

	public void setMsgId(String msgId) {
		MsgId = msgId;
	}
	
}
