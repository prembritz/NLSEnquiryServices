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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

@Path("/DepositSummary")
public class DepositSummaryDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String AccountTable = "ACCOUNT";
	private static String AA_TableName = "AA_ARRANGEMENT";
	private static String AATermTableNameTable = "AA$ARR$TERM$AMOUNT";
	private static String AAAccountDetailsTable = "AA$ACCOUNT$DETAILS";
	private static String AAArrInterstTable = "AA$ARR$INTEREST";
	private static String accountDetailsTable = "AA$ACCOUNT$DETAILS";
	private static String AAInterestAccurals = "AA$INTEREST$ACCRUALS";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static void setDBPool(DataSource cmDBPool) {
		DepositSummaryDetails.cmDBPool = cmDBPool;

	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		DepositSummaryDetails.ActualTableName = ActualTableName;
	}

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = DepositSummaryList.class, responseDescription = "Deposit Summary Details Response", responseCode = "200")
	@Operation(summary = "Deposit Summary Details Request", description = "returns Deposit Summary Details data")
	public Response getDepositSummaryDetails(
			@RequestBody(description = "Deposit Summary Details Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepositSummaryRequest.class))) DepositSummaryRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Deposit Summary Details Interface Started on ["+startTime+"]");
			String startDate = "";
			double depositAmount = 0.0;
			double interest = 0.0;
			long tenor1 = 0;
			long tenor2 = 0;
			String unitids = "";
			String unitIdNumber = "";
			String cif = "";
			String accountNumber = "";
			boolean flag = false;
			double totalDepoistInterest = 0.0;
			double netInterest = 0.0;
			double wtx = 0.0;
			double totalDepositBalance = 0.0;

			String accountTable = "";
			String aaArrTableName = "";
			String aaTermTableNameTable = "";
			String aaAccountDetailsTable = "";
			String aaArrInterstTable = "";
			String accDetailTab = "";
			String arrangementValueID="";
			ArrayList<Boolean>RespStatus=new ArrayList<Boolean>();
			Connection dbConnection = cmDBPool.getConnection();
			DepositSummaryObject deposit = null;
			DepositSummaryList depoList = new DepositSummaryList();
			int j = 0;
			for (int i = 0; i < id.summaryData.size(); i++) {
				unitids = id.summaryData.get(i).unitId;
				cif = id.summaryData.get(i).cifRef;
				accountNumber = id.summaryData.get(i).depositAccNo;

				if (!unitids.equalsIgnoreCase(unitIdNumber)) {
					unitIdNumber = unitids;

					System.out.println("Deposit Summary Request Table Ref's [ " + unitIdNumber + " ] [" + AccountTable
							+ "] [" + AA_TableName + "] [" + AATermTableNameTable + "] [" + AAAccountDetailsTable
							+ "] [" + AAArrInterstTable + "] [" + AAInterestAccurals + "]");

					accountTable = ActualTableName.get(unitIdNumber + "-" + AccountTable);
					aaArrTableName = ActualTableName.get(unitIdNumber + "-" + AA_TableName);
					aaTermTableNameTable = ActualTableName.get(unitIdNumber + "-" + AATermTableNameTable);
					aaAccountDetailsTable = ActualTableName.get(unitIdNumber + "-" + AAAccountDetailsTable);
					aaArrInterstTable = ActualTableName.get(unitIdNumber + "-" + AAArrInterstTable);
					accDetailTab = ActualTableName.get(unitIdNumber + "-" + accountDetailsTable);
				//	accuralTab = ActualTableName.get(unitIdNumber + "-" + AAInterestAccurals);

					System.out.println("Deposit Summary Request Actual Table Names [ " + unitIdNumber + " ] ["
							+ accountTable + "] [" + aaArrTableName + "] [" + aaTermTableNameTable + "] ["
							+ aaAccountDetailsTable + "] [" + aaArrInterstTable + "]");
				}

				System.out.println("Fetching Deposit Summary Request Fields For Procode: [ " + id.proCode
						+ " ] UnitId: [ " + unitIdNumber + " ] " + "CifRef: [ " + cif + " ]" + " DepositAccNo [ "
						+ accountNumber + " ]");
				try (PreparedStatement dbSt = dbConnection
						.prepareStatement("SELECT * FROM " + accountTable + "  WHERE ID=?")) {
					dbSt.setString(1, accountNumber);

					try (ResultSet dbRs = dbSt.executeQuery()) {
						if (dbRs.next()) {
							flag = true;
							RespStatus.add(flag);
							deposit = new DepositSummaryObject();
							if (++j == 1)
							{
								depoList.setUnitId(unitIdNumber);
								//depoList.setDepAccNumber(accountNumber);
							}

							deposit.setDepositAccNo(accountNumber);
							arrangementValueID=dbRs.getString("arrangment_id");
 
							System.out.println("Arrangement ID=[" + arrangementValueID + "]");
							String Details[] = getArrangementDetails(dbConnection, arrangementValueID,
									aaArrTableName);
							deposit.setCurrency(Details[0]);
							deposit.setDepositTypeCode(Details[1]);
							deposit.setBookingDate(Details[2]);
							deposit.setDepositDescription(Details[3]);

							String TermDetails[] = TermAmount(dbConnection, arrangementValueID,
									aaTermTableNameTable);
							deposit.setDepositAmount(Double.parseDouble(TermDetails[0]));
							depositAmount = Double.parseDouble(TermDetails[0]);

							LocalDate contractDate = getContractDate(arrangementValueID, dbConnection,
									accDetailTab);

							if (contractDate != null && !TermDetails[1].equals("")) {
								deposit.setTenor(ChronoUnit.DAYS.between(contractDate,
										LocalDate.parse(TermDetails[1], DateTimeFormatter.ofPattern("yyyy-MM-dd")))
										+ "D");

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

							deposit.setMaturityDate(DateFormtter.format(TimeFormat.parse("" + TermDetails[1])));

							interest = getInterestRate(dbConnection, arrangementValueID,
									aaArrInterstTable);
							deposit.setRateofInterest(interest);

							System.out.println("MaturityAmount : " + depositAmount + " interest: " + interest
									+ " tenor1: " + tenor1);

							/*
							 * System.out.println("DepositBalance: " + depositAmount + " interest: " +
							 * interest + " tenor2: " + tenor2);
							 */

							deposit.setMaturityAmount(Math.abs(depositAmount * interest * tenor1));

							totalDepoistInterest = InterestCalculation.getDepositInterest(dbConnection, arrangementValueID,
									aaArrTableName, Details[1], "" + contractDate,
									DateFormtter.format(TimeFormat.parse(TermDetails[1])), Details[0],accountNumber); 

							// totalDepoistInterest =
							// getDepositeTotalInterest(dbConnection,arrangementValueID,
							// accuralTab);

							wtx = totalDepoistInterest * 15 / 100;
							netInterest = totalDepoistInterest - wtx;
							totalDepositBalance = depositAmount + netInterest;
							
							deposit.setDepositBalance(totalDepositBalance);

							depoList.addAccount(deposit);
						} else {
							RespStatus.add(flag);
							ResponseMessages(depoList,deposit,unitIdNumber,accountNumber,
									ERROR_CODE.NOT_FOUND,ErrorResponseStatus.ACCOUNT_NOT_FOUND.getValue());

						}

						dbRs.close();
					} catch (Exception e) {
						e.printStackTrace();
						RespStatus.add(flag);
						ResponseMessages(depoList,deposit,unitIdNumber,accountNumber,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					}

					dbSt.clearParameters();
					dbSt.close();
				} catch (Exception except) {
					except.printStackTrace();
					RespStatus.add(flag);
					ResponseMessages(depoList,deposit,unitIdNumber,accountNumber,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				}
			}
			
			if(!RespStatus.contains(true))
			{
				depoList.setErrCode(ERROR_CODE.NOT_FOUND);
				depoList.setErrorDesc(ErrorResponseStatus.FAILURE.getValue());
			}
			return Response.status(Status.ACCEPTED).entity(depoList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Deposit Summary Details Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private String[] TermAmount(Connection dbconnection, String ArrangementID, String AAArrTermAmountTable) {

		String TermDetails[] = new String[2];

		String amount = "0.0";
		String maturity = "";

		try {

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

		} catch (Exception e) {
			e.printStackTrace();
		}

		TermDetails[0] = amount;
		TermDetails[1] = maturity;

		System.out.println("Amount [" + TermDetails[0] + "] MaturityDate [" + TermDetails[1] + "]");

		return TermDetails;
	}

	public static LocalDate getContractDate(String arrangementReference, Connection dbConnection, String accDetailTab)
			throws SQLException {
		LocalDate contractDate = null;
		String contract = null;
		try (PreparedStatement dbSt = dbConnection
				.prepareStatement("SELECT REGEXP_SUBSTR(CONTRACT_DATE,'[^^]+$') AS CONTRACT_DATE FROM " + accDetailTab
						+ " WHERE ID = ?")) {
			dbSt.setString(1, arrangementReference);
			try (ResultSet dbRs = dbSt.executeQuery()) {
				if (dbRs.next()) {
					if (dbRs.getString("CONTRACT_DATE") != null) {
						contract = dbRs.getString("CONTRACT_DATE");
						contractDate = LocalDate.parse(contract, DateTimeFormatter.ofPattern("yyyyMMdd"));
					}
				}
				dbRs.close();
			}
			dbSt.close();
		}
		System.out.println("Contract Date [" + contractDate + "]");
		return contractDate;
	}

	private Double getInterestRate(Connection dbconnection, String ArrangementID, String AAArrInterestTable) {
		Double EffectiveRate = 0.0;

		try {
			PreparedStatement ArrInterestPS = dbconnection
					.prepareStatement("select REGEXP_SUBSTR(EFFECTIVE_RATE, '[^^]+$') as EFFECTIVE_RATE" + " from "
							+ AAArrInterestTable + " where arrangement_id = ? "
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

		String details[] = new String[4];

		String Currency = "";
		String Product = "";
		String StartDate = "";
		String ProductType = "";

		try {
			PreparedStatement arrangeStmt = dbConnection.prepareStatement(
					"SELECT ID,CURRENCY,PRODUCT,START_DATE,LINKED_APPL_ID FROM " + arrangeTable + " WHERE ID=?");
			arrangeStmt.setString(1, arrangeID);
			ResultSet arrangeRs = arrangeStmt.executeQuery();
			if (arrangeRs.next()) {
				Currency = arrangeRs.getString("CURRENCY");
				Product = arrangeRs.getString("PRODUCT");
				if (arrangeRs.getString("START_DATE") != null && !arrangeRs.getString("START_DATE").equals(""))
					StartDate = DateFormtter.format(TimeFormat2.parse("" + arrangeRs.getString("START_DATE")));

				if (arrangeRs.getString("PRODUCT").contains("FIX"))
					ProductType = "Fixed Deposit";
				else
					ProductType = "Call Deposit";
			}
			arrangeRs.close();
			arrangeStmt.clearParameters();

		} catch (Exception e) {

		}

		details[0] = Currency;
		details[1] = Product;
		details[2] = StartDate;
		details[3] = ProductType;

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
	
	private void ResponseMessages(DepositSummaryList depoList,DepositSummaryObject deposit,String unitIdNumber,String accountNumber,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		deposit = new DepositSummaryObject();
		deposit.setUnitId(unitIdNumber);
		deposit.setErCode(ErrorCode);
		deposit.setErMsg(ErrorDescription);
		deposit.setDepositAccNo(accountNumber);
		depoList.addAccount(deposit);
	}

}
