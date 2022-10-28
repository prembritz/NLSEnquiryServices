package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "customerNumber", "inputAcctCategory", "detailsAsof", "acctCategory", "systemModule", "acctType",
		"acctTypeDesc", "acctSubType", "accountSubTypeDesc", "accountName", "shadowInstrument", "customerName",
		"creditLineid", "creditLineDesc", "creditLineAmount", "totOutstnd", "instrumentIdNumber", "acctNumber",
		"openAmount", "currentBalance", "currencyCode", "exchangeRate", "maturityDate", "accountStatus",
		"depMarginBalance", "productName", "outStndinaed", "arindicator", "renewalDate", "branchNumber" })
public class ExportLCBillCollectionObject {

	@JsonbProperty("CustomerNumber")
	private String customerNumber;

	@JsonbProperty("InputAcctCategory")
	private String inputAcctCategory;

	@JsonbProperty("Detailsasof")
	private String detailsAsof;

	@JsonbProperty("AcctCategory")
	private String acctCategory;

	@JsonbProperty("SystemModule")
	private String systemModule;

	@JsonbProperty("AcctType")
	private Integer acctType;

	@JsonbProperty("AcctTypeDesc")
	private String acctTypeDesc;

	@JsonbProperty("AcctSubType")
	private String acctSubType;

	@JsonbProperty("AccountSubTypeDesc")
	private String accountSubTypeDesc;

	@JsonbProperty("AccountName")
	private String accountName;

	@JsonbProperty("ShadowInstrument")
	private String shadowInstrument;

	@JsonbProperty("CustomerName")
	private String customerName;

	@JsonbProperty("CreditlineId")
	private String creditLineid;

	@JsonbProperty("CreditlineDesc")
	private String creditLineDesc;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("CreditlineAmount")
	private Double creditLineAmount;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("Totoutstnd")
	private Double totOutstnd;

	@JsonbProperty("InstrumentIdNumber")
	private String instrumentIdNumber;

	@JsonbProperty("Acctnumber")
	private String acctNumber;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("OpenAmount")
	private Double openAmount;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("CurrentBalance")
	private Double currentBalance;

	@JsonbProperty("CurrencyCode")
	private String currencyCode;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("ExchangeRate")
	private Double exchangeRate;

	@JsonbProperty("MaturityDate")
	private String maturityDate;

	@JsonbProperty("AccountStatus")
	private String accountStatus;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DepmarginBalance")
	private Double depMarginBalance;

	@JsonbProperty("ProductName")
	private String productName;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("Outstndinaed")
	private Double outStndinaed;

	@JsonbProperty("Arindicator")
	private String arindicator;

	@JsonbProperty("RenewalDate")
	private String renewalDate;

	@JsonbProperty("BranchCode")
	private String branchNumber;

	public ExportLCBillCollectionObject() {
		this.setCustomerNumber(customerNumber);
		this.setInputAcctCategory(inputAcctCategory);
		this.setDetailsAsof(detailsAsof);
		this.setAcctCategory(acctCategory);
		this.setSystemModule(systemModule);
		this.setAcctType(acctType);
		this.setAcctTypeDesc(acctTypeDesc);
		this.setAcctSubType(acctSubType);
		this.setAccountSubTypeDesc(accountSubTypeDesc);
		this.setAccountName(accountName);
		this.setShadowInstrument(shadowInstrument);
		this.setCustomerName(customerName);
		this.setCreditLineid(creditLineid);
		this.setCreditLineDesc(creditLineDesc);
		this.setCreditLineAmount(creditLineAmount);
		this.setTotOutstnd(totOutstnd);
		this.setInstrumentIdNum(instrumentIdNumber);
		this.setAcctNumber(acctNumber);
		this.setOpenAmount(openAmount);
		this.setCurrentBalance(currentBalance);
		this.setCurrencyCode(currencyCode);
		this.setExchangeRate(exchangeRate);
		this.setMaturityDate(maturityDate);
		this.setAccountStatus(accountStatus);
		this.setDepmarginBalance(depMarginBalance);
		this.setProductName(productName);
		this.setOutStndinaed(outStndinaed);
		this.setArindicator(arindicator);
		this.setRenewalDate(renewalDate);
		this.setBranchNumber(branchNumber);
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getInputAcctCategory() {
		return inputAcctCategory;
	}

	public void setInputAcctCategory(String inputAcctCategory) {
		this.inputAcctCategory = inputAcctCategory;
	}

	public String getDetailsAsof() {
		return detailsAsof;
	}

	public void setDetailsAsof(String detailsAsof) {
		this.detailsAsof = detailsAsof;
	}

	public String getAcctCategory() {
		return acctCategory;
	}

	public void setAcctCategory(String acctCategory) {
		this.acctCategory = acctCategory;
	}

	public String getSystemModule() {
		return systemModule;
	}

	public void setSystemModule(String systemModule) {
		this.systemModule = systemModule;
	}

	public Integer getAcctType() {
		return acctType;
	}

	public void setAcctType(Integer acctType) {
		this.acctType = acctType;
	}

	public String getAcctTypeDesc() {
		return acctTypeDesc;
	}

	public void setAcctTypeDesc(String acctTypeDesc) {
		this.acctTypeDesc = acctTypeDesc;
	}

	public String getAcctSubType() {
		return acctSubType;
	}

	public void setAcctSubType(String acctSubType) {
		this.acctSubType = acctSubType;
	}

	public String getAccountSubTypeDesc() {
		return accountSubTypeDesc;
	}

	public void setAccountSubTypeDesc(String accountSubTypeDesc) {
		this.accountSubTypeDesc = accountSubTypeDesc;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getShadowInstrument() {
		return shadowInstrument;
	}

	public void setShadowInstrument(String shadowInstrument) {
		this.shadowInstrument = shadowInstrument;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCreditLineid() {
		return creditLineid;
	}

	public void setCreditLineid(String creditLineid) {
		this.creditLineid = creditLineid;
	}

	public String getCreditLineDesc() {
		return creditLineDesc;
	}

	public void setCreditLineDesc(String creditLineDesc) {
		this.creditLineDesc = creditLineDesc;
	}

	public Double getCreditLineAmount() {
		return creditLineAmount;
	}

	public void setCreditLineAmount(Double creditLineAmount) {
		this.creditLineAmount = creditLineAmount;
	}

	public Double getTotOutstnd() {
		return totOutstnd;
	}

	public void setTotOutstnd(Double totOutstnd) {
		this.totOutstnd = totOutstnd;
	}

	public String getInstrumentIdNum() {
		return instrumentIdNumber;
	}

	public void setInstrumentIdNum(String instrumentIdNumber) {
		this.instrumentIdNumber = instrumentIdNumber;
	}

	public String getAcctNumber() {
		return acctNumber;
	}

	public void setAcctNumber(String acctNumber) {
		this.acctNumber = acctNumber;
	}

	public Double getOpenAmount() {
		return openAmount;
	}

	public void setOpenAmount(Double openAmount) {
		this.openAmount = openAmount;
	}

	public Double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(Double currentBalance) {
		this.currentBalance = currentBalance;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public Double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(Double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Double getDepmarginBalance() {
		return depMarginBalance;
	}

	public void setDepmarginBalance(Double depMarginBalance) {
		this.depMarginBalance = depMarginBalance;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Double getOutStndinaed() {
		return outStndinaed;
	}

	public void setOutStndinaed(Double outStndinaed) {
		this.outStndinaed = outStndinaed;
	}

	public String getArindicator() {
		return arindicator;
	}

	public void setArindicator(String arindicator) {
		this.arindicator = arindicator;
	}

	public String getRenewalDate() {
		return renewalDate;
	}

	public void setRenewalDate(String renewalDate) {
		this.renewalDate = renewalDate;
	}

	public String getBranchNumber() {
		return branchNumber;
	}

	public void setBranchNumber(String branchNumber) {
		this.branchNumber = branchNumber;
	}

}
