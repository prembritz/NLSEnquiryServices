package com.nls.Enquiry;

public enum ACCOUNT_TYPE {

	ACCOUNTS("C"), LENDING("L"), DEPOSITS("T"), UNKNOWN("U");
   
	private String value;

	private ACCOUNT_TYPE(String value) {
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static boolean isLoanOrDeposit(String productLine) {
		boolean isLD = false;
		try {
			ACCOUNT_TYPE type = ACCOUNT_TYPE.valueOf(productLine);
			isLD = type == ACCOUNT_TYPE.LENDING || type == ACCOUNT_TYPE.DEPOSITS;
		} catch (Exception except) {

		}
		return isLD;
	}

	public static boolean isLoanOrDepositContract(String productLine) {
		boolean isLD = false;
		try {
			ACCOUNT_TYPE type = ACCOUNT_TYPE.valueOf(productLine);
			isLD = type == ACCOUNT_TYPE.LENDING || type == ACCOUNT_TYPE.DEPOSITS;
		} catch (Exception except) {
			// except.printStackTrace();
		}
		return isLD;
	}

	public static boolean isLoanContract(String productLine) {
		boolean isLD = false;
		try {
			ACCOUNT_TYPE type = ACCOUNT_TYPE.valueOf(productLine);
			isLD = type == ACCOUNT_TYPE.LENDING;
		} catch (Exception except) {
			// except.printStackTrace();
		}
		return isLD;
	}
}
