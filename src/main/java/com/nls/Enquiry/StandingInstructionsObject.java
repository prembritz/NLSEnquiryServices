package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "branchId", "customerId", "custGivenName", "custMidName", "stndDebAccount", "stndIng",
		"stndChargeAmt", "stndCurrency", "stndAmt", "stndTranBy", "stndRefNo", "tranByDesc", "stndBenName",
		"stndBenAddr1", "stndBenAddr2", "stndBenAcct", "stndBenBankName1", "stndBenBankName2", "stndPayDetail1",
		"stndPayDetail2", "stndPeriod", "stndFistPay", "stndEffectDate", "stndLastPay", "stndOrderDate", "stndOPId",
		"stndAppDate", "stndAppId", "stndFirstPayNew", "stndLastPayNew", "stndAmtNew", "stndCurrencyNew",
		"stndComment1", "stndComment2", "bnkCode", "cityId", "ttCountryId", "stndNextPay", "stndNextPayNew",
		"stndProcStatus", "stndClose", "stndCloseId", "stndCCloseId", "stndCloseDate", "stndCCloseDate",
		"stndOrderType", "stndCharity", "stndCharityDesc", "stndCode", "tranDesc", "bicCode", "bnkName", "chrgCode",
		"tranTypeCode", "tlrSwift701", "stndFailCounter", "stndLastSuccessDate"})
public class StandingInstructionsObject {
	@JsonbProperty("BranchId")
	private String branchId;

	@JsonbProperty("CustomerId")
	private String customerId;

	@JsonbProperty("CustGivenName")
	private String custGivenName;

	@JsonbProperty("CustMidName")
	private String custMidName;

	@JsonbProperty("StndDebAccount")
	private String stndDebAccount;

	@JsonbProperty("StndIng")
	private String stndIng;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("StndChargeAmt")
	private double stndChargeAmt;

	@JsonbProperty("StndCurrency")
	private String stndCurrency;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("StndAmt")
	private double stndAmt;

	@JsonbProperty("StndTranBy")
	private String stndTranBy;

	@JsonbProperty("StndRefNo")
	private String stndRefNo;

	@JsonbProperty("TranByDesc")
	private String tranByDesc;

	@JsonbProperty("StndBenName")
	private String stndBenName;

	@JsonbProperty("StndBenAddr1")
	private String stndBenAddr1;

	@JsonbProperty("StndBenAddr2")
	private String stndBenAddr2;

	@JsonbProperty("StndBenAcct")
	private String stndBenAcct;

	@JsonbProperty("StndBenBankName1")
	private String stndBenBankName1;

	@JsonbProperty("StndBenBankName2")
	private String stndBenBankName2;

	@JsonbProperty("StndPayDetail1")
	private String stndPayDetail1;

	@JsonbProperty("StndPayDetail2")
	private String stndPayDetail2;

	@JsonbProperty("StndPeriod")
	private String stndPeriod;

	@JsonbProperty("StndFistPay")
	private String stndFistPay;

	@JsonbProperty("StndEffectDate")
	private String stndEffectDate;

	@JsonbProperty("StndLastPay")
	private String stndLastPay;

	@JsonbProperty("StndOrderDate")
	private String stndOrderDate;

	@JsonbProperty("StndOPId")
	private String stndOPId;

	@JsonbProperty("StndAppDate")
	private String stndAppDate;

	@JsonbProperty("StndAppId")
	private String stndAppId;

	@JsonbProperty("StndFirstPayNew")
	private String stndFirstPayNew;

	@JsonbProperty("StndLastPayNew")
	private String stndLastPayNew;

	@JsonbProperty("StndAmtNew")
	private String stndAmtNew;

	@JsonbProperty("StndCurrencyNew")
	private String stndCurrencyNew;

	@JsonbProperty("StndComment1")
	private String stndComment1;

	@JsonbProperty("StndComment2")
	private String stndComment2;

	@JsonbProperty("BnkCode")
	private String bnkCode;

	@JsonbProperty("CityId")
	private String cityId;

	@JsonbProperty("TtCountryId")
	private String ttCountryId;

	@JsonbProperty("StndNextPay")
	private String stndNextPay;

	@JsonbProperty("StndNextPayNew")
	private String stndNextPayNew;

	@JsonbProperty("StndProcStatus")
	private String stndProcStatus;

	@JsonbProperty("StndClose")
	private String stndClose;

	@JsonbProperty("StndCloseId")
	private String stndCloseId;

	@JsonbProperty("StndCCloseId")
	private String stndCCloseId;

	@JsonbProperty("StndCloseDate")
	private String stndCloseDate;

	@JsonbProperty("StndCCloseDate")
	private String stndCCloseDate;

	@JsonbProperty("StndOrderType")
	private String stndOrderType;

	@JsonbProperty("StndCharity")
	private String stndCharity;

	@JsonbProperty("StndCharityDesc")
	private String stndCharityDesc;

	@JsonbProperty("StndCode")
	private String stndCode;

	@JsonbProperty("TranDesc")
	private String tranDesc;

	@JsonbProperty("BicCode")
	private String bicCode;

	@JsonbProperty("BnkName")
	private String bnkName;

	@JsonbProperty("ChrgCode")
	private String chrgCode;

	@JsonbProperty("TranTypeCode")
	private String tranTypeCode;

	@JsonbProperty("TlrSwift701")
	private String tlrSwift701;

	@JsonbProperty("StndFailCounter")
	private String stndFailCounter;

	@JsonbProperty("StndLastSuccessDate")
	private String stndLastSuccessDate;

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustGivenName() {
		return custGivenName;
	}

	public void setCustGivenName(String custGivenName) {
		this.custGivenName = custGivenName;
	}

	public String getCustMidName() {
		return custMidName;
	}

	public void setCustMidName(String custMidName) {
		this.custMidName = custMidName;
	}

	public String getStndDebAccount() {
		return stndDebAccount;
	}

	public void setStndDebAccount(String stndDebAccount) {
		this.stndDebAccount = stndDebAccount;
	}

	public String getStndIng() {
		return stndIng;
	}

	public void setStndIng(String stndIng) {
		this.stndIng = stndIng;
	}

	public double getStndChargeAmt() {
		return stndChargeAmt;
	}

	public void setStndChargeAmt(double stndChargeAmt) {
		this.stndChargeAmt = stndChargeAmt;
	}

	public String getStndCurrency() {
		return stndCurrency;
	}

	public void setStndCurrency(String stndCurrency) {
		this.stndCurrency = stndCurrency;
	}

	public double getStndAmt() {
		return stndAmt;
	}

	public void setStndAmt(double stndAmt) {
		this.stndAmt = stndAmt;
	}

	public String getStndTranBy() {
		return stndTranBy;
	}

	public void setStndTranBy(String stndTranBy) {
		this.stndTranBy = stndTranBy;
	}

	public String getStndRefNo() {
		return stndRefNo;
	}

	public void setStndRefNo(String stndRefNo) {
		this.stndRefNo = stndRefNo;
	}

	public String getTranByDesc() {
		return tranByDesc;
	}

	public void setTranByDesc(String tranByDesc) {
		this.tranByDesc = tranByDesc;
	}

	public String getStndBenName() {
		return stndBenName;
	}

	public void setStndBenName(String stndBenName) {
		this.stndBenName = stndBenName;
	}

	public String getStndBenAddr1() {
		return stndBenAddr1;
	}

	public void setStndBenAddr1(String stndBenAddr1) {
		this.stndBenAddr1 = stndBenAddr1;
	}

	public String getStndBenAddr2() {
		return stndBenAddr2;
	}

	public void setStndBenAddr2(String stndBenAddr2) {
		this.stndBenAddr2 = stndBenAddr2;
	}

	public String getStndBenAcct() {
		return stndBenAcct;
	}

	public void setStndBenAcct(String stndBenAcct) {
		this.stndBenAcct = stndBenAcct;
	}

	public String getStndBenBankName1() {
		return stndBenBankName1;
	}

	public void setStndBenBankName1(String stndBenBankName1) {
		this.stndBenBankName1 = stndBenBankName1;
	}

	public String getStndBenBankName2() {
		return stndBenBankName2;
	}

	public void setStndBenBankName2(String stndBenBankName2) {
		this.stndBenBankName2 = stndBenBankName2;
	}

	public String getStndPayDetail1() {
		return stndPayDetail1;
	}

	public void setStndPayDetail1(String stndPayDetail1) {
		this.stndPayDetail1 = stndPayDetail1;
	}

	public String getStndPayDetail2() {
		return stndPayDetail2;
	}

	public void setStndPayDetail2(String stndPayDetail2) {
		this.stndPayDetail2 = stndPayDetail2;
	}

	public String getStndPeriod() {
		return stndPeriod;
	}

	public void setStndPeriod(String stndPeriod) {
		this.stndPeriod = stndPeriod;
	}

	public String getStndFistPay() {
		return stndFistPay;
	}

	public void setStndFistPay(String stndFistPay) {
		this.stndFistPay = stndFistPay;
	}

	public String getStndEffectDate() {
		return stndEffectDate;
	}

	public void setStndEffectDate(String stndEffectDate) {
		this.stndEffectDate = stndEffectDate;
	}

	public String getStndLastPay() {
		return stndLastPay;
	}

	public void setStndLastPay(String stndLastPay) {
		this.stndLastPay = stndLastPay;
	}

	public String getStndOrderDate() {
		return stndOrderDate;
	}

	public void setStndOrderDate(String stndOrderDate) {
		this.stndOrderDate = stndOrderDate;
	}

	public String getStndOPId() {
		return stndOPId;
	}

	public void setStndOPId(String stndOPId) {
		this.stndOPId = stndOPId;
	}

	public String getStndAppDate() {
		return stndAppDate;
	}

	public void setStndAppDate(String stndAppDate) {
		this.stndAppDate = stndAppDate;
	}

	public String getStndAppId() {
		return stndAppId;
	}

	public void setStndAppId(String stndAppId) {
		this.stndAppId = stndAppId;
	}

	public String getStndFirstPayNew() {
		return stndFirstPayNew;
	}

	public void setStndFirstPayNew(String stndFirstPayNew) {
		this.stndFirstPayNew = stndFirstPayNew;
	}

	public String getStndLastPayNew() {
		return stndLastPayNew;
	}

	public void setStndLastPayNew(String stndLastPayNew) {
		this.stndLastPayNew = stndLastPayNew;
	}

	public String getStndAmtNew() {
		return stndAmtNew;
	}

	public void setStndAmtNew(String stndAmtNew) {
		this.stndAmtNew = stndAmtNew;
	}

	public String getStndCurrencyNew() {
		return stndCurrencyNew;
	}

	public void setStndCurrencyNew(String stndCurrencyNew) {
		this.stndCurrencyNew = stndCurrencyNew;
	}

	public String getStndComment1() {
		return stndComment1;
	}

	public void setStndComment1(String stndComment1) {
		this.stndComment1 = stndComment1;
	}

	public String getStndComment2() {
		return stndComment2;
	}

	public void setStndComment2(String stndComment2) {
		this.stndComment2 = stndComment2;
	}

	public String getBnkCode() {
		return bnkCode;
	}

	public void setBnkCode(String bnkCode) {
		this.bnkCode = bnkCode;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getTtCountryId() {
		return ttCountryId;
	}

	public void setTtCountryId(String ttCountryId) {
		this.ttCountryId = ttCountryId;
	}

	public String getStndNextPay() {
		return stndNextPay;
	}

	public void setStndNextPay(String stndNextPay) {
		this.stndNextPay = stndNextPay;
	}

	public String getStndNextPayNew() {
		return stndNextPayNew;
	}

	public void setStndNextPayNew(String stndNextPayNew) {
		this.stndNextPayNew = stndNextPayNew;
	}

	public String getStndProcStatus() {
		return stndProcStatus;
	}

	public void setStndProcStatus(String stndProcStatus) {
		this.stndProcStatus = stndProcStatus;
	}

	public String getStndClose() {
		return stndClose;
	}

	public void setStndClose(String stndClose) {
		this.stndClose = stndClose;
	}

	public String getStndCloseId() {
		return stndCloseId;
	}

	public void setStndCloseId(String stndCloseId) {
		this.stndCloseId = stndCloseId;
	}

	public String getStndCCloseId() {
		return stndCCloseId;
	}

	public void setStndCCloseId(String stndCCloseId) {
		this.stndCCloseId = stndCCloseId;
	}

	public String getStndCloseDate() {
		return stndCloseDate;
	}

	public void setStndCloseDate(String stndCloseDate) {
		this.stndCloseDate = stndCloseDate;
	}

	public String getStndCCloseDate() {
		return stndCCloseDate;
	}

	public void setStndCCloseDate(String stndCCloseDate) {
		this.stndCCloseDate = stndCCloseDate;
	}

	public String getStndOrderType() {
		return stndOrderType;
	}

	public void setStndOrderType(String stndOrderType) {
		this.stndOrderType = stndOrderType;
	}

	public String getStndCharity() {
		return stndCharity;
	}

	public void setStndCharity(String stndCharity) {
		this.stndCharity = stndCharity;
	}

	public String getStndCharityDesc() {
		return stndCharityDesc;
	}

	public void setStndCharityDesc(String stndCharityDesc) {
		this.stndCharityDesc = stndCharityDesc;
	}

	public String getStndCode() {
		return stndCode;
	}

	public void setStndCode(String stndCode) {
		this.stndCode = stndCode;
	}

	public String getTranDesc() {
		return tranDesc;
	}

	public void setTranDesc(String tranDesc) {
		this.tranDesc = tranDesc;
	}

	public String getBicCode() {
		return bicCode;
	}

	public void setBicCode(String bicCode) {
		this.bicCode = bicCode;
	}

	public String getBnkName() {
		return bnkName;
	}

	public void setBnkName(String bnkName) {
		this.bnkName = bnkName;
	}

	public String getChrgCode() {
		return chrgCode;
	}

	public void setChrgCode(String chrgCode) {
		this.chrgCode = chrgCode;
	}

	public String getTranTypeCode() {
		return tranTypeCode;
	}

	public void setTranTypeCode(String tranTypeCode) {
		this.tranTypeCode = tranTypeCode;
	}

	public String getTlrSwift701() {
		return tlrSwift701;
	}

	public void setTlrSwift701(String tlrSwift701) {
		this.tlrSwift701 = tlrSwift701;
	}

	public String getStndFailCounter() {
		return stndFailCounter;
	}

	public void setStndFailCounter(String stndFailCounter) {
		this.stndFailCounter = stndFailCounter;
	}

	public String getStndLastSuccessDate() {
		return stndLastSuccessDate;
	}

	public void setStndLastSuccessDate(String stndLastSuccessDate) {
		this.stndLastSuccessDate = stndLastSuccessDate;
	}

	public StandingInstructionsObject() {
		this.setBranchId(branchId);
		this.setCustomerId(customerId);
		this.setCustGivenName(custGivenName);
		this.setCustMidName(custMidName);
		this.setStndDebAccount(stndDebAccount);
		this.setStndIng(stndIng);
		this.setStndChargeAmt(stndChargeAmt);
		this.setStndCurrency(stndCurrency);
		this.setStndAmt(stndAmt);
		this.setStndTranBy(stndTranBy);
		this.setStndRefNo(stndRefNo);
		this.setTranByDesc(tranByDesc);
		this.setStndBenName(stndBenName);
		this.setStndBenAddr1(stndBenAddr1);
		this.setStndBenAddr2(stndBenAddr2);
		this.setStndBenAcct(stndBenAcct);
		this.setStndBenBankName1(stndBenBankName1);
		this.setStndBenBankName2(stndBenBankName2);
		this.setStndPayDetail1(stndPayDetail1);
		this.setStndPayDetail2(stndPayDetail2);
		this.setStndPeriod(stndPeriod);
		this.setStndFistPay(stndFistPay);
		this.setStndEffectDate(stndEffectDate);
		this.setStndLastPay(stndLastPay);
		this.setStndOrderDate(stndOrderDate);
		this.setStndOPId(stndOPId);
		this.setStndAppDate(stndAppDate);
		this.setStndAppId(stndAppId);
		this.setStndFirstPayNew(stndFirstPayNew);
		this.setStndLastPayNew(stndLastPayNew);
		this.setStndAmtNew(stndAmtNew);
		this.setStndCurrencyNew(stndCurrencyNew);
		this.setStndComment1(stndComment1);
		this.setStndComment2(stndComment2);
		this.setBnkCode(bnkCode);
		this.setCityId(cityId);
		this.setTtCountryId(ttCountryId);
		this.setStndNextPay(stndNextPay);
		this.setStndNextPayNew(stndNextPayNew);
		this.setStndProcStatus(stndProcStatus);
		this.setStndClose(stndClose);
		this.setStndCloseId(stndCloseId);
		this.setStndCCloseId(stndCCloseId);
		this.setStndCloseDate(stndCloseDate);
		this.setStndCCloseDate(stndCCloseDate);
		this.setStndOrderType(stndOrderType);
		this.setStndCharity(stndCharity);
		this.setStndCharityDesc(stndCharityDesc);
		this.setStndCode(stndCode);
		this.setTranDesc(tranDesc);
		this.setBicCode(bicCode);
		this.setBnkName(bnkName);
		this.setChrgCode(chrgCode);
		this.setTranTypeCode(tranTypeCode);
		this.setTlrSwift701(tlrSwift701);
		this.setStndFailCounter(stndFailCounter);
		this.setStndLastSuccessDate(stndLastSuccessDate);
	}

}
