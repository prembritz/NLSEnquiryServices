package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbNillable(true)
@JsonbPropertyOrder({ "errCode", "errorDesc", "uId", "accNo", "accName", "custId", "cur", "accType", "acctypeDesc",
		 "bCode", "availBal", "frozenAmt", "odLmt", "totalBalance", "unclearedBalance",
		"bicId", "prevdayBal", "curBal", "openBal","accStatus","postingRestrict", "accstatusDesc", "timeStamp" })
public class AccountDetailsObject {

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	@JsonbProperty("UnitID")
	private String uId;

	@JsonbProperty("AccountNumber")
	private String accNo;

	@JsonbProperty("AccountName")
	private String accName;

	@JsonbProperty("CIF")
	private String custId;

	@JsonbProperty("Currency")
	private String cur;

	@JsonbProperty("AccountType")
	private Integer accType;

	@JsonbProperty("AccountTypeDesc")
	private String acctypeDesc;

	@JsonbProperty("AccountBranch")
	private String bCode;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("AvailableBalance")
	private Double availBal;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("TotalBalance")
	private Double totalBalance;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("UnclearedAmount")
	private Double unclearedBalance;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("FrozenAmount")
	private Double frozenAmt;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("ODLimit")
	private Double odLmt;

	@JsonbProperty("BIC")
	private String bicId;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("PreviousDayBalance")
	private Double prevdayBal;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("CurrentBalance")
	private Double curBal;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("OpeningBalance")
	private Double openBal;

	@JsonbProperty("AccountStatus")
	private String accStatus;
	
	@JsonbProperty("PostingRestrict")
	private String postingRestrict;

	@JsonbProperty("AccountStatusDesc")
	private String accstatusDesc;

	@JsonbProperty("LastBalanceTimestamp")
	private String timeStamp;

	public AccountDetailsObject() {

		this.setAccNo(accNo);
		this.setAccName(accName);
		this.setCustId(custId);
		this.setCur(cur);
		this.setAccType(accType);
		this.setAcctypeDesc(acctypeDesc);
		this.setbCode(bCode);
		this.setAvailBal(availBal);
		this.setTotalBalance(totalBalance);
		this.setUnclearedBalance(unclearedBalance);
		this.setFrozenAmt(frozenAmt);
		this.setOdLmt(odLmt);
		this.setBicId(bicId);
		this.setPrevdayBal(prevdayBal);
		this.setCurBal(curBal);
		this.setOpenBal(openBal);
		this.setAccStatus(accStatus);
		this.setAccstatusDesc(accstatusDesc);
		this.setTimeStamp(timeStamp);

	}

	public ERROR_CODE getErrCode() {
		return errCode;
	}

	public void setErrCode(ERROR_CODE errCode) {
		this.errCode = errCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCur() {
		return cur;
	}

	public void setCur(String cur) {
		this.cur = cur;
	}

	public Integer getAccType() {
		return accType;
	}

	public void setAccType(Integer accType) {
		this.accType = accType;
	}

	public String getAcctypeDesc() {
		return acctypeDesc;
	}

	public void setAcctypeDesc(String acctypeDesc) {
		this.acctypeDesc = acctypeDesc;
	}

	public String getbCode() {
		return bCode;
	}

	public void setbCode(String bCode) {
		this.bCode = bCode;
	}

	public Double getAvailBal() {
		return availBal;
	}

	public Double getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(Double totalBalance) {
		this.totalBalance = totalBalance;
	}

	public Double getUnclearedBalance() {
		return unclearedBalance;
	}

	public void setUnclearedBalance(Double unclearedBalance) {
		this.unclearedBalance = unclearedBalance;
	}

	public void setAvailBal(Double availBal) {
		this.availBal = availBal;
	}

	public Double getFrozenAmt() {
		return frozenAmt;
	}

	public void setFrozenAmt(Double frozenAmt) {
		this.frozenAmt = frozenAmt;
	}

	public Double getOdLmt() {
		return odLmt;
	}

	public void setOdLmt(Double odLmt) {
		this.odLmt = odLmt;
	}

	public String getBicId() {
		return bicId;
	}

	public void setBicId(String bicId) {
		this.bicId = bicId;
	}

	public Double getPrevdayBal() {
		return prevdayBal;
	}

	public void setPrevdayBal(Double prevdayBal) {
		this.prevdayBal = prevdayBal;
	}

	public Double getCurBal() {
		return curBal;
	}

	public void setCurBal(Double curBal) {
		this.curBal = curBal;
	}

	public Double getOpenBal() {
		return openBal;
	}

	public void setOpenBal(Double openBal) {
		this.openBal = openBal;
	}

	public String getAccStatus() {
		return accStatus;
	}

	public void setAccStatus(String accStatus) {
		this.accStatus = accStatus;
	}

	public String getPostingRestrict() {
		return postingRestrict;
	}

	public void setPostingRestrict(String postingRestrict) {
		this.postingRestrict = postingRestrict;
	}

	public String getAccstatusDesc() {
		return accstatusDesc;
	}

	public void setAccstatusDesc(String accstatusDesc) {
		this.accstatusDesc = accstatusDesc;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

}
