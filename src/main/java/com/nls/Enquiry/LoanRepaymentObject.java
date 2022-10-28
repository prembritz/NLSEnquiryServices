package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbNillable(true)
@JsonbPropertyOrder({"installmentNo","dueDate","loanOpeningbal","principalAmt",
	"intrstAmt","intrstRate","totalAmt","loanclsngBal","installmentPaystatus"})
public class LoanRepaymentObject 
{
	
	@JsonbProperty("InstallmentNo")
	private String installmentNo;
	
	@JsonbProperty("DueDate")
	private String dueDate;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("LoanOpeningBalance")
	private Double loanOpeningbal;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("PrincipalAmount")
	private Double principalAmt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("InterestAmount")
	private Double intrstAmt;
	
	@JsonbProperty("InterestRate")
	private Double intrstRate;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("TotalAmount")
	private Double totalAmt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("LoanClosingBalance")
	private Double loanclsngBal;
	
	@JsonbProperty("InstallmentPaymentStatus")
	private String installmentPaystatus;
	
	
	
	public LoanRepaymentObject() {

		this.setInstallmentNo(installmentNo);
		this.setDueDate(dueDate);
		this.setLoanOpeningbal(loanOpeningbal);
		this.setPrincipalAmt(principalAmt);
		this.setIntrstAmt(intrstAmt);
		this.setIntrstRate(intrstRate);
		this.setTotalAmt(totalAmt);
		this.setLoanclsngBal(loanclsngBal);
		this.setInstallmentPaystatus(installmentPaystatus);
	}

	public String getInstallmentNo() {
		return installmentNo;
	}



	public void setInstallmentNo(String installmentNo) {
		this.installmentNo = installmentNo;
	}



	public String getDueDate() {
		return dueDate;
	}



	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}



	public Double getLoanOpeningbal() {
		return loanOpeningbal;
	}



	public void setLoanOpeningbal(Double loanOpeningbal) {
		this.loanOpeningbal = loanOpeningbal;
	}



	public Double getPrincipalAmt() {
		return principalAmt;
	}



	public void setPrincipalAmt(Double principalAmt) {
		this.principalAmt = principalAmt;
	}



	public Double getIntrstAmt() {
		return intrstAmt;
	}



	public void setIntrstAmt(Double intrstAmt) {
		this.intrstAmt = intrstAmt;
	}



	public Double getIntrstRate() {
		return intrstRate;
	}



	public void setIntrstRate(Double intrstRate) {
		this.intrstRate = intrstRate;
	}



	public Double getTotalAmt() {
		return totalAmt;
	}



	public void setTotalAmt(Double totalAmt) {
		this.totalAmt = totalAmt;
	}



	public Double getLoanclsngBal() {
		return loanclsngBal;
	}



	public void setLoanclsngBal(Double loanclsngBal) {
		this.loanclsngBal = loanclsngBal;
	}



	public String getInstallmentPaystatus() {
		return installmentPaystatus;
	}



	public void setInstallmentPaystatus(String installmentPaystatus) {
		this.installmentPaystatus = installmentPaystatus;
	}


}
