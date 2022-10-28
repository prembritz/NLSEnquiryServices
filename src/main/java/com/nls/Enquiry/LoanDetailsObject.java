package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbNillable(true)
@JsonbPropertyOrder({ "errCode","errorDesc","unId","loanAccnumber","loanCur","lnType","loanTypedesc","fundingAcc",
	"paymentFreq","originalAmt","outstandingLoanamt","pastDueamt","lastPaymentamt","emi","emiPaid","contractDate",
	"maturityDate","intrstRate","nextPaydate","nextPayamt","payOffamt","principalClsgamt","disbursementAcc",
	"intRepaymentamt","intPaydate","principalPaydate","principalPayamt","lnHoldername","lnTenor" })
public class LoanDetailsObject 
{
	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";
	
	@JsonbProperty("UnitID")
	public String unId;
	
	@JsonbProperty("LoanAccountNumber")
	private String loanAccnumber;
	
	@JsonbProperty("LoanCurrency")
	private String loanCur;
	
	@JsonbProperty("LoanType")
	private String lnType;
	
	@JsonbProperty("LoanTypeDesc")
	private String loanTypedesc;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("FundingAccount")
	private String fundingAcc;
	
	@JsonbProperty("PaymentFrequency")
	private String paymentFreq;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("OriginalAmount")
	private Double originalAmt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("OutstandingLoanAmount")
	private Double outstandingLoanamt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("PastDueAmount")
	private Double pastDueamt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("LastPaymentAmount")
	private Double lastPaymentamt;
	
	@JsonbProperty("NumberOfInstallment")
	private Double emi;
	
	@JsonbProperty("InstallmentPaid")
	private Integer emiPaid;
	
	//@JsonbDateFormat("yyyy-MM-dd")
	@JsonbProperty("LoanStartDate")
	private String contractDate;
	
	//@JsonbDateFormat("yyyy-MM-dd")
	@JsonbProperty("LoanMaturityDate")
	private String maturityDate;
	
	@JsonbProperty("InterestRate")
	private Double intrstRate;
	
	//@JsonbDateFormat("yyyy-MM-dd")
	@JsonbProperty("NextPaymentDate")
	private String nextPaydate;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("NextPaymentAmount")
	private Double nextPayamt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("PayoffAmount")
	private Double payOffamt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("PrincipalClosingAmount")
	private Double principalClsgamt;
	
	@JsonbProperty("DisbursementAccount")
	private String disbursementAcc;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("InterestRePaymentAmount")
	private Double intRepaymentamt;
	
	//@JsonbDateFormat("yyyy-MM-dd")
	@JsonbProperty("InterestPaymentDate")
	private String intPaydate;
	
	//@JsonbDateFormat("yyyy-MM-dd")
	@JsonbProperty("PrincipalPaymentDate")
	private String principalPaydate;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("PrincipalPaymentAmount")
	private Double principalPayamt;
	
	@JsonbProperty("LoanHolderName")
	private String lnHoldername;
	
	@JsonbProperty("LoanTenor")
	private String lnTenor;
	
	
	public LoanDetailsObject() {

		this.setErrCode(errCode);
		this.setErrorDesc(errorDesc);
		this.setUnId(unId);
		this.setLoanAccnumber(loanAccnumber);
		this.setLoanCur(loanCur);
		this.setLnType(lnType);
		this.setLoanTypedesc(loanTypedesc);
		this.setFundingAcc(fundingAcc);
		this.setPaymentFreq(paymentFreq);
		this.setOriginalAmt(originalAmt);
		this.setOutstandingLoanamt(outstandingLoanamt);
		this.setPastDueamt(pastDueamt);
		this.setLastPaymentamt(lastPaymentamt);
		this.setEmi(emi);
		this.setEmiPaid(emiPaid);
		this.setContractDate(contractDate);
		this.setMaturityDate(maturityDate);
		
		this.setIntrstRate(intrstRate);
		this.setNextPaydate(nextPaydate);
		this.setNextPayamt(nextPayamt);
		this.setPayOffamt(payOffamt);
		this.setPrincipalClsgamt(principalClsgamt);
		this.setDisbursementAcc(disbursementAcc);
		this.setIntRepaymentamt(intRepaymentamt);
		this.setIntPaydate(intPaydate);
		this.setPrincipalPaydate(principalPaydate);
		this.setPrincipalPayamt(principalPayamt);
		this.setLnHoldername(lnHoldername);
		this.setLnTenor(lnTenor);

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


	public String getUnId() {
		return unId;
	}


	public void setUnId(String unId) {
		this.unId = unId;
	}


	public String getLoanAccnumber() {
		return loanAccnumber;
	}


	public void setLoanAccnumber(String loanAccnumber) {
		this.loanAccnumber = loanAccnumber;
	}


	public String getLoanCur() {
		return loanCur;
	}


	public void setLoanCur(String loanCur) {
		this.loanCur = loanCur;
	}


	public String getLnType() {
		return lnType;
	}


	public void setLnType(String lnType) {
		this.lnType = lnType;
	}


	public String getLoanTypedesc() {
		return loanTypedesc;
	}


	public void setLoanTypedesc(String loanTypedesc) {
		this.loanTypedesc = loanTypedesc;
	}


	public String getFundingAcc() {
		return fundingAcc;
	}


	public void setFundingAcc(String fundingAcc) {
		this.fundingAcc = fundingAcc;
	}


	public String getPaymentFreq() {
		return paymentFreq;
	}


	public void setPaymentFreq(String paymentFreq) {
		this.paymentFreq = paymentFreq;
	}


	public Double getOriginalAmt() {
		return originalAmt;
	}


	public void setOriginalAmt(Double originalAmt) {
		this.originalAmt = originalAmt;
	}


	public Double getOutstandingLoanamt() {
		return outstandingLoanamt;
	}


	public void setOutstandingLoanamt(Double outstandingLoanamt) {
		this.outstandingLoanamt = outstandingLoanamt;
	}


	public Double getPastDueamt() {
		return pastDueamt;
	}


	public void setPastDueamt(Double pastDueamt) {
		this.pastDueamt = pastDueamt;
	}


	public Double getLastPaymentamt() {
		return lastPaymentamt;
	}


	public void setLastPaymentamt(Double lastPaymentamt) {
		this.lastPaymentamt = lastPaymentamt;
	}


	public Double getEmi() {
		return emi;
	}


	public void setEmi(Double emi) {
		this.emi = emi;
	}


	public Integer getEmiPaid() {
		return emiPaid;
	}


	public void setEmiPaid(Integer emiPaid) {
		this.emiPaid = emiPaid;
	}


	public String getContractDate() {
		return contractDate;
	}


	public void setContractDate(String contractDate) {
		this.contractDate = contractDate;
	}


	public String getMaturityDate() {
		return maturityDate;
	}


	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}


	public Double getIntrstRate() {
		return intrstRate;
	}


	public void setIntrstRate(Double intrstRate) {
		this.intrstRate = intrstRate;
	}


	public String getNextPaydate() {
		return nextPaydate;
	}


	public void setNextPaydate(String nextPaydate) {
		this.nextPaydate = nextPaydate;
	}


	public Double getNextPayamt() {
		return nextPayamt;
	}


	public void setNextPayamt(Double nextPayamt) {
		this.nextPayamt = nextPayamt;
	}


	public Double getPayOffamt() {
		return payOffamt;
	}


	public void setPayOffamt(Double payOffamt) {
		this.payOffamt = payOffamt;
	}


	public Double getPrincipalClsgamt() {
		return principalClsgamt;
	}


	public void setPrincipalClsgamt(Double principalClsgamt) {
		this.principalClsgamt = principalClsgamt;
	}


	public String getDisbursementAcc() {
		return disbursementAcc;
	}


	public void setDisbursementAcc(String disbursementAcc) {
		this.disbursementAcc = disbursementAcc;
	}


	public Double getIntRepaymentamt() {
		return intRepaymentamt;
	}


	public void setIntRepaymentamt(Double intRepaymentamt) {
		this.intRepaymentamt = intRepaymentamt;
	}


	public String getIntPaydate() {
		return intPaydate;
	}


	public void setIntPaydate(String intPaydate) {
		this.intPaydate = intPaydate;
	}


	public String getPrincipalPaydate() {
		return principalPaydate;
	}


	public void setPrincipalPaydate(String principalPaydate) {
		this.principalPaydate = principalPaydate;
	}


	public Double getPrincipalPayamt() {
		return principalPayamt;
	}


	public void setPrincipalPayamt(Double principalPayamt) {
		this.principalPayamt = principalPayamt;
	}


	public String getLnHoldername() {
		return lnHoldername;
	}


	public void setLnHoldername(String lnHoldername) {
		this.lnHoldername = lnHoldername;
	}


	public String getLnTenor() {
		return lnTenor;
	}


	public void setLnTenor(String lnTenor) {
		this.lnTenor = lnTenor;
	}

	
}

