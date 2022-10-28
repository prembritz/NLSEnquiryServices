package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "dealNo", "instrument", "dealDate", "settlementDate", "maturityDate", "branch", "buyccy",
		"buyAmount", "sellccy", "sellAmount", "contrrate", "fwdrate", "counterPartyName" })
public class FXFutureObject {

	@JsonbProperty("CustomerId")
	private String customerId;

	@JsonbProperty("RequestTime")
	private String requestTime;

	@JsonbProperty("dealNo")
	private String dealNo;

	@JsonbProperty("Instrument")
	private String instrument;

	@JsonbProperty("dealDate")
	private String dealDate;

	@JsonbProperty("SettlementDate")
	private String settlementDate;

	@JsonbProperty("MaturityDate")
	private String maturityDate;

	@JsonbProperty("Branch")
	private String branch;

	@JsonbProperty("Buyccy")
	private String buyccy;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("Buyamount")
	private double buyAmount;

	@JsonbProperty("Sellccy")
	private String sellccy;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("SellAmount")
	private double sellAmount;

	@JsonbProperty("ContrRate")
	private String contrRate;

	@JsonbProperty("FwdRate")
	private String fwdRate;

	@JsonbProperty("CounterPartyName")
	private String counterPartyName;

	public FXFutureObject() {
		this.setCustomerId(customerId);
		this.setRequestTime(requestTime);
		this.setDealNo(dealNo);
		this.setInstrument(instrument);
		this.setDealDate(dealDate);
		this.setSettlementDate(settlementDate);
		this.setMaturityDate(maturityDate);
		this.setBranch(branch);
		this.setBuyccy(buyccy);
		this.setBuyAmount(buyAmount);
		this.setSellccy(sellccy);
		this.setSellAmount(sellAmount);
		this.setContrRate(contrRate);
		this.setFwdRate(fwdRate);
		this.setCounterPartyname(counterPartyName);
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public String getDealNo() {
		return dealNo;
	}

	public void setDealNo(String dealNo) {
		this.dealNo = dealNo;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public String getDealDate() {
		return dealDate;
	}

	public void setDealDate(String dealDate) {
		this.dealDate = dealDate;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getBuyccy() {
		return buyccy;
	}

	public void setBuyccy(String buyccy) {
		this.buyccy = buyccy;
	}

	public double getBuyAmount() {
		return buyAmount;
	}

	public void setBuyAmount(double buyAmount) {
		this.buyAmount = buyAmount;
	}

	public String getSellccy() {
		return sellccy;
	}

	public void setSellccy(String sellccy) {
		this.sellccy = sellccy;
	}

	public double getSellAmount() {
		return sellAmount;
	}

	public void setSellAmount(double sellAmount) {
		this.sellAmount = sellAmount;
	}

	public String getContrRate() {
		return contrRate;
	}

	public void setContrRate(String contrRate) {
		this.contrRate = contrRate;
	}

	public String getFwdRate() {
		return fwdRate;
	}

	public void setFwdRate(String fwdRate) {
		this.fwdRate = fwdRate;
	}

	public String getCounterPartyname() {
		return counterPartyName;
	}

	public void setCounterPartyname(String counterPartyname) {
		this.counterPartyName = counterPartyname;
	}

}
