package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbNillable(true)
@JsonbPropertyOrder({"loanCur","loanAccnumber","lnType","loanTypedesc","loanAmt",
	 "loanBal","loanbkgDate","loanclsedate","country","erCode","erMsg" })
public class LoanSummaryObject {
   
@JsonbProperty("LoanCurrency")
   private String loanCur;
   
   @JsonbProperty("LoanAccountNo")
   private String loanAccnumber;
   
   @JsonbProperty("LoanType")
   private String lnType;
   
   @JsonbProperty("LoanTypeDescription")
   private String loanTypedesc;
   
   @JsonbNumberFormat("###0.00")
   @JsonbProperty("LoanAmount")
   private Double loanAmt;
   
   @JsonbNumberFormat("###0.00")
   @JsonbProperty("LoanBalance")
   private Double loanBal;
   
  // @JsonbDateFormat("yyyy-MM-dd")
   @JsonbProperty("LoanBookingDate")
   private String loanbkgDate;
   
  // @JsonbDateFormat("yyyy-MM-dd")
   @JsonbProperty("LoanClosureDueDate")
   private String loanclsedate;
   
   @JsonbProperty("Country")
   private String country;
   
     @JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE erCode = ERROR_CODE.SUCCESSFUL;
	
     @JsonbProperty("ErrorMessage")
      private String erMsg = "Loan Account Found";
  


	public LoanSummaryObject() {
		this.setLoanCur(loanCur);
		this.setLoanAccnumber(loanAccnumber);
		this.setLnType(lnType);
		this.setLoanTypedesc(loanTypedesc);
		this.setLoanAmt(loanAmt);
		this.setLoanBal(loanBal);
		this.setLoanbkgDate(loanbkgDate);
		this.setLoanclsedate(loanclsedate);
		this.setCountry(country);
	}


	public String getLoanCur() {
		return loanCur;
	}


	public void setLoanCur(String loanCur) {
		this.loanCur = loanCur;
	}


	public String getLoanAccnumber() {
		return loanAccnumber;
	}


	public void setLoanAccnumber(String loanAccnumber) {
		this.loanAccnumber = loanAccnumber;
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


	public Double getLoanAmt() {
		return loanAmt;
	}


	public void setLoanAmt(Double loanAmt) {
		this.loanAmt = loanAmt;
	}


	public Double getLoanBal() {
		return loanBal;
	}


	public void setLoanBal(Double loanBal) {
		this.loanBal = loanBal;
	}


	public String getLoanbkgDate() {
		return loanbkgDate;
	}


	public void setLoanbkgDate(String loanbkgDate) {
		this.loanbkgDate = loanbkgDate;
	}


	public String getLoanclsedate() {
		return loanclsedate;
	}


	public void setLoanclsedate(String loanclsedate) {
		this.loanclsedate = loanclsedate;
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


		public String getCountry() {
			return country;
		}


		public void setCountry(String country) {
			this.country = country;
		}

}
