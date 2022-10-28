package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Vector;

import javax.sql.DataSource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;
import org.eclipse.microprofile.opentracing.Traced;

@Path("/DepositDetails")
public class DepositDetailsFetching {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String AccountTable = "ACCOUNT";
	private static String AA_TableName = "AA_ARRANGEMENT";
	private static String AATermTableNameTable = "AA$ARR$TERM$AMOUNT";
	private static String AAAccountDetailsTable = "AA$ACCOUNT$DETAILS";
	private static String AAArrInterstTable = "AA$ARR$INTEREST";
	private static String accountDetailsTable = "AA$ACCOUNT$DETAILS";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static void setDBPool(DataSource cmDBPool) {
		DepositDetailsFetching.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		DepositDetailsFetching.ActualTableName = ActualTableName;
	}

	NumberFormat amountFormat = new DecimalFormat("#,##0.00");

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = DepositDetailsObject.class, responseDescription = "Deposit Details Response", responseCode = "200")
	@Operation(summary = "Deposit Details Request", description = "returns Deposit Details data")
	public Response getDepositDetails(
			@RequestBody(description = "Deposit Details Details Id", 
			required = true, content = @Content(mediaType = "application/json", 
			schema = @Schema(implementation = DepositDetailsRequest.class)))
			DepositDetailsRequest id) {
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Deposit Details Interface Started on ["+startTime+"]");
			String unitId=id.unitId;
			String accno=id.depositAccNo;
			System.out.println("Fetching Deposit Details Request Fields For Procode: [ " + id.proCode + " ] UnitId: [ "
					+ unitId + " ]" + " Deposittype: [ " + id.depositType + " ] DepositAccNo: [ " + accno
					+ " ]");

			double depositAmount = 0.0;
			double interest = 0.0;
			long tenor1 = 0;
			long tenor2 = 0;
			boolean flag = false;   

			DepositDetailsObject deposit = new DepositDetailsObject();
			System.out.println("DepositDetailsFetching Table Ref's [ " + unitId + " ] [" + AccountTable + "] ["
					+ AA_TableName + "] [" + AATermTableNameTable + "] [" + AAAccountDetailsTable + "] ["
					+ AAArrInterstTable + "] [" + accountDetailsTable + "]");

			String accountTable = ActualTableName.get(unitId + "-" + AccountTable);
			String aaArrangement_Table = ActualTableName.get(unitId + "-" + AA_TableName);
			String aaTermTable = ActualTableName.get(unitId + "-" + AATermTableNameTable);
			String aaAcountDetailsTable = ActualTableName.get(unitId + "-" + AAAccountDetailsTable);
			String aaArrInterstTable = ActualTableName.get(unitId + "-" + AAArrInterstTable);
			String accDetailTab = ActualTableName.get(unitId + "-" + accountDetailsTable);

			System.out.println("DepositDetailsFetching Actual Table Names [ " + unitId + " ] [" + accountTable
					+ "] [" + aaArrangement_Table + "] [" + aaTermTable + "] [" + aaAcountDetailsTable + "] ["
					+ aaArrInterstTable + "]");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection
							.prepareStatement("SELECT ID,ARRANGMENT_ID FROM " + accountTable + " WHERE ID=?")) {
				dbSt.setString(1, accno);

				try (ResultSet dbRs = dbSt.executeQuery()) {
					if (dbRs.next()) {
						flag = true;
						deposit.setUnitId(unitId);
						deposit.setDepositAccNo(accno);

						String Details[] = getArrangementDetails(dbConnection, dbRs.getString("ARRANGMENT_ID"),
								aaArrangement_Table);
						deposit.setDepositcurrency(Details[0]);
						deposit.setDepositType(Details[1]);
						deposit.setStartDate(Details[2]);
						deposit.setDepositPrincipalAccountNumber(Details[3]);
						deposit.setInterestSettlementAccount(Details[4]);
						deposit.setFundingAccountNumber(Details[5]);

						String TermDetails[] = TermAmount(dbConnection, dbRs.getString("ARRANGMENT_ID"), aaTermTable);
						System.out.println("OriginalPrincipalAmount =[" + TermDetails[0] + "]");
						deposit.setOriginalPrincipalAmount(Double.parseDouble(TermDetails[0]));
						depositAmount = Double.parseDouble(TermDetails[0]);

						LocalDate contractDate = getContractDate(dbRs.getString("ARRANGMENT_ID"), dbConnection,
								accDetailTab);

						if (contractDate != null && TermDetails[1] != null) {
							deposit.setTenor(ChronoUnit.DAYS.between(contractDate,
									LocalDate.parse(TermDetails[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"))) + "D");
							tenor1 = ChronoUnit.DAYS.between(contractDate,
									LocalDate.parse(TermDetails[1], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
						} else {
							deposit.setTenor("0D");
							tenor1 = 0;
						}
						if (contractDate != null) {
							LocalDate today = LocalDate.now();
							tenor2 = ChronoUnit.DAYS.between(contractDate, today);
						} else {
							tenor2 = 0;
						}

						deposit.setMaturityDate(TermDetails[1]);

						interest = getInterestRate(dbConnection, dbRs.getString("ARRANGMENT_ID"), aaArrInterstTable);
						deposit.setRateofInterest(interest);

						System.out.println("DepositAmount: [" + depositAmount + "] Interest: [" + interest
								+ "] Tenor1: [" + tenor1 + "]");
						System.out.println("DepositAmount: [" + depositAmount + "] Interest: [" + interest
								+ "] Tenor2: [" + tenor2 + "]");
						deposit.setMaturityAmount(Math.abs(depositAmount * interest * tenor1));
						deposit.setInterestonMaturity(Math.abs(depositAmount * interest * tenor2));
					}

					if (!flag) {
						ResponseMessages(deposit,unitId,accno,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(deposit).build();
					}
					dbRs.close();
				}catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(deposit,unitId,accno,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(deposit).build();
				}

				dbSt.clearParameters();
				dbSt.close();
				
			} catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(deposit,unitId,accno,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(deposit).build();
			}
			return Response.status(Status.ACCEPTED).entity(deposit).build();
		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Deposit Details Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private String[] TermAmount(Connection dbconnection, String ArrangementID, String AAArrTermAmountTable) {
		String TermDetails[] = new String[2];
		try {
			String amount = "0.0";
			String maturity = "";
			PreparedStatement ArrTermPS = dbconnection
					.prepareStatement("select amount,maturity_date from " + AAArrTermAmountTable + " "
							+ " where arrangement_id=? and rownum<=1 order by REGEXP_SUBSTR(id, '[^-]+$') desc");
			ArrTermPS.setString(1, ArrangementID);
			ResultSet ArrTermRS = ArrTermPS.executeQuery();
			if (ArrTermRS.next()) {
				if (ArrTermRS.getString(1) != null && !ArrTermRS.getString(1).equals(""))
					amount = ArrTermRS.getString(1);
				if (ArrTermRS.getString(2) != null && !ArrTermRS.getString(2).equals(""))
					maturity = TimeFormat.format(TimeFormat.parse("" + ArrTermRS.getString(2)));
				else {
					LocalDate today = LocalDate.now();
					maturity = "" + today;
				}
			}
			ArrTermRS.close();
			ArrTermPS.close();

			TermDetails[0] = amount;
			TermDetails[1] = maturity;

			System.out.println("Amount [" + TermDetails[0] + "] MaturityDate [" + TermDetails[1] + "]");

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Term Amount [" + TermDetails[0] + "] MaturityDate = [" + TermDetails[1] + "]");

		return TermDetails;
	}

	public static LocalDate getContractDate(String arrangementReference, Connection dbConnection, String accDetailTab)
			throws SQLException {
		LocalDate contractDate = null;
		String contract = null;
		String contra="";
		try (PreparedStatement dbSt = dbConnection
				.prepareStatement("SELECT CONTRACT_DATE FROM " + accDetailTab + " WHERE ID = ?")) {
			dbSt.setString(1, arrangementReference);
			try (ResultSet dbRs = dbSt.executeQuery()) {
				if (dbRs.next()){
					if(dbRs.getString("CONTRACT_DATE") != null) {
						contract = dbRs.getString("CONTRACT_DATE");
						//System.out.println("INSIDE CONTRACT_DATE:"+contract);
						Vector<String> cdate = split(contract, "^");
						//contra=cdate.get(cdate.size()-1);
						contractDate = LocalDate.parse(cdate.lastElement(), DateTimeFormatter.ofPattern("yyyyMMdd"));
					}
				}
				dbRs.close();
			}
			dbSt.close();
		}
		//System.out.println("Contract Testing "+contra); 
		System.out.println("Contract Date [" + contractDate + "]");
		return contractDate;
	}
	

	private Double getInterestRate(Connection dbconnection, String ArrangementID, String AAArrInterestTable) {
		Double EffectiveRate = 0.0;

		try {
			PreparedStatement ArrInterestPS = dbconnection
					.prepareStatement("select REGEXP_SUBSTR(EFFECTIVE_RATE, '[^^]+$') as EFFECTIVE_RATE"
							+ " from "+AAArrInterestTable+" where arrangement_id = ? "
									+ " and rownum<=1 order by REGEXP_SUBSTR(id, '[^-]+$') desc ");
			ArrInterestPS.setString(1, ArrangementID);
			ResultSet ArrInterestRS = ArrInterestPS.executeQuery();
			if (ArrInterestRS.next()) {
				EffectiveRate = Double.parseDouble(ArrInterestRS.getString("EFFECTIVE_RATE"));
			}
			ArrInterestRS.close();
			ArrInterestPS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Effective Rate [ " + EffectiveRate + " ]");

		return EffectiveRate;
	}

	private String[] getArrangementDetails(Connection dbConnection, String arrangeID, String arrangeTable) {

		String details[] = new String[6];

		String Currency = "";
		String Product = "";
		String StartDate = "";
		String AccountNo = "";
		try {
			PreparedStatement arrangeStmt = dbConnection.prepareStatement(
					"SELECT ID,CURRENCY,PRODUCT,START_DATE,LINKED_APPL_ID FROM " + arrangeTable + " WHERE ID=?");
			arrangeStmt.setString(1, arrangeID);
			ResultSet arrangeRs = arrangeStmt.executeQuery();
			if (arrangeRs.next()) {
				Currency = arrangeRs.getString("CURRENCY");
				//Product = arrangeRs.getString("PRODUCT");
				if (arrangeRs.getString("PRODUCT").contains("FIX"))
					Product = "Fixed Deposit";
				else
					Product = "Call Deposit";
				StartDate = DateFormtter.format(TimeFormat2.parse("" + arrangeRs.getString("START_DATE")));
				AccountNo = arrangeRs.getString("LINKED_APPL_ID");
			}
			arrangeRs.close();
			arrangeStmt.clearParameters();
		} catch (Exception e) {

		}

		details[0] = Currency;
		details[1] = Product;
		details[2] = StartDate;
		details[3] = AccountNo;
		details[4] = AccountNo; 
		details[5] = AccountNo;

		return details;
	}

	public static Vector<String> split(String expression, String delimeter) {
		Vector<String> tokens = new Vector<String>();
		if (expression == null) {
			tokens.add("");
			return tokens;
		}
		int index = 0;
		String tempString = "";
		do {
			index = expression.indexOf(delimeter);
			if (index == -1) {
				break;
			} else {
				tempString = expression.substring(0, index);
				expression = expression.substring(index + 1, expression.length());
				tokens.add(tempString);
			}
		} while (true);
		tokens.add(expression);

		return tokens;
	}
	
	private void ResponseMessages(DepositDetailsObject deposit,String unitId,String accno,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		deposit.setUnitId(unitId);
		deposit.setDepositAccNo(accno);
		deposit.setErrCode(ErrorCode);
		deposit.setErrorDesc(ErrorDescription);
	}
}
