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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;
import org.eclipse.microprofile.opentracing.Traced;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Path("/LoanStatement")
public class LoanStatement {

	private static DataSource cmDBPool;
	// private static NumberFormat numberFormat = new DecimalFormat("#,##0.00");
	private static NumberFormat numberFormat = new DecimalFormat("###0.00");

	private static String AcctTableRef = "ACCOUNT";
	private static String AAActivityHistoryRef = "AA$ACTIVITY$HISTORY";
	private static String AABillDetailsRef = "AA$BILL$DETAILS";
	private static String AccActivityRef = "ACCT_ACTIVITY";
	private static String StmtEntryRef = "STATEMENT$ENTRY";

	LoanStatementList LnStatementList = new LoanStatementList();
	LoanStatementObject Lnstmt = null;
	int TotalTxn = 0;

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");
	private static HashMap<String, String> ActualTableName;

	private static Map<String, String> fldMap = new HashMap<String, String>();
	private static HashMap<String, String> allowedTransMap = new HashMap<String, String>();
	private static HashMap<String, String> allowedActivityMap = new HashMap<String, String>();
	private static List<String> accountBalanceTypes;
	static {

		fldMap.put("DAY_NO", "1");
		fldMap.put("TURNOVER_CREDIT", "2");
		fldMap.put("TURNOVER_DEBIT", "3");
		fldMap.put("BALANCE", "4");

		fldMap.put("BK_DAY_NO", "15");
		fldMap.put("BK_BALANCE", "16");
		fldMap.put("BK_CREDIT_MVMT", "17");
		fldMap.put("BK_DEBIT_MVMT", "18");

		accountBalanceTypes = Arrays.asList(new String[] { "CURACCOUNT", "DA1ACCOUNT", "DA2ACCOUNT", "DA3ACCOUNT",
				"DBTACCOUNT", "DUEACCOUNT", "LS1ACCOUNT", "LS2ACCOUNT", "LSSACCOUNT", "NOMACCOUNT", "NORACCOUNT",
				"SS1ACCOUNT", "SS2ACCOUNT", "SS3ACCOUNT", "SS4ACCOUNT", "SS5ACCOUNT", "STDACCOUNT", "WATACCOUNT",
				"WT1ACCOUNT", "WT2ACCOUNT", "WT3ACCOUNT" });

		// accountBalanceTypes = Arrays.asList(new String[] { "CURBALANCE" });

		allowedTransMap.put("865", "CREDIT");
		allowedActivityMap.put("LENDING-APPLYPAYMENT-PR.REPAYMENT", "Payment Received");
		allowedActivityMap.put("LENDING-SETTLE-PAYOFF", "Payment Received");
		allowedActivityMap.put("LENDING-CREDIT-ARRANGEMENT", "Payment Received");
		allowedActivityMap.put("LENDING-APPLYPAYMENT-PR.CURRENT.BALANCES", "Payment Received");
		allowedActivityMap.put("LENDING-APPLYPAYMENT-PR.ACCRUED.INTEREST1", "Payment Received"); // Payment Received and
																									// Penalty Interest
																									// Payment

		allowedActivityMap.put("LENDING-APPLYPAYMENT-PO.WITHDRAWAL", "Loan Disbursement");
		allowedActivityMap.put("LENDING-APPLYPAYMENT-PR.PRINCIPAL.DECREASE", "Principal Decrease"); // Principal
																									// Decrease and
																									// Payment Received
		allowedActivityMap.put("LENDING-DISBURSE-COMMITMENT", "Principal Increase");
		allowedActivityMap.put("LENDING-APPLYPAYMENT-PR.ACCRUED.INTEREST#@#TXN.AMOUNT", "Penalty Interest Payment");
	}

	public static void setDBPool(DataSource cmDBPool) {
		LoanStatement.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		LoanStatement.ActualTableName = ActualTableName;
	}

	// @Timeout (value=2, unit=ChronoUnit.SECONDS)
	@POST
	@Timeout(value = 10, unit = ChronoUnit.MINUTES)
	@Traced()
	@Counted()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = LoanStatementList.class, responseDescription = "Loan Statement Response", responseCode = "200")
	@Operation(summary = "Loan Statement Request", description = "returns Loan Statement")
	public Response getLoanStatement(
			@RequestBody(description = "Loan Account Number", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanStatementRequest.class))) LoanStatementRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Loan Statement Interface Started on ["+startTime+"]");
			String unitId = id.unId;
			String AccountNumber=id.loanAccnumber;
			String StDate=id.fromDate;
			String enDate=id.toDate;
			
			System.out.println("Fetching Loan Statement For Loan Account number [ " + AccountNumber
					+ " ] , From Date [ " + StDate + " ], To Date [ " + enDate + " ]");

			String dayFld = "BK_DAY_NO";
			String balanceField = "BK_BALANCE";

			System.out.println("Loan Statement Table Ref [" + AcctTableRef + "," + AAActivityHistoryRef + ","
					+ AccActivityRef + "," + StmtEntryRef + "," + AABillDetailsRef + "]");

			
			
			String AcctTable = ActualTableName.get(unitId + "-" + AcctTableRef);
			String AAActivityHistoryTable = ActualTableName.get(unitId + "-" + AAActivityHistoryRef);
			String AccActivityTable = ActualTableName.get(unitId + "-" + AccActivityRef);
			String StmtEntryTable = ActualTableName.get(unitId + "-" + StmtEntryRef);
			String AABillDetailsTable = ActualTableName.get(unitId + "-" + AABillDetailsRef);

			System.out.println("Loan Statement Table Names [" + AcctTable + "," + AAActivityHistoryTable + ","
					+ AccActivityTable + "," + StmtEntryTable + "," + AABillDetailsTable + "]");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection.prepareStatement("select ID,CURRENCY,ARRANGMENT_ID,"
							+ " OPENING_DATE from " + AcctTable + " WHERE id =? " + " and OPENING_DATE is not null ")) {

				dbSt.setString(1, AccountNumber);

				try (ResultSet dbRs = dbSt.executeQuery()) {
					boolean Exists = false;
					if (dbRs.next()) {

						// System.out.println("id ="+dbRs.getString("ID"));

						Exists = true;

						String StartDate = TimeFormat2.format(DateFormtter.parse("" + StDate));
						String EndDate = TimeFormat2.format(DateFormtter.parse("" + enDate));

						LocalDate openingDate = dbRs.getDate("OPENING_DATE").toLocalDate();

						// System.out.println("Start Date ="+StartDate+", End Date ="+EndDate);

						// Object
						// OpeningBalance[]=getOpeningBalance(dbConnection,id.LoanAccNumber,StartDate);
						// System.out.println("opening balance
						// ="+Double.parseDouble(OpeningBalance[1].toString()));

						double currentBalance = 0.00;
						for (String acctBalType : accountBalanceTypes) {
							currentBalance += Math.abs(getAccountActivityBalance(dbConnection, openingDate,
									AccountNumber, EndDate, dayFld, balanceField, acctBalType, unitId));
						}
						System.out.println("Current Balance Amount Including OD Amounts [ " + currentBalance + " ] ");

						LnStatementList.setUnID(unitId);
						LnStatementList.setLoanAccnumber(AccountNumber);
						LnStatementList.setOpenBal(currentBalance);

						currentBalance = getActivityHistory(dbConnection, dbRs.getString("ID"), StartDate, EndDate,
								dbRs.getString("ARRANGMENT_ID"), currentBalance, AAActivityHistoryTable,
								unitId);

						System.out.println(
								"Running Balance After Activity History [" + numberFormat.format(currentBalance) + "]");

						currentBalance = getBillGenerateStatement(dbConnection, dbRs.getString("ARRANGMENT_ID"),
								dbRs.getString("ID"), StartDate, EndDate, currentBalance, AABillDetailsTable, unitId);

						System.out.println("Running Balance After Bill Generate Statement ["
								+ numberFormat.format(currentBalance) + "]");
//		Vector<String> transReferStmt=transReferenceReversal(dbConnection,arrangementReference);
						currentBalance = generateStatement(dbConnection, AccountNumber, StartDate, EndDate, "KES",
								dbRs.getString("ARRANGMENT_ID"), currentBalance, StmtEntryTable, unitId);

						System.out.println("Running Balance After Generate Statement ["
								+ numberFormat.format(currentBalance) + "]");

						LnStatementList.setClosingBal(currentBalance);
						System.out.println("TotalTxn [" + TotalTxn + "]");

						if (TotalTxn == 0) {
							LnStatementList=new LoanStatementList();
							ResponseMessages(LnStatementList,unitId,AccountNumber,
									ERROR_CODE.NOT_FOUND,ErrorResponseStatus.STATEMENT_NOT_FOUND.getValue());
							return Response.status(Status.ACCEPTED).entity(LnStatementList).build();
						}
					}
					dbRs.close();
					if (!Exists) {
						LnStatementList=new LoanStatementList();
						ResponseMessages(LnStatementList,unitId,AccountNumber,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.LOAN_ACCOUNT_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(LnStatementList).build();
					}

				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					LnStatementList=new LoanStatementList();
					ResponseMessages(LnStatementList,unitId,AccountNumber,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(LnStatementList).build();
				}
				dbSt.close();

			} catch (Exception e) {
				e.printStackTrace();
				LnStatementList=new LoanStatementList();
				ResponseMessages(LnStatementList,unitId,AccountNumber,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(LnStatementList).build();
			}
			return Response.status(Status.ACCEPTED).entity(LnStatementList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Loan Statement Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}

	}

	public Double generateStatement(Connection dbConnection, String accountNumber, String startDate, String endDate,
			String localCurrency, String arrangementReference, Double CurrentBalance, String StmtEntryTable,
			String unitId) throws Exception {
		Vector<String[]> unappliedFunds = new Vector<String[]>();
		int tempStartDate = Integer.parseInt(startDate.replaceAll("-", ""));
		int tempEndDate = Integer.parseInt(endDate.replaceAll("-", ""));
		int bookingDate = 0;
		Double DebitAmt = 0.0;
		// String[] data = new String[6];
		PreparedStatement stmtStatement = dbConnection.prepareStatement("SELECT * FROM " + StmtEntryTable + " "
				+ " WHERE ACCOUNT_NUMBER = ? and CRF_TYPE='UNCACCOUNT' and TRANSACTION_CODE='865'");
		stmtStatement.setString(1, accountNumber);
		ResultSet stmtRecord = stmtStatement.executeQuery();
		while (stmtRecord.next()) {

//			String reversalEntries = null;
//			Map<String, String> reversalEntryMap = new TreeMap<String, String>();
//			for (String removeReversal : transReferStmt) {
//				reversalEntries = removeReversal;
//				reversalEntryMap.put(reversalEntries, "AUTH-REV");
//				// System.out.println(reversalEntryMap);
//			}
			bookingDate = Integer.parseInt(stmtRecord.getString("VALUE_DATE").replaceAll("-", "").substring(0, 8));
			if (tempStartDate <= bookingDate && bookingDate <= tempEndDate) {
				/*
				 * data[0] = stmtRecord.getString("VALUE_DATE").replaceAll("-", "").substring(0,
				 * 8); data[1] = stmtRecord.getString("TRANSACTION_CODE").trim(); data[2] =
				 * "Unapplied Funds"; data[3] = "DEBIT"; data[4] =
				 * stmtRecord.getString("AMOUNT_LCY"); data[5] = stmtRecord.getString("ID");
				 * 
				 * unappliedFunds.add(data);
				 */

				Lnstmt = new LoanStatementObject();

				Lnstmt.setTransDate(TimeFormat.format(DateFormtter.parse("" + stmtRecord.getString("VALUE_DATE"))));
				Lnstmt.setTransCode(
						TransactionDescription(dbConnection, stmtRecord.getString("TRANSACTION_CODE").trim(), unitId));
				Lnstmt.setNarr("Unapplied Funds");
				Lnstmt.setCrfType("DEBIT");
				// DebitAmt = Math.abs(stmtRecord.getDouble("AMOUNT_LCY"));
				CurrentBalance = (Double.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
						- stmtRecord.getDouble("AMOUNT_LCY"));
				Lnstmt.setDebtAmt(stmtRecord.getDouble("AMOUNT_LCY"));
				Lnstmt.setCredAmt(0.0);
				Lnstmt.setBal(CurrentBalance);
				Lnstmt.setRefNo(stmtRecord.getString("ID"));
				Lnstmt.setTransSeqno(++TotalTxn);
				LnStatementList.addAccount(Lnstmt);

			}
		}

		/*
		 * for (String[] entry : unappliedFunds) {
		 * System.out.println(Arrays.asList(entry).stream().collect(Collectors.joining(
		 * ";"))); }
		 */

		return CurrentBalance;
	}

	private Double getBillGenerateStatement(Connection dbConnection, String arrangementReference, String accountNumber,
			String startDate, String endDate, Double CurrentBalance, String AABillDetailsTable, String unitId)
			throws Exception {

		Vector<String[]> billStatement = new Vector<String[]>();
		int tempStartDate = Integer.parseInt(startDate);
		int tempEndDate = Integer.parseInt(endDate);
		Vector<String> payProertiy = null;
		Vector<String> repayAmount = null;
		Vector<String> repayRef = null;
		Vector<String> billDate = null;
		Vector<String> or_pr_amt = null;
		Vector<String> os_pr_amt = null;
		String repayReference = null;
		String amount = null;
		String paymentMethod = null;
		// System.out.println("arrangementReference ["+arrangementReference+"]");
		System.out.println(
				"SELECT * FROM " + AABillDetailsTable + " where ARRANGEMENT_ID='" + arrangementReference + "'");
		PreparedStatement billDetailsPS = dbConnection
				.prepareStatement("SELECT  ID,REPAY_REF,REPAY_AMOUNT,PAYMENT_METHOD,"
						+ " PAY_PROPERTY,OR_PR_AMT,OS_PR_AMT  FROM " + AABillDetailsTable + " where ARRANGEMENT_ID=?");
		billDetailsPS.setString(1, arrangementReference);
		ResultSet dbRs = billDetailsPS.executeQuery();
		while (dbRs.next()) {

			repayReference = dbRs.getString("REPAY_REF");
			amount = dbRs.getString("REPAY_AMOUNT");
			paymentMethod = dbRs.getString("PAYMENT_METHOD");
			if (repayReference != null && amount != null && !paymentMethod.contains("PAY")) {

				repayRef = split(dbRs.getString("REPAY_REF"), "^");
				repayAmount = split(dbRs.getString("REPAY_AMOUNT"), "^");
				payProertiy = split(dbRs.getString("PAY_PROPERTY"), "^");
				or_pr_amt = split(dbRs.getString("OR_PR_AMT"), "^");
				os_pr_amt = split(dbRs.getString("OS_PR_AMT"), "^");

				int tempDate;
				// System.out.println("repayReference="+repayReference);
				String DebitAmt = "";
				String CreditAmt = "";
				String repayPreperty = null;
				int i = 0;
				for (int k = 0; k < payProertiy.size(); k++) {

					repayPreperty = payProertiy.get(k);
					// System.out.println("Pay_Preperty [" + repayPreperty + "]");
					if (repayPreperty.equals("ACCOUNT")) {
						double osPrAmount = 0.0, repay = 0.0;
						osPrAmount = Math.abs(Double.parseDouble(or_pr_amt.get(k)))
								- Math.abs(Double.parseDouble(os_pr_amt.get(k)));
						String orPrAmountAccount = String.valueOf(numberFormat.format(osPrAmount));
						System.out.println("i=" + i);

						ACCOUNT: for (int j = i; j < repayAmount.size(); j++) {
							// System.out.println("orPrAmountAccount["+orPrAmountAccount+"],["+repayAmount.get(j)+"]");
							if (!orPrAmountAccount.equals("0.00")) {
								// if (!orPrAmountAccount.equals("0.00")) {
								if (!repayAmount.get(j).isEmpty() && !repayRef.get(j).isEmpty()) {
									if (!repayRef.get(j).contains("SUSPEND") && repayRef.get(j) != null
											&& (!repayAmount.get(j).isEmpty() || repayAmount.get(j).equals("0"))) {
										try {
											repay = Math.abs(repay) + Math.abs(Double.parseDouble(repayAmount.get(j)));
										} catch (Exception e) {
											repay = 0.00;
										}
										String repayValues = String.valueOf(numberFormat.format(repay));

										tempDate = Integer.parseInt(repayRef.get(j).split("-")[1]);
										if (tempStartDate <= tempDate && tempDate <= tempEndDate) {

											/*
											 * String[] data = new String[6]; data[0] = repayRef.get(j).split("-")[1];
											 * data[1] = "865"; data[2] = "Principal Payment"; data[3] = "CREDIT";
											 * data[4] = repayAmount.get(j); data[5] = repayRef.get(j).split("-")[0];
											 * 
											 * billStatement.add(data);
											 */

											Lnstmt = new LoanStatementObject();

											Lnstmt.setTransDate(TimeFormat2
													.format(DateFormtter.parse("" + repayRef.get(j).split("-")[1])));
											Lnstmt.setTransCode(TransactionDescription(dbConnection, "865", unitId));
											Lnstmt.setNarr("Principal Payment");
											Lnstmt.setCrfType("CREDIT");
											CreditAmt = repayAmount.get(j);
											CurrentBalance = Double
													.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
													+ Math.abs(Double.parseDouble(CreditAmt));
											Lnstmt.setCredAmt(Double.parseDouble(CreditAmt));
											Lnstmt.setDebtAmt(0.0);
											Lnstmt.setBal(CurrentBalance);
											try {
												Lnstmt.setRefNo(repayRef.get(j).split("-")[0]);
											} catch (Exception ex) {
												Lnstmt.setRefNo(repayRef.get(j).split("-")[0]);
											}
											Lnstmt.setTransSeqno(++TotalTxn);
											LnStatementList.addAccount(Lnstmt);

										} else {
											i++;
										}

										if (repayValues.equals(orPrAmountAccount)) {
											i++;
											break ACCOUNT;
										} else {
											i++;
										}
									} else {
										i++;
									}
								} else {
									i++;
									break ACCOUNT;
								}

							} else {
								i++;
								break ACCOUNT;
							}

						}
					} else if (repayPreperty.equals("PRINCIPALINT")) {
						// System.out.println("Principal Interst");
						double osPrAmount = 0.0, repay = 0.0;
						osPrAmount = Math.abs(Double.parseDouble(or_pr_amt.get(k)))
								- Math.abs(Double.parseDouble(os_pr_amt.get(k)));
						String orPrAmountAccount = String.valueOf(numberFormat.format(osPrAmount));

						PRINCIPALINT: for (int j = i; j < repayAmount.size(); j++) {
							if (!orPrAmountAccount.equals("0.00")) {
								if (!repayAmount.get(j).isEmpty() && !repayRef.get(j).isEmpty()) {
									if (!repayRef.get(j).contains("SUSPEND") && repayRef.get(j) != null
											&& (!repayAmount.get(j).isEmpty() || repayAmount.get(j).equals("0"))) {
										try {
											repay = Math.abs(repay) + Math.abs(Double.parseDouble(repayAmount.get(j)));
										} catch (Exception e) {
											repay = 0.00;
										}
										String repayValues = String.valueOf(numberFormat.format(repay));

										tempDate = Integer.parseInt(repayRef.get(j).split("-")[1]);
										if (tempStartDate <= tempDate && tempDate <= tempEndDate) {

											/*
											 * String[] data = new String[6]; data[0] = repayRef.get(j).split("-")[1];
											 * data[1] = "804"; data[1]=repayRef.get(j).split("-")[0]; data[2] =
											 * "Interest Repayment"; data[3] = "DEBIT"; data[4] = repayAmount.get(j);
											 * data[5] = repayRef.get(j).split("-")[0];
											 * 
											 * billStatement.add(data);
											 */

											Lnstmt = new LoanStatementObject();

											Lnstmt.setTransDate(TimeFormat2
													.format(DateFormtter.parse("" + repayRef.get(j).split("-")[1])));
											Lnstmt.setTransCode(TransactionDescription(dbConnection, "804", unitId));
											Lnstmt.setNarr("Interest Repayment");
											Lnstmt.setCrfType("DEBIT");
											DebitAmt = repayAmount.get(j);
											CurrentBalance = Double
													.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
													- Math.abs(Double.parseDouble(DebitAmt));
											Lnstmt.setDebtAmt(Double.parseDouble(DebitAmt));
											Lnstmt.setCredAmt(0.0);
											Lnstmt.setBal(CurrentBalance);
											try {
												Lnstmt.setRefNo(repayRef.get(j).split("-")[0]);
											} catch (Exception ex) {
												Lnstmt.setRefNo(repayRef.get(j).split("-")[0]);
											}
											Lnstmt.setTransSeqno(++TotalTxn);
											LnStatementList.addAccount(Lnstmt);

										} else {
											i++;
										}
										// System.out.println("repayValues="+repayValues+"
										// orPrAmountAccount"+orPrAmountAccount);
										if (repayValues.equals(orPrAmountAccount)) {

											i++;
											break PRINCIPALINT;
										} else {
											i++;
										}
									} else {
										i++;
									}
								} else {
									i++;
									break PRINCIPALINT;
								}

							} else {
								i++;
								break PRINCIPALINT;
							}

						}

					} else if (repayPreperty.equals("PENALTYINT")) {
						// System.out.println("Penalty Interest");
						double osPrAmount = 0.0, repay = 0.0;
						osPrAmount = Math.abs(Double.parseDouble(or_pr_amt.get(k)))
								- Math.abs(Double.parseDouble(os_pr_amt.get(k)));
						String orPrAmountAccount = String.valueOf(numberFormat.format(osPrAmount));

						PENALTYINT: for (int j = i; j < repayAmount.size(); j++) {
							// System.out.println("i="+repayAmount.get(j)+" "+repayAmount.size()+"
							// orPrAmountAccount="+orPrAmountAccount);
							if (!orPrAmountAccount.equals("0.00")) {
								if (!repayAmount.get(j).isEmpty() && !repayRef.get(j).isEmpty()) {
									if (!repayRef.get(j).contains("SUSPEND") && repayRef.get(j) != null
											&& (!repayAmount.get(j).isEmpty() || repayAmount.get(j).equals("0"))) {
										try {
											repay = Math.abs(repay) + Math.abs(Double.parseDouble(repayAmount.get(j)));
//											System.out.println("repay=" + repay);
										} catch (Exception e) {
											repay = 0.00;
										}
										String repayValues = String.valueOf(numberFormat.format(repay));

										tempDate = Integer.parseInt(repayRef.get(j).split("-")[1]);
										if (tempStartDate <= tempDate && tempDate <= tempEndDate) {

											/*
											 * String[] data1 = new String[6]; data1[0] = repayRef.get(j).split("-")[1];
											 * data1[1] = "804"; data1[2] = "Penalty Interest Payment"; data1[3] =
											 * "DEBIT"; data1[4] = repayAmount.get(j); data1[5] =
											 * repayRef.get(j).split("-")[0];
											 * 
											 * billStatement.add(data1);
											 */

											Lnstmt = new LoanStatementObject();

											Lnstmt.setTransDate(TimeFormat2
													.format(DateFormtter.parse("" + repayRef.get(j).split("-")[1])));
											Lnstmt.setTransCode(TransactionDescription(dbConnection, "804", unitId));
											Lnstmt.setNarr("Penalty Interest Payment");
											Lnstmt.setCrfType("DEBIT");
											DebitAmt = repayAmount.get(j);
											CurrentBalance = Double
													.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
													- Math.abs(Double.parseDouble(DebitAmt));
											Lnstmt.setDebtAmt(Double.parseDouble(DebitAmt));
											Lnstmt.setCredAmt(0.0);
											Lnstmt.setBal(CurrentBalance);
											try {
												Lnstmt.setRefNo(repayRef.get(j).split("-")[0]);
											} catch (Exception ex) {
												Lnstmt.setRefNo(repayRef.get(j).split("-")[0]);
											}
											Lnstmt.setTransSeqno(++TotalTxn);
											LnStatementList.addAccount(Lnstmt);

										} else {
											i++;
										}
//										System.out.println(
//												"repayValues=" + repayValues + " orPrAmountAccount" + orPrAmountAccount);
										if (repayValues.equals(orPrAmountAccount)) {
											i++;
											break PENALTYINT;
										} else {

											i++;
										}
									} else {
										i++;
									}
								} else {
									i++;
									break PENALTYINT;

								}

							} else {
								i++;
								break PENALTYINT;

							}

						}

					} else if (repayPreperty.equals("PENALTYPS")) {
						i++;

					} else if (repayPreperty.equals("PENALTYCS")) {
						i++;
					} else if (repayPreperty.equals("PENALTYCE")) {
						i++;
					}

				}

				// }

			}

		}

		/*
		 * for (String[] entry : billStatement) {
		 * System.out.println(Arrays.asList(entry).stream().collect(Collectors.joining(
		 * ";"))); }
		 */
		return CurrentBalance;

	}

	private Double getActivityHistory(Connection dbConnection, String acctId, String startDate, String endDate,
			String arrangementId, Double CurrentBalance, String AAActivityHistoryTable, String unitId)
			throws Exception {
		Vector<String[]> data = new Vector<String[]>();

		int tempStartDate = Integer.parseInt(startDate);
		int tempEndDate = Integer.parseInt(endDate);
		int tempDate;
		Vector<String> activities = null;
		Vector<String> amts = null;
		Vector<String> sysDates = null;
		Vector<String> actStatus = null;
		Vector<String> activityConRef = null;
		Vector<String> dailyCumulative = null;
		Vector<String> actDates = null;
		Vector<String> actRef = null;
		try (PreparedStatement dbSt = dbConnection
				.prepareStatement("SELECT * FROM " + AAActivityHistoryTable + " WHERE ID = ?")) {
			dbSt.setString(1, arrangementId);
			try (ResultSet dbRs = dbSt.executeQuery()) {
				if (dbRs.next()) {
					// System.out.println("Calling AA$ACTIVITY$HISTORY ");
					activities = split(dbRs.getString("ACTIVITY"), "^");
					amts = split(dbRs.getString("ACTIVITY_AMT"), "^");
					sysDates = split(dbRs.getString("SYSTEM_DATE"), "^");
					actStatus = split(dbRs.getString("ACT_STATUS"), "^");

					activityConRef = split(dbRs.getString("ACTIVITY_CON_REF"), "^");
					dailyCumulative = split(dbRs.getString("DAILY_CUMULATIVE"), "^");
					actDates = split(dbRs.getString("ACT_DATE"), "^");
					actRef = split(dbRs.getString("ACTIVITY_REF"), "^");
				}
			}

		}
		String DebitAmt = "";
		String CreditAmt = "";

		if (activities != null) {
			for (int i = 0; i < activities.size(); i++) {
				if (activities.size() > i && amts.size() > i && sysDates.size() > i) {
					Vector<String> temp1 = split(activities.get(i).replaceAll("<SV>", "^"), "^");
					Vector<String> temp2 = split(amts.get(i).replaceAll("<SV>", "^"), "^");
					Vector<String> temp3 = split(sysDates.get(i).replaceAll("<SV>", "^"), "^");
					Vector<String> temp4 = split(actStatus.get(i).replaceAll("<SV>", "^"), "^");
					Vector<String> temp5 = split(actRef.get(i).replaceAll("<SV>", "^"), "^");
					for (int j = 0; j < temp1.size(); j++) {

						if (!temp1.get(j).isEmpty()) {

							tempDate = Integer.parseInt(temp3.get(j));

							if (allowedActivityMap.containsKey(temp1.get(j)) // && temp4.get(j).equals("AUTH")
									&& Arrays.asList(new String[] { "AUTH" }).contains(temp4.get(j))
									&& tempStartDate <= tempDate && tempDate <= tempEndDate) {
								/*
								 * String[] temp = new String[6]; temp[0] = temp3.get(j); temp[1] = ""; temp[2]
								 * = allowedActivityMap.get(temp1.get(j)); if (Arrays.asList(new String[] {
								 * "LENDING-APPLYPAYMENT-PO.WITHDRAWAL", "LENDING-DISBURSE-COMMITMENT" })
								 * .contains(temp1.get(j))) { temp[3] = "DEBIT"; } else { temp[3] = "CREDIT"; }
								 * 
								 * try { temp[4] = temp2.get(j); } catch (Exception ex) { temp[4] =
								 * temp2.get(0); } try { temp[5] = temp5.get(j); } catch (Exception ex) {
								 * temp[5] = temp5.get(0); }
								 * 
								 * data.add(temp);
								 */

								Lnstmt = new LoanStatementObject();

								Lnstmt.setTransDate(temp3.get(j));
								Lnstmt.setTransCode("");
								Lnstmt.setNarr(allowedActivityMap.get(temp1.get(j)));
								if (Arrays.asList(new String[] { "LENDING-APPLYPAYMENT-PO.WITHDRAWAL",
										"LENDING-DISBURSE-COMMITMENT" }).contains(temp1.get(j))) {
									Lnstmt.setCrfType("DEBIT");
									try {
										DebitAmt = temp2.get(j);
										CurrentBalance = Double
												.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
												- Math.abs(Double.parseDouble(DebitAmt));
										Lnstmt.setDebtAmt(Double.parseDouble(DebitAmt));
										Lnstmt.setCredAmt(0.0);
										Lnstmt.setBal(CurrentBalance);
									} catch (Exception ex) {
										DebitAmt = temp2.get(0);
										CurrentBalance = Double
												.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
												- Math.abs(Double.parseDouble(DebitAmt));
										Lnstmt.setDebtAmt(Double.parseDouble(DebitAmt));
										Lnstmt.setCredAmt(0.0);
										Lnstmt.setBal(CurrentBalance);
									}
								} else {
									Lnstmt.setCrfType("CREDIT");
									try {
										CreditAmt = temp2.get(j);
										CurrentBalance = Double
												.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
												+ Math.abs(Double.parseDouble(CreditAmt));
										Lnstmt.setCredAmt(Math.abs(Double.parseDouble(CreditAmt)));
										Lnstmt.setDebtAmt(0.0);
										Lnstmt.setBal(CurrentBalance);
									} catch (Exception ex) {
										CreditAmt = temp2.get(0);
										CurrentBalance = Double
												.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
												+ Math.abs(Double.parseDouble(CreditAmt));
										Lnstmt.setCredAmt(Math.abs(Double.parseDouble(CreditAmt)));
										Lnstmt.setDebtAmt(0.0);
										Lnstmt.setBal(CurrentBalance);
									}
								}

								try {
									Lnstmt.setRefNo(temp5.get(j));
								} catch (Exception ex) {
									Lnstmt.setRefNo(temp5.get(0));
								}
								Lnstmt.setTransSeqno(++TotalTxn);
								LnStatementList.addAccount(Lnstmt);

								if (Arrays
										.asList(new String[] { "LENDING-APPLYPAYMENT-PR.ACCRUED.INTEREST",
												"LENDING-APPLYPAYMENT-PR.PRINCIPAL.DECREASE" })
										.contains(temp1.get(j))) {
									/*
									 * String[] tempAcc = new String[6]; tempAcc[0] = temp3.get(j); tempAcc[1] = "";
									 * tempAcc[2] = "Payment Received"; tempAcc[3] = "CREDIT"; tempAcc[4] =
									 * temp2.get(j); try { tempAcc[5] = temp5.get(j); } catch (Exception ex) {
									 * tempAcc[5] = temp5.get(0); }
									 * 
									 * data.add(tempAcc);
									 */

									Lnstmt = new LoanStatementObject();

									Lnstmt.setTransDate(temp3.get(j));
									Lnstmt.setTransCode("");
									Lnstmt.setNarr("Payment Received");
									Lnstmt.setCrfType("CREDIT");
									CreditAmt = temp2.get(j);
									CurrentBalance = Double.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
											+ Math.abs(Double.parseDouble(CreditAmt));
									Lnstmt.setCredAmt(Double.parseDouble(CreditAmt));
									Lnstmt.setDebtAmt(0.0);
									Lnstmt.setBal(CurrentBalance);
									try {
										Lnstmt.setRefNo(temp5.get(j));
									} catch (Exception ex) {
										Lnstmt.setRefNo(temp5.get(0));
									}
									Lnstmt.setTransSeqno(++TotalTxn);
									LnStatementList.addAccount(Lnstmt);
								}
								if (Arrays.asList(new String[] { "LENDING-APPLYPAYMENT-PR.ACCRUED.INTEREST1" })
										.contains(temp1.get(j))) {

									/*
									 * String[] tempAc= new String[6]; tempAc[0] = temp3.get(j); tempAc[1] = "";
									 * tempAc[2] = "Penalty Interest Payment1"; tempAc[3] = "DEBIT"; tempAc[4] =
									 * temp2.get(j); try { tempAc[5] = temp5.get(j); } catch (Exception ex) {
									 * tempAc[5] = temp5.get(0); } data.add(tempAc);
									 */

									Lnstmt = new LoanStatementObject();

									Lnstmt.setTransDate(temp3.get(j));
									Lnstmt.setTransCode("");
									Lnstmt.setNarr("Penalty Interest Payment1");
									Lnstmt.setCrfType("DEBIT");
									DebitAmt = temp2.get(j);
									CurrentBalance = Double.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
											- Math.abs(Double.parseDouble(DebitAmt));
									Lnstmt.setDebtAmt(Double.parseDouble(DebitAmt));
									Lnstmt.setCredAmt(0.0);
									Lnstmt.setBal(CurrentBalance);
									try {
										Lnstmt.setRefNo(temp5.get(j));
									} catch (Exception ex) {
										Lnstmt.setRefNo(temp5.get(0));
									}
									Lnstmt.setTransSeqno(++TotalTxn);
									LnStatementList.addAccount(Lnstmt);
								}

							}
						}
					}
				}
			}

		}
		if (activityConRef != null) {
			for (int i = 0; i < activityConRef.size(); i++) {
				if (activityConRef.size() > i && dailyCumulative.size() > i && actDates.size() > i) {
					if (allowedActivityMap.containsKey(activityConRef.get(i))) {
						Vector<String> tempDate1 = split(actDates.get(i).replaceAll("<SV>", "^"), "^");
						Vector<String> tempDay = split(dailyCumulative.get(i).replaceAll("<SV>", "^"), "^");
						Vector<String> tempID = split(actRef.get(i).replaceAll("<SV>", "^"), "^");
						for (int j = 0; j < tempDay.size(); j++) {
							if (tempDay != null && !tempDay.isEmpty()) {
								tempDate = Integer.parseInt(tempDate1.get(j));
								if (tempStartDate <= tempDate && tempDate <= tempEndDate) {

									/*
									 * String[] temp = new String[6]; temp[0] = tempDate1.get(j); temp[1] = "";
									 * temp[2] = "Penalty Interest Payment"; temp[3] = "DEBIT"; temp[4] =
									 * tempDay.get(j); temp[5] = tempID.get(j); data.add(temp);
									 */

									Lnstmt = new LoanStatementObject();

									Lnstmt.setTransDate(tempDate1.get(j));
									Lnstmt.setTransCode("");
									Lnstmt.setNarr("Penalty Interest Payment");
									Lnstmt.setCrfType("DEBIT");
									DebitAmt = tempDay.get(j);
									CurrentBalance = Double.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
											- Math.abs(Double.parseDouble(DebitAmt));
									Lnstmt.setDebtAmt(Double.parseDouble(DebitAmt));
									Lnstmt.setCredAmt(0.0);
									Lnstmt.setBal(CurrentBalance);
									try {
										Lnstmt.setRefNo(tempID.get(j));
									} catch (Exception ex) {
										Lnstmt.setRefNo(tempID.get(j));
									}
									Lnstmt.setTransSeqno(++TotalTxn);

									LnStatementList.addAccount(Lnstmt);

									/*
									 * String[] temp1 = new String[6]; temp1[0] = tempDate1.get(j); temp1[1] = "";
									 * temp1[2] = "Payment Received"; temp1[3] = "CREDIT"; temp1[4] =
									 * tempDay.get(j); temp1[5] = tempID.get(j); data.add(temp1);
									 */

									Lnstmt = new LoanStatementObject();

									Lnstmt.setTransDate(tempDate1.get(j));
									Lnstmt.setTransCode("");
									Lnstmt.setNarr("Payment Received");
									Lnstmt.setCrfType("CREDIT");
									CreditAmt = tempDay.get(j);
									CurrentBalance = Double.parseDouble(numberFormat.format(Math.abs(CurrentBalance)))
											+ Math.abs(Double.parseDouble(CreditAmt));
									Lnstmt.setCredAmt(Double.parseDouble(CreditAmt));
									Lnstmt.setDebtAmt(0.0);
									Lnstmt.setBal(CurrentBalance);
									try {
										Lnstmt.setRefNo(tempID.get(j));
									} catch (Exception ex) {
										Lnstmt.setRefNo(tempID.get(j));
									}
									Lnstmt.setTransSeqno(++TotalTxn);
									LnStatementList.addAccount(Lnstmt);
								}
							}
						}

					}

				}
			}
		}

		/*
		 * for (String[] entry : data) {
		 * System.out.println(Arrays.asList(entry).stream().collect(Collectors.joining(
		 * ";"))); }
		 */

		return CurrentBalance;
	}

	private static double getAccountActivityBalance(Connection dbConnection, LocalDate openingDate, String accountId,
			String activityDate, String dayFld, String balanceField, String balanceType, String UnitId)
			throws SQLException {
		double balance = 0.00;
		try {
			// System.out.println("Fetching Account Activity Balance For Account [ " +
			// accountId + " ] Activity DAte [ "
			// + activityDate + " ] Opening Date [ " +
			// openingDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
			// + " ]");

			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMM");
			DateTimeFormatter dd = DateTimeFormatter.ofPattern("dd");
			LocalDate actDate = LocalDate.parse(activityDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
			LocalDate startdate = LocalDate.parse(activityDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
			String activityDay = actDate.format(dd);
			int activityDayNo = Integer.parseInt(activityDay);
			String days = null;
			String bals = null;
			Vector<String> dayList = null;
			Vector<String> balList = null;
			String actMonth = "";
			boolean requiredBalanceActivityMonth = true;
			String tableAcctActivity = ActualTableName.get(UnitId + "-" + "ACCT_ACTIVITY$BALANCE_TYPE");
			String sql = "SELECT * FROM " + tableAcctActivity + " WHERE BALANCE_TYPE = '" + balanceType
					+ "' AND ID = ?";
			System.out.println("actDate [" + actDate + "],balanceType[" + balanceType + "]");

			boolean balanceExist = false;
			try (PreparedStatement acctSt = dbConnection.prepareStatement(sql)) {
				ACTIVITY_LOOP: while (openingDate.isBefore(actDate)) {
					actMonth = actDate.format(fmt);
					// System.out.println("Generating Activity For [ " + accountId + " ] [ "
					// +actMonth + " ]");
					generateActivityByBalanceType(dbConnection, accountId, actMonth, UnitId);

					System.out.println("Trying To Fetch Record [ " + accountId + "-" + actMonth + " ]");
					acctSt.setString(1, accountId + "-" + actMonth);
					// System.out.println("Trying to Fetch Activity Record [ " + accountId + "-" +
					// actMonth + " ]");
					requiredBalanceActivityMonth = actMonth.equals(startdate.format(fmt));
					/*
					 * read activity record for balances
					 */
					// System.out.println("requiredBalanceActivityMonth="+requiredBalanceActivityMonth);
					try (ResultSet acctRs = acctSt.executeQuery()) {
						if (acctRs.next()) {
							/*
							 * fetch the last day's activity
							 */
							days = acctRs.getString(dayFld);
							bals = acctRs.getString(balanceField);
							if (days != null && !days.trim().isEmpty()) {
								dayList = split(days, "^");
								balList = split(bals, "^");
								// System.out.println("Month [ " + actMonth + " ] DayList [ " + dayList + " ]
								// balList [ "+ balList + " ]");
								if (requiredBalanceActivityMonth) {
									/*
									 * if activity exist for the required balance month then evaluate the date
									 */
									if (dayList.contains(activityDay)) {
										// System.out.println("if activityDay="+activityDay);
										int dayListIdx = dayList.indexOf(activityDay);
										if (dayListIdx != -1 && balList.size() > dayListIdx) {
											balance = Double.parseDouble(balList.get(dayListIdx));
											balanceExist = true;
										}
									} else {

										int activityDayNoI = dayList.size() - 1;
										for (; activityDayNoI >= 0 && Integer.parseInt(
												dayList.get(activityDayNoI)) > activityDayNo; activityDayNoI--) {
											/*
											 * no action inside the loop
											 */
										}

										if (activityDayNoI >= 0 && balList.size() > activityDayNoI) {
											balance = Double.parseDouble(balList.get(activityDayNoI));
											balanceExist = true;
										}
										// System.out.println("else activityDayNoI="+activityDayNoI);
									}
								} else {
									/*
									 * if not activity month pick the previous activity month last days activity
									 * balance
									 */
									balance = Double.parseDouble(balList.get(balList.size() - 1));
									balanceExist = true;
									// System.out.println("else balance="+balance);
								}
								if (balanceExist) {
									break ACTIVITY_LOOP;
								}
							}
						}
					}
					/*
					 * read activity record for balances
					 */
					actDate = actDate.minus(1, ChronoUnit.MONTHS);
					requiredBalanceActivityMonth = false;
				}
			}
		} catch (Exception except) {
			except.printStackTrace();
			balance = 0.00;
		}
		System.out.println("balance [" + balanceType + "],[" + balance + "]");
		return balance;
	}

	public static void generateActivityByBalanceType(Connection dbConnection, String accountNo, String yyyyMM,
			String UnitId) throws Exception {
		String tblName = ActualTableName.get(UnitId + "-" + "ACCT$BALANCE$ACTIVITY");
		String actTableDetail = ActualTableName.get(UnitId + "-" + "ACCT_ACTIVITY$BALANCE_TYPE");
		// System.out.println("Generating Activity For [ " + accountNo + " ] [ " +
		// yyyyMM + " ]");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		try {
			PreparedStatement acctBalanceActSt = dbConnection
					.prepareStatement("SELECT * FROM " + tblName + " TBL WHERE ID = ?");
			PreparedStatement acctBalSt = dbConnection.prepareStatement(
					"SELECT TBL.* FROM " + actTableDetail + " TBL WHERE ID = ? AND BALANCE_TYPE = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			acctBalanceActSt.setString(1, accountNo + "-" + yyyyMM);
			try (ResultSet acctBalanceActRs = acctBalanceActSt.executeQuery()) {
				if (acctBalanceActRs.next()) {
					Document doc = builder.parse(acctBalanceActRs.getClob("XML_CONTENT").getAsciiStream());
					final Element rootElement = doc.getDocumentElement();
					Map<String, Integer> balanceTypePos = extractBalanceTypePost(rootElement);
					balanceTypePos.entrySet().stream().forEach(entry -> {
						generateBalanceTypeActivity(acctBalSt, accountNo, yyyyMM, entry.getKey(), entry.getValue(),
								rootElement);
					});
					if (!dbConnection.getAutoCommit())
						dbConnection.commit();

				}

			}
		} catch (Exception ee) {
			// System.out.println("Exceptions [ " + acctId + " ] "+ee.getMessage());
			ee.printStackTrace();
			try {
				throw new Exception("Exception: [ " + accountNo + " ] Not Complete");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static Map<String, Integer> extractBalanceTypePost(Element rootElement) {
		Map<String, Integer> balanceTypePos = new LinkedHashMap<String, Integer>();
		NodeList elemList = rootElement.getElementsByTagName("c1");
//		System.out.println("elemList=========="+elemList.getLength());
		int elemCnt = elemList.getLength();
		Element balElement = null;
		String attr = null;

		for (int elemI = 0; elemI < elemCnt; elemI++) {

			balElement = (Element) elemList.item(elemI);
			attr = balElement.getAttribute("m");
			if (attr == null || attr.isEmpty()) {
				attr = "1";
			}
			balanceTypePos.put(balElement.getFirstChild().getNodeValue(), Integer.parseInt(attr));

//			System.out.println("balanceTypePos=="+balanceTypePos);
		}
		return balanceTypePos;
	}

	private static void generateBalanceTypeActivity(PreparedStatement acctBalSt, String accountNo, String yyyyMM,
			String balanceType, Integer balanceTypePos, final Element rootElement) {
		try {
			boolean newRecord = false;
			String id = accountNo + "-" + yyyyMM;
			acctBalSt.setString(1, id);
			acctBalSt.setString(2, balanceType);
			try (ResultSet acctRs = acctBalSt.executeQuery()) {
				newRecord = !acctRs.next();
				if (newRecord) {
					acctRs.moveToInsertRow();
				}
				acctRs.updateString("ID", id);
				acctRs.updateString("BALANCE_TYPE", balanceType);

				fldMap.entrySet().stream().forEach(entry -> {
					try {
						updateFldData(acctRs, rootElement, balanceTypePos, entry.getKey(), entry.getValue());
					} catch (Exception except) {
						throw new RuntimeException(except);
					}
				});
				if (newRecord) {
					acctRs.insertRow();
				} else {
					acctRs.updateRow();
				}

			}
		} catch (Exception except) {
			except.printStackTrace();
		}

	}

	private static void updateFldData(ResultSet acctRs, Element rootElement, Integer balanceTypePos, String fldName,
			String fldPos) throws SQLException {
		NodeList elementList = rootElement.getElementsByTagName("c2");
		int elemCnt = elementList.getLength();
		Element fldElem = null;
		String m = null;
		String s = null;
		String t = null;
		Vector<String> contentVector = new Vector<String>();
		String elementContent = null;
		for (int elemI = 0; elemI < elemCnt; elemI++) {
			fldElem = (Element) elementList.item(elemI);
			if (fldElem.getChildNodes().getLength() > 0) {
				elementContent = fldElem.getFirstChild().getNodeValue();
			} else {
				elementContent = "";
			}
			m = fldElem.getAttribute("m");
			if (m == null || m.isEmpty()) {
				m = "1";
			}
			if (Integer.parseInt(m) == balanceTypePos) {
				s = fldElem.getAttribute("s");
				if (s == null || s.isEmpty()) {
					s = "1";
				}
				if (Integer.parseInt(s) == Integer.parseInt(fldPos)) {
					t = fldElem.getAttribute("t");
					if (t == null || t.isEmpty()) {
						t = "1";
					}

					if (Integer.parseInt(t) == 1) {
						contentVector.add(elementContent);
					} else {
						int size = contentVector.size();
						if (size + 1 < Integer.parseInt(t)) {
							for (int i = size; i < Integer.parseInt(t) - 1; i++) {
								contentVector.add("");
							}
						}
						contentVector.add(elementContent);
					}

				}
			}
		}
		String content = contentVector.stream().collect(Collectors.joining("^"));
//		 System.out.println(balanceTypePos + "/" + fldName + "=" + content);
		acctRs.updateString(fldName, content);

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

	private String TransactionDescription(Connection dbconnection, String TransCode, String unitId) {

		String TransDesc = "";
		String TransactionTable = ActualTableName.get(unitId + "-" + "TRANSACTION");
		try {
			PreparedStatement TransactionDescPS = dbconnection
					.prepareStatement("SELECT description FROM " + TransactionTable + "  where id = ? ");
			TransactionDescPS.setString(1, TransCode);
			ResultSet TransactionDescRS = TransactionDescPS.executeQuery();
			if (TransactionDescRS.next()) {
				TransDesc = TransactionDescRS.getString(1);
			}
			TransactionDescPS.close();
			TransactionDescRS.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return TransDesc;
	}
	
	private void ResponseMessages(LoanStatementList LnStatementList,String unitId,String AccountNumber,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		LnStatementList.setUnID(unitId);
		LnStatementList.setLoanAccnumber(AccountNumber);
		LnStatementList.setErrCode(ErrorCode);
		LnStatementList.setErrorDesc(ErrorDescription);
		
	}

}
