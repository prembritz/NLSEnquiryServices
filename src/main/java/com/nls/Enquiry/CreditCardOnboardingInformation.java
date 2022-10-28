package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;

public class CreditCardOnboardingInformation {

	@JsonbProperty("UnitID")
	private String unitID;
	
	@JsonbProperty("CardID")
	private String cardID;
	
	@JsonbProperty("CardCurrency")
	private String cardCur;
	
	@JsonbProperty("BranchCode")
	private String branchCode;
	
	@JsonbProperty("CardHolderName")
	private String cardholder;
	
	@JsonbProperty("OpenDate")
	private String OpenDt;
	
	@JsonbProperty("CustomerID")
	private String CustID;
	
	@JsonbProperty("ShadowAccount")
	private String ShadowAcc;
	
	@JsonbProperty("CardType")
	private String cardType;
	
	@JsonbProperty("ExpiryDate")
	private String expDate;
	
	@JsonbProperty("PrimaryFlag")
	private String prmyFlag;
	
	@JsonbProperty("CardStatus")
	private String cardStat;
	
	@JsonbProperty("Country")
	private String country;
	
	@JsonbProperty("CardCategory")
	private String cardCat;

	public String getUnitID() {
		return unitID;
	}

	public void setUnitID(String unitID) {
		this.unitID = unitID;
	}

	public String getCardID() {
		return cardID;
	}

	public void setCardID(String cardID) {
		this.cardID = cardID;
	}

	public String getCardCur() {
		return cardCur;
	}

	public void setCardCur(String cardCur) {
		this.cardCur = cardCur;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getCardholder() {
		return cardholder;
	}

	public void setCardholder(String cardholder) {
		this.cardholder = cardholder;
	}

	public String getOpenDt() {
		return OpenDt;
	}

	public void setOpenDt(String openDt) {
		OpenDt = openDt;
	}

	public String getCustID() {
		return CustID;
	}

	public void setCustID(String custID) {
		CustID = custID;
	}

	public String getShadowAcc() {
		return ShadowAcc;
	}

	public void setShadowAcc(String shadowAcc) {
		ShadowAcc = shadowAcc;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public String getPrmyFlag() {
		return prmyFlag;
	}

	public void setPrmyFlag(String prmyFlag) {
		this.prmyFlag = prmyFlag;
	}

	public String getCardStat() {
		return cardStat;
	}

	public void setCardStat(String cardStat) {
		this.cardStat = cardStat;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCardCat() {
		return cardCat;
	}

	public void setCardCat(String cardCat) {
		this.cardCat = cardCat;
	}
	
}
