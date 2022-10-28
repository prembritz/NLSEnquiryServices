package com.nls.core;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "accountId", "bankId", "uniqueId"})
@Schema(name = "PesaLinkAccountValidationCoreRequest", description = "request object with UniqueId, AccountId")
public class PesaLinkAccountValidationCoreRequest {
	
	@Schema(required = true, example = "1711460011", description = "Account Id")
	@JsonbProperty("accountId")
	private String accountId;

	@Schema(required = true, example = "0001", description = "Bank Id")
	@JsonbProperty("bankId")
	private String bankId;

	@Schema(required = true, example = "20208AAA", description = "Unique Id")
	@JsonbProperty("uniqueId")
	private String uniqueId;

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

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public PesaLinkAccountValidationCoreRequest(String accountId, String bankId, String uniqueId) {
		super();
		this.accountId = accountId;
		this.bankId = bankId;
		this.uniqueId = uniqueId;
	}

	
}
