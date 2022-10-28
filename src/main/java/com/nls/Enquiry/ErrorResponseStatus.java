package com.nls.Enquiry;

public enum ErrorResponseStatus {
	TRANSACTION_NOT_FOUND("There is no Transactions in the Given Range!"),
	CUSTOMER_NOT_ONBOARDED("Customer is not Onboarded!"), TABLE_MAPPING_NOT_FOUND("TABLE_MAPPING_NOT_FOUND"),
	ACCOUNT_NOT_FOUND("Account Doesn't Exist"),
	STATEMENT_NOT_FOUND("Statement Doesn't Exist in the given Date Range !"), FAILURE("Failure"),
	CUSTOMER_NOT_FOUND("Customer not Found"), SUMMARY_NOT_FOUND("Summary Details not Found"),
	INVALID_CHEQUE_RANGE("Please check cheque range!"), CHEQUE_NOT_FOUND("Cheque Number is not Found!"),
	DATA_NOT_FOUND("Data not Found"), LOAN_ACCOUNT_NOT_FOUND("Loan Account Doesn't Exist"),TRANSACTION_NOT_EXISTS("TRANSACTION_NOT_FOUND"),
	SUCCESS("SUCCESS"),FAILED("FAILED"),NOT_FOUND("111");
	

	private String value;

	ErrorResponseStatus(String value) {
		this.setValue(value);
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;

	}
}