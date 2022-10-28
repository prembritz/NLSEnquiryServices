package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

@Path("/LoanSummary")
public class LoanSummary {

	private static DataSource cmDBPool;
	private static DecimalFormat amtFmt = new DecimalFormat("#,##0.00");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static HashMap<String, String> ActualTableName;

	private static String AcctTableRef = "ACCOUNT";
	private static String CategoryRef = "CATEGORY";
	private static String CustomerRef = "CUSTOMER";
	private static String AAAccountTableRef = "AA$ACCOUNT$DETAILS";
	private static String AAArrTermAmountRef = "AA$ARR$TERM$AMOUNT";
	private static String EBContractBalRef = "EB$CONTRACT$BALANCES";
	private static String PaymentScheduleRef = "PAYMENT$SHEDULE";

	public static void setDBPool(DataSource cmDBPool) {
		LoanSummary.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		LoanSummary.ActualTableName = ActualTableName;
	}
	
	@POST
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = LoanSummaryList.class, responseDescription = "Loan Summary Response", responseCode = "200")
	@Operation(summary = "Loan Summary Data Request", description = "returns Loan Summary data")
	public Response getLoanSummary(
			@RequestBody(description = "Loan Summary Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanSummaryRequests.class))) LoanSummaryRequests id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Loan Summary Interface Started on ["+startTime+"]");
			Connection dbConnection = cmDBPool.getConnection();

			String CustomerId = "";
			String AccountNumber = "";
			String UnitID = "";

			System.out.println("Loan Summary Table Ref [" + AcctTableRef + "," + CategoryRef + "," + ""
					+ AAArrTermAmountRef + "," + AAAccountTableRef + "," + PaymentScheduleRef + "," + EBContractBalRef + ", "+CustomerRef+"]");

			String AcctTable = "", CategoryTable = "", AAArrTermAmountTable = "", AAAccountTable = "",
					EBContractBalTable = "",PaymentScheduleTable="",CustomerTable="";

			LoanSummaryList LoanList = new LoanSummaryList();
			LoanSummaryObject loan = null;

			boolean LoanDataExists = false;
			String UnitIDs = "";
			int count = 0;
			ArrayList<Boolean>RespStatus=new ArrayList<Boolean>();
			for (int i = 0; i < id.loanAcctdata.size(); i++) {
				LoanDataExists = false;
				AccountNumber = id.loanAcctdata.get(i).loanAccnumber;
				CustomerId = id.loanAcctdata.get(i).cifRef;
				UnitID = id.loanAcctdata.get(i).uID;

				if (!UnitID.equals(UnitIDs)) {
					UnitIDs = UnitID;
					AcctTable = ActualTableName.get(UnitID + "-" + AcctTableRef);
					CategoryTable = ActualTableName.get(UnitID + "-" + CategoryRef);
					AAAccountTable = ActualTableName.get(UnitID + "-" + AAAccountTableRef);
					AAArrTermAmountTable = ActualTableName.get(UnitID + "-" + AAArrTermAmountRef);
					EBContractBalTable = ActualTableName.get(UnitID + "-" + EBContractBalRef);
					PaymentScheduleTable = ActualTableName.get(UnitID + "-" + PaymentScheduleRef);
					CustomerTable = ActualTableName.get(UnitID + "-" + CustomerRef);
					
					System.out.println("Loan Summary Table Names for Unit ID [" + UnitID + "],[" + AcctTable + "," + " "
							+ CategoryTable + "," + AAAccountTable + "," + AAArrTermAmountTable + "," + PaymentScheduleTable + "," + ""
							+ EBContractBalTable + "]");
				}

				System.out.println("Fetching Loan Summary Data For CIF [ " + CustomerId + " ]");
				System.out.println("Fetching Loan Summary Data For UnitID [ " + UnitID + " ]");
				System.out.println("Fetching Loan Summary Data For Product Code[ " + id.proCode + " ]");
				System.out.println("Fetching Loan Summary Data For Loan Account Number[ " + AccountNumber + " ]");

				try (PreparedStatement CustPs=dbConnection.prepareStatement("select COUNTRY from "+CustomerTable+" where id=?");
						PreparedStatement dbSt = dbConnection.prepareStatement("SELECT arrangment_id,COMPANY_ID,"
						+ " CURRENCY,ID,CATEGORY FROM " + AcctTable + " WHERE CUSTOMER_ID=? AND ID =? ")) {
					dbSt.setString(1, CustomerId);
					dbSt.setString(2, AccountNumber);

					try (ResultSet dbRs = dbSt.executeQuery()) {
						if (dbRs.next()) {

							loan = new LoanSummaryObject();
							RespStatus.add(true);				
							if (++count == 1)
								LoanList.setUnId(UnitID);

							loan.setLoanCur(dbRs.getString("CURRENCY"));
							loan.setLoanAccnumber(dbRs.getString("ID"));
							loan.setLoanTypedesc(DescriptionPick(dbConnection, dbRs.getString("CATEGORY")));
							loan.setLnType(dbRs.getString("CATEGORY"));
							String TermDetails[]=TermAmount(dbConnection, dbRs.getString("arrangment_id"),
									AAArrTermAmountTable);
							loan.setLoanAmt(Double.parseDouble(TermDetails[0]));
							Object[] loanBalanceCalc = loanBalanceCalc(dbConnection, dbRs.getString("ID"), AcctTable,
									EBContractBalTable, AAArrTermAmountTable, CategoryTable);
							loan.setLoanBal(Double.parseDouble(loanBalanceCalc[2].toString()));
							loan.setLoanbkgDate(getContractDate(PaymentScheduleTable,dbRs.getString("arrangment_id"),dbConnection));
							loan.setLoanclsedate(TermDetails[1]);
							
							CustPs.setString(1, CustomerId);
						    ResultSet CustRs=CustPs.executeQuery();
							if(CustRs.next())
							{
								loan.setCountry(CustRs.getString(1));	
							}
							else
							{
								loan.setCountry("");
							}
							CustRs.close();
							CustPs.clearParameters();
							
							LoanList.addLoanAccounts(loan);

						}
						else
						{
							RespStatus.add(LoanDataExists);
							ResponseMessages(LoanList,loan,UnitIDs,AccountNumber,
									ERROR_CODE.NOT_FOUND,ErrorResponseStatus.LOAN_ACCOUNT_NOT_FOUND.getValue());
							return Response.status(Status.ACCEPTED).entity(LoanList).build();
						}
						dbRs.close();				
					}
					catch (Exception e) {
						e.printStackTrace();
						RespStatus.add(LoanDataExists);
						ResponseMessages(LoanList,loan,UnitIDs,AccountNumber,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(LoanList).build();
					}
					dbSt.close();	
					CustPs.close();
				} catch (Exception e) {
					e.printStackTrace();
					RespStatus.add(LoanDataExists);
					ResponseMessages(LoanList,loan,UnitIDs,AccountNumber,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(LoanList).build();
				}
			}

			if(!RespStatus.contains(true))
			{
				LoanList.setErrCode(ERROR_CODE.NOT_FOUND);
				LoanList.setErrorDesc(ErrorResponseStatus.FAILURE.getValue());
			}

			return Response.status(Status.ACCEPTED).entity(LoanList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Loan Summary Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private String DescriptionPick(Connection dbConnection, String Category) throws SQLException

	{
		String Description = "";

		PreparedStatement CategoryPS = dbConnection.prepareStatement("select DESCRIPTION from category where id = ? ");
		CategoryPS.setString(1, Category);
		ResultSet CategoryRS = CategoryPS.executeQuery();
		if (CategoryRS.next()) {
			Description = CategoryRS.getString(1);
		}
		CategoryPS.close();
		CategoryRS.close();

		return Description;
	}
	
	/*public static LocalDate getAAMaturityDate(String schema, String arrangementReference, Connection dbConnection)
			throws SQLException {
		LocalDate matDate = null;
		String termAmountTable = "AA$ARR$TERM$AMOUNT";
		try (PreparedStatement dbSt = dbConnection
				.prepareStatement("SELECT * FROM " + schema + "." + termAmountTable + " WHERE ARRANGEMENT_ID = ?")) {
			dbSt.setString(1, arrangementReference);
			try (ResultSet dbRs = dbSt.executeQuery()) {
				while (dbRs.next() && dbRs.getDate("MATURITY_DATE") != null) {
					matDate = dbRs.getDate("MATURITY_DATE").toLocalDate();
				}
			}
		}
		return matDate;
	}*/
	
   private String[] TermAmount(Connection dbconnection, String ArrangementID, String AAArrTermAmountTable) {
		
		String TermDetails[] = new String[2];
		String DisbursementAmt="";
		String MaturityDate="";
		try {
			PreparedStatement ArrTermPS = dbconnection
					.prepareStatement("select amount,maturity_date from " + AAArrTermAmountTable + " "
							+ " where arrangement_id=? and rownum<=1 order by REGEXP_SUBSTR(id, '[^-]+$') desc");
			ArrTermPS.setString(1, ArrangementID);
			ResultSet ArrTermRS = ArrTermPS.executeQuery();
			if (ArrTermRS.next()) {
				DisbursementAmt = ArrTermRS.getString(1);
				MaturityDate = DateFormtter.format(TimeFormat.parse("" + ArrTermRS.getString(2)));
				//TermDetails[1] = ArrTermRS.getString(2);
			}
			ArrTermRS.close();
			ArrTermPS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TermDetails[0]=DisbursementAmt;
		TermDetails[1]=MaturityDate;
		
		 System.out.println("Disbursement Amount["+DisbursementAmt+"] maturityDate ["+MaturityDate+"]");

		return TermDetails;
	}

	/*private double DisbursementAmount(Connection dbconnection, String ArrangementID, String AAArrTermAmountTable) {
		
		Double DisbursementAmt = 0.0;

		try {
			PreparedStatement ArrTermPS = dbconnection.prepareStatement("select amount from " + AAArrTermAmountTable
					+ " " + " where arrangement_id=? order by REGEXP_SUBSTR(id, '[^-]+$') desc");
			ArrTermPS.setString(1, ArrangementID);
			ResultSet ArrTermRS = ArrTermPS.executeQuery();
			if (ArrTermRS.next()) {
				DisbursementAmt = ArrTermRS.getDouble(1);
			}
			ArrTermRS.close();
			ArrTermPS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("DisbursementcAmt [ " + DisbursementAmt + " ]");

		return DisbursementAmt;

	}*/

	public String getContractDate(String PaymentScheduleTable, String arrangementReference, Connection dbConnection)
			throws SQLException, ParseException {
		String contractDate = null;
		try (PreparedStatement dbSt = dbConnection
				.prepareStatement("SELECT BASE_DATE FROM " +PaymentScheduleTable+ " WHERE ARRANGEMENT_ID = ?")) {
			dbSt.setString(1, arrangementReference);
			try (ResultSet dbRs = dbSt.executeQuery()) {
				if (dbRs.next() && dbRs.getString("BASE_DATE") != null) {
					contractDate=DateFormtter.format(TimeFormat2.parse("" + dbRs.getString("BASE_DATE")));
				}
			}
		}
		System.out.println("Contract Date["+contractDate+"]");
		return contractDate;
	}

	public Vector<String> nlsSplit(String expression, String delimeter) {
		Vector<String> tokens = new Vector<String>();
		if (expression == null) {
			tokens.add("");
			return tokens;
		}
		int index = 0;
		String tempString = "";
		while ((index = expression.indexOf(delimeter)) != -1) {
			tempString = expression.substring(0, index);
			expression = expression.substring(index + 1, expression.length());
			tokens.add(tempString);
		}
		tokens.add(expression);
		return tokens;
	}

	public Double GetECBAmount(Connection dbconnection, String Account_no) throws SQLException {

		Double OpeningBalance = 0.0;
		LocalDate curDate = LocalDate.now();
		String balType = null;
		final List<String> contractBalanceTypes = new ArrayList<String>();
		try (PreparedStatement ecbSt = dbconnection
				.prepareStatement("SELECT * FROM EB$CONTRACT$BALANCES WHERE ID = ?")) {
			ecbSt.setString(1, Account_no);
			try (ResultSet ecbRs = ecbSt.executeQuery()) {
				if (ecbRs.next()) {
					if (ecbRs.getString("CURR_ASSET_TYPE") != null && !ecbRs.getString("CURR_ASSET_TYPE").isEmpty()) {
						Arrays.asList(ecbRs.getString("CURR_ASSET_TYPE").split("\\^")).forEach(balance -> {
							if (!balance.startsWith("CUR")
									&& !(balance.startsWith("ACC") && balance.contains("PRINCIPALINT"))
									&& !balance.equals("ACCOUNT") && !balance.contains("COMMITMENT")) {
								System.out.println("asset types =" + balance);
								if (!contractBalanceTypes.contains(balance)) {
									contractBalanceTypes.add(balance);
								}
							}
						});
					}

					for (int balTypeI = 0; balTypeI < contractBalanceTypes.size(); balTypeI++) {
						balType = contractBalanceTypes.get(balTypeI);
						// System.out.println("balType="+balType);
						OpeningBalance = getECBBalanceMovements(balType, curDate, curDate, curDate,
								ecbRs.getString("TYPE_SYSDATE"), ecbRs.getString("CURR_ASSET_TYPE"),
								ecbRs.getString("OPEN_BALANCE"), ecbRs.getString("CREDIT_MVMT"),
								ecbRs.getString("DEBIT_MVMT"), true);
					}
				}
			}

		}
		// System.out.println("OpeningBalance="+OpeningBalance);
		return OpeningBalance;
	}

	public static double getECBBalanceMovements(String balType, LocalDate openDateObj, LocalDate curDate,
			LocalDate lastCapDateObj, String typeSysDate, String assetType, String openBalString, String credMvmt,
			String debMvmt, boolean containsOrEquals) {
		Vector<String> typeDates = split(typeSysDate, "^");
		Vector<String> types = split(assetType, "^");
		Vector<String> openBals = split(openBalString, "^");
		Vector<String> credMvmts = split(credMvmt, "^");
		Vector<String> debtMvmts = split(debMvmt, "^");
		int noOfMovements = typeDates.size();
		String curType = null;
		String typeDate = null;
		double currentOpBalance = 0;
		String opBal = null;
		String crMv = null;
		String drMv = null;
		for (int dateI = 0; dateI < noOfMovements; dateI++) {
			curType = types.get(dateI);
			if (curType.toUpperCase().contains(balType.toUpperCase()) && !curType.startsWith("UNC")
					&& !curType.split("\\-")[0].endsWith("SP")) {

				// System.out.println("Currenttype ="+curType);
				typeDate = typeDates.get(dateI);
				opBal = openBals.get(dateI);
				crMv = credMvmts.get(dateI);
				drMv = debtMvmts.get(dateI);

				// System.out.println("opBal ="+opBal);
				// System.out.println("crMv ="+crMv);
				// System.out.println("drMv ="+drMv);
				// System.out.println("typeDate="+typeDate);
				/*
				 * balance type matched check the date component
				 */

				if (typeDate.indexOf("-") == -1
						&& ((containsOrEquals && typeDate.toUpperCase().contains(balType))
								|| (!containsOrEquals && typeDate.toUpperCase().equals(balType)))

						&& opBal != null) {
					// System.out.println("openingbalance="+Double.parseDouble(opBal));
					currentOpBalance += Double.parseDouble(opBal);
				} else {
					if (crMv != null && crMv.length() > 0) {
						// System.out.println("crMv="+Double.parseDouble(crMv));
						currentOpBalance += Double.parseDouble(crMv);
					}
					if (drMv != null && drMv.length() > 0) {
						// System.out.println("drMv="+Double.parseDouble(drMv));
						currentOpBalance += Double.parseDouble(drMv); // -= Math.abs(Double.parseDouble(drMv));
					}

				}
			}
		}
		// System.out.println("currentOpBalance="+currentOpBalance);
		return Math.abs(currentOpBalance);

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

	/*
	 * public double getBalance(Vector<String> Balance, Vector<String>
	 * TypeofAccount, Vector<String> debitmvt, Vector<String> creditmvt) { String[]
	 * type = new String[] { "CURACCOUNT", "DUEACCOUNT", "NORACCOUNT", "WT1ACCOUNT",
	 * "WT3ACCOUNT", "SS2ACCOUNT", "DA1ACCOUNT", "DA2ACCOUNT", "LS1ACCOUNT",
	 * "SS4ACCOUNT", "SS1ACCOUNT", "SS3ACCOUNT", "UNCACCOUNT" }; double bal = 0.0;
	 * int size = 0; size = TypeofAccount.size() < Balance.size() ?
	 * TypeofAccount.size() : Balance.size(); int i = 0; while (i < size) { String[]
	 * myType = TypeofAccount.get(i).toString().split("\\-"); if
	 * (Arrays.asList(type).contains(myType[0])) { bal += Double
	 * .parseDouble(Balance.get(i).toString().equals("") ? "0.0" :
	 * Balance.get(i).toString()) + Double.parseDouble(debitmvt.get(i).toString()
	 * .equals("") ? "0.0" : debitmvt.get(i) .toString()) +
	 * Double.parseDouble(creditmvt.get(i).toString() .equals("") ? "0.0" :
	 * creditmvt.get(i) .toString()); } ++i; } return Math.abs(bal); }
	 */

	/*
	 * public double[] otherBalance(Vector<String> Balance, Vector<String>
	 * TypeofAccount) { String[] type = new String[] { "BLTOTCOMMITMENT",
	 * "CURCOMMITMENT", "CURACCOUNT", "ACCPRINCIPALINT", "ACCPENALTYINT",
	 * "ACCPENALTYINT-", "ACCPRINCIPALINT-" }; double[] bal = new double[7]; int
	 * size = 0; size = TypeofAccount.size() < Balance.size() ? TypeofAccount.size()
	 * : Balance.size();
	 * 
	 * int i = 0; String typeofacct = ""; while (i < size) {
	 * 
	 * if (TypeofAccount.get(i).contains("-")) { typeofacct =
	 * TypeofAccount.get(i).substring(0, TypeofAccount.get(i).indexOf("-")); } else
	 * { typeofacct = TypeofAccount.get(i); } if
	 * (Arrays.asList(type).contains(typeofacct)) {
	 * 
	 * bal[Arrays.asList(type).indexOf(typeofacct)] = Double
	 * .parseDouble(Balance.get(i).toString().equals("") ? "0.0" :
	 * Balance.get(i).toString());
	 * 
	 * }
	 * 
	 * ++i; }
	 * 
	 * return bal; }
	 */

	public static Object[] loanBalanceCalc(Connection dbConnection, String AccountId, String AccTable, String ECBTable,
			String AAArrTermAmountTable, String CategoryTable) {

		double loanSum = 0;
		Object[] returnObject = null;

		try {
			PreparedStatement loanDetSt = dbConnection
					.prepareStatement("select a.ID,a.ARRANGMENT_ID," + " b.DESCRIPTION from " + AccTable + " a, " + " "
							+ CategoryTable + " b where a.id = '" + AccountId + "' and a.CATEGORY = b.ID ");
			ResultSet loanDetRs = loanDetSt.executeQuery();

			if (loanDetRs.next()) {
				returnObject = new Object[4];

				String arrangement = loanDetRs.getString("ARRANGMENT_ID");
				String account = loanDetRs.getString("ID");
				String Description = loanDetRs.getString("DESCRIPTION");

//	                System.out.println("Loan Processing: [" + arrangement + "] [" + account + "] [" + Description + "]");

				/*
				 * calculate contract balances
				 */
				Vector<String> Balance = new Vector<String>();
				Vector<String> type = new Vector<String>();
				double principle = 0.0D;

				Double Total_Outstanding;
				Double TotalPriIntSus;
				Double TotalPenaltyIntSus;
				Double TotalPriInt;
				Double TotalPenaltyInt;

				PreparedStatement ps_Principle = dbConnection.prepareStatement("SELECT ID,TYPE_SYSDATE, "
						+ " CURR_ASSET_TYPE,DEBIT_MVMT,CREDIT_MVMT,OPEN_BALANCE " + " FROM " + ECBTable
						+ " where OPEN_BALANCE " + " is not null and CURR_ASSET_TYPE is not null and id= ?");
				ps_Principle.setString(1, account);
				ResultSet rs_principle = ps_Principle.executeQuery();
				double Other_Balance[] = new double[7];
				if (rs_principle.next()) {

//	                    System.out.println("Loan Processing 1: [" + rs_principle.getString("OPEN_BALANCE") + "]");
					double pdamnt = 0.0D;
					Vector<String> opbalance = split(rs_principle.getString("OPEN_BALANCE"), "^");
					Vector<String> debitmvt = split(rs_principle.getString("DEBIT_MVMT"), "^");
					Vector<String> creditmvt = split(rs_principle.getString("CREDIT_MVMT"), "^");
					Balance = split(rs_principle.getString("OPEN_BALANCE"), "^");
					type = split(rs_principle.getString("TYPE_SYSDATE"), "^");

					principle = getBalance(Balance, type, debitmvt, creditmvt);
					Other_Balance = otherBalance(Balance, type);
					pdamnt = Other_Balance[0];
					if (pdamnt == 0.00) {
						PreparedStatement ps_aarrtermamount = dbConnection.prepareStatement("SELECT amount " + " FROM "
								+ AAArrTermAmountTable + " where id like ? order by ID FETCH NEXT 1 ROWS ONLY");
						ps_aarrtermamount.setString(1, arrangement + "%");
						ResultSet rs_aarrtermamount = ps_aarrtermamount.executeQuery();
						if (rs_aarrtermamount.next()) {
							pdamnt = Double.parseDouble(rs_aarrtermamount.getString("amount") == null ? "0.00"
									: rs_aarrtermamount.getString("amount"));
						}
						rs_aarrtermamount.close();
						ps_aarrtermamount.close();
					}

					Total_Outstanding = principle;
					double principalInterestDueSuspen = 0.00;
					double principalInterestSuspen = 0.00;
					double PenaltypalInterestDueSuspen = 0.00;
					double PenaltyInterestSuspen = 0.00;
					double PenaltyInterest = 0.00;
					double PenaltypalInterestDue = 0.00;
					double principalInterest = 0.00;
					double principalInterestDue = 0.00;

					if (opbalance.size() == type.size()) {
						for (int i = 0; i < opbalance.size(); i++) {
							if (!type.get(i).contains("SP")) {
								{
									if (type.get(i).substring(0, 2).equals("LS")
											|| type.get(i).substring(0, 2).equals("SS")
											|| type.get(i).substring(0, 2).equals("DA")
											|| type.get(i).substring(0, 3).equals("ESP")
											|| type.get(i).substring(0, 2).equals("WT")
											|| type.get(i).substring(0, 3).equals("NOR")
											|| type.get(i).startsWith("ACCPE")
											|| type.get(i).substring(0, 3).equals("DUE")) {

									}
								}
								if (type.get(i).contains("PRINCIPAL")) {
									if (type.get(i).substring(0, 2).equals("LS")
											|| type.get(i).substring(0, 2).equals("SS")
											|| type.get(i).substring(0, 2).equals("DA")
											|| type.get(i).substring(0, 3).equals("ESP")
											|| type.get(i).substring(0, 2).equals("WT")
											|| type.get(i).substring(0, 3).equals("NOR")) {
										String amt1 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
										String amtmvt1 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
										String amtcrt1 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
										principalInterestDue += Double.parseDouble(amt1) + Double.parseDouble(amtmvt1)
												+ Double.parseDouble(amtcrt1);
									}
									if (type.get(i).startsWith("ACCPRIN")) {
										String amt2 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
										String amtmvt2 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
										String amtcrt2 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
										principalInterest += Double.parseDouble(amt2) + Double.parseDouble(amtmvt2)
												+ Double.parseDouble(amtcrt2);

									}

								} else if (type.get(i).contains("PENALTY")) {
									if (type.get(i).substring(0, 2).equals("LS")
											|| type.get(i).substring(0, 2).equals("SS")
											|| type.get(i).substring(0, 2).equals("DA")
											|| type.get(i).substring(0, 3).equals("ESP")
											|| type.get(i).substring(0, 2).equals("WT")
											|| type.get(i).substring(0, 3).equals("NOR")) {
										String amt1 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
										String amtmvt1 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
										String amtcrt1 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
										PenaltypalInterestDue += Double.parseDouble(amt1) + Double.parseDouble(amtmvt1)
												+ Double.parseDouble(amtcrt1);
									}
									if (type.get(i).startsWith("ACCPENALTY")) {
										String amt2 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
										String amtmvt2 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
										String amtcrt2 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
										PenaltyInterest += Double.parseDouble(amt2) + Double.parseDouble(amtmvt2)
												+ Double.parseDouble(amtcrt2);
									}
								}
							} else if (type.get(i).contains("PRINCIPAL")) {
								if (type.get(i).substring(0, 2).equals("LS") || type.get(i).substring(0, 2).equals("SS")
										|| type.get(i).substring(0, 2).equals("DA")
										|| type.get(i).substring(0, 3).equals("ESP")
										|| type.get(i).substring(0, 2).equals("WT")
										|| type.get(i).substring(0, 3).equals("NOR")) {
									String amt1 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
									String amtmvt1 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
									String amtcrt1 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
									principalInterestDueSuspen += Double.parseDouble(amt1) + Double.parseDouble(amtmvt1)
											+ Double.parseDouble(amtcrt1);
								}
								if (type.get(i).startsWith("ACCPRIN")) {
									String amt2 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
									String amtmvt2 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
									String amtcrt2 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
									principalInterestSuspen += Double.parseDouble(amt2) + Double.parseDouble(amtmvt2)
											+ Double.parseDouble(amtcrt2);
								}
							} else if (type.get(i).contains("PENALTY")) {
								if (type.get(i).substring(0, 2).equals("LS") || type.get(i).substring(0, 2).equals("SS")
										|| type.get(i).substring(0, 2).equals("DA")
										|| type.get(i).substring(0, 3).equals("ESP")
										|| type.get(i).substring(0, 2).equals("WT")
										|| type.get(i).substring(0, 3).equals("NOR")) {
									String amt1 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
									String amtmvt1 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
									String amtcrt1 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
									PenaltypalInterestDueSuspen += Double.parseDouble(amt1)
											+ Double.parseDouble(amtmvt1) + Double.parseDouble(amtcrt1);
								}

								if (type.get(i).startsWith("ACCPENALTY")) {
									String amt2 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
									String amtmvt2 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
									String amtcrt2 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
									PenaltyInterestSuspen += Double.parseDouble(amt2) + Double.parseDouble(amtmvt2)
											+ Double.parseDouble(amtcrt2);
								}
							}
//	                            System.out.println("Loan Processing 2: [" + type.get(i) + "]");
						}

					}
					TotalPriIntSus = principalInterestSuspen + principalInterestDueSuspen;
					TotalPenaltyIntSus = PenaltypalInterestDueSuspen + PenaltyInterestSuspen;
					TotalPriInt = principalInterest + principalInterestDue;
					TotalPenaltyInt = PenaltyInterest + PenaltypalInterestDue;

					loanSum = Math.abs(Total_Outstanding) + Math.abs(TotalPriInt) + Math.abs(TotalPenaltyInt)
							+ Math.abs(TotalPenaltyInt) + Math.abs(TotalPenaltyIntSus) + Math.abs(TotalPriIntSus);

//	                    System.out.println("[ACCOUNT]  [ " + account + "] [ARRANGEMENT]  [ " + arrangement + "]  [BALANCE] [" + amtFmt.format(loanSum) + "]");

				}
				rs_principle.close();
				ps_Principle.close();

				returnObject[0] = account;
				returnObject[1] = arrangement;
				// returnObject[2] = amtFmt.format(loanSum);
				returnObject[2] = loanSum;
				returnObject[3] = Description;

//	                System.out.println("Loan Processing 3: [" + amtFmt.format(loanSum) + "]");

			}

			loanDetRs.close();
			loanDetSt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return returnObject;
	}

	public static double getBalance(Vector<String> Balance, Vector<String> TypeofAccount, Vector<String> debitmvt,
			Vector<String> creditmvt) {
		String type[] = { "CURACCOUNT", "DUEACCOUNT", "NORACCOUNT", "WT1ACCOUNT", "WT3ACCOUNT", "SS2ACCOUNT",
				"DA1ACCOUNT", "DA2ACCOUNT", "LS1ACCOUNT", "SS4ACCOUNT", "SS1ACCOUNT", "SS3ACCOUNT" };
		double bal = 0.0D;
		int size = 0;
		if (TypeofAccount.size() < Balance.size()) {
			size = TypeofAccount.size();
		} else {
			size = Balance.size();
		}

		for (int i = 0; i < size; i++) {
			String[] myType = TypeofAccount.get(i).toString().split("\\-");

			if (Arrays.asList(type).contains(myType[0])) {

				bal += Double.parseDouble(Balance.get(i).toString().equals("") ? "0.0" : Balance.get(i).toString())
						+ Double.parseDouble(debitmvt.get(i).toString().equals("") ? "0.0" : debitmvt.get(i).toString())
						+ Double.parseDouble(
								creditmvt.get(i).toString().equals("") ? "0.0" : creditmvt.get(i).toString());
			}
		}

		return bal;
	}

	public static double[] otherBalance(Vector<String> Balance, Vector<String> TypeofAccount) {
		String type[] = { "TOTCOMMITMENT", "CURCOMMITMENT", "CURACCOUNT", "ACCPRINCIPALINT", "ACCPENALTYINT",
				"ACCPENALTYINT-", "ACCPRINCIPALINT-" };
		double bal[] = new double[7];
		int size = 0;
		if (TypeofAccount.size() < Balance.size()) {
			size = TypeofAccount.size();
		} else {
			size = Balance.size();
		}

		for (int i = 0; i < size; i++) {
			if (Arrays.asList(type).contains(TypeofAccount.get(i).toString())) {
				bal[Arrays.asList(type).indexOf(TypeofAccount.get(i))] = Double
						.parseDouble(Balance.get(i).toString().equals("") ? "0.0" : Balance.get(i).toString());
			}
		}

		return bal;
	}

	private void ResponseMessages(LoanSummaryList LoanList,LoanSummaryObject loan,String UnitIDs,String AccountNumber,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		loan = new LoanSummaryObject();
		loan.setLoanAccnumber(AccountNumber);		
		loan.setErCode(ErrorCode);
		loan.setErMsg(ErrorDescription);
		LoanList.setUnId(UnitIDs);
		LoanList.addLoanAccounts(loan);
	}
}
