package com.nls.Enquiry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

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

@Path("/AccountStatement")
public class AccountStatement {

	private static DataSource cmDBPool;

	private static HashMap<String, String> descriptionMappings;
	private static HashMap<String, String> ActualTableName;
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	private static String AcctTableRef = "ACCOUNT";
	private static String AccActivityRef = "ACCT_ACTIVITY";
	private static String AccStmtPrintRef = "ACCT$STMT$PRINT";
	private static String StmtEntryTableRef = "STATEMENT$ENTRY";
	private static String StmtEntryDetailsTableRef = "STATEMENT$ENTRY$DETAILS";
	private static String StmtPrintedRef = "STMT$PRINTED";
	private static String StmtEntryDetailsXrefTableRef = "STMT_ENTRY_DETAIL_XREF";
	private static String FundsTransferRef = "FUNDS$TRANSFER";
	private static String FundsTransferHistoryRef = "FUNDS$TRANSFER_HISTORY";
	private static String TellerRef = "TELLER";
	private static String TellerHistoryRef = "TELLER_HISTORY";

	// latest narrative changes
	private static String ftAppl = "FUNDS.TRANSFER";
	private static String ttAppl = "TELLER";
	private static String acChgReqAppl = "AC.CHARGE.REQUEST";
	private static String fxAppl = "FOREX";

	private static Map<String, Map<String, String>> applFldMapgs = null;
	private static Map<String, String> paymentDetailFld = new HashMap<String, String>();
	private static Map<String, String> systemIdMap = new HashMap<String, String>();
	private static Map<String, String> TableColumns = new HashMap<String, String>();
	private static Map<String, String> liveTables = new HashMap<String, String>();
	private static Map<String, String> historyTables = new HashMap<String, String>();

	private static String ftHistTableT24 = "FBNK_FUNDS_TRANSFER#HIS";
	private static String ttHistTableT24 = "FBNK_TELLER#HIS";

	public static void setDBPool(DataSource cmDBPool) {
		AccountStatement.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		AccountStatement.ActualTableName = ActualTableName;
	}

	public static void setInitiailizeGlobalParameters(HashMap<String, String> GlobalParameters) {
		AccountStatement.descriptionMappings = GlobalParameters;
	}

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = AccountStatementList.class, responseDescription = "Account Statement Response", responseCode = "200")
	@Operation(summary = "Account Statement Request", description = "returns Account Statement")
	public Response getAccountStatement(
			@RequestBody(description = "Account Number", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountStatementRequest.class))) AccountStatementRequest id) {

		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Account Statement Interface Started on ["+startTime+"]");
			Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
			System.out.println(" [AccountStatement] Enquiry Has Been Started on [" + timestamp1 + "]");
			
			String unitId = id.unId;
			String AccountNumber = id.accNumber;
			String StDate = id.fromDate;
			String EnDate = id.toDate;
			String AccType = id.accType;

			System.out
					.println("Fetching Account Statement For Account Number [ " + AccountNumber + " ] , Account Type [ "
							+ AccType + " ], From Date [ " + StDate + " ], To Date [ " + EnDate + " ]");

			AccountStatementList accountStmtList = new AccountStatementList();

			System.out.println("Account Statement Table Ref [" + AcctTableRef + "," + AccActivityRef + ","
					+ AccStmtPrintRef + "," + StmtEntryTableRef + "," + StmtEntryDetailsTableRef + ","
					+ StmtEntryDetailsXrefTableRef + "," + FundsTransferRef + "," + TellerRef + "]");

			String AcctTableName = ActualTableName.get(unitId + "-" + AcctTableRef);
			String AcctActivityTableName = ActualTableName.get(unitId + "-" + AccActivityRef);
			String AcctStmtPrintTableName = ActualTableName.get(unitId + "-" + AccStmtPrintRef);

			System.out.println("Account Statement Table Names [" + AcctTableName + "," + AcctActivityTableName + ","
					+ AcctStmtPrintTableName + "]");

			int TotalTxn = 0;

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection
							.prepareStatement("select ID,ACCOUNT_TITLE,CURRENCY,ONLINE_WORKING_BALANCE,"
									+ " OPEN_ACTUAL_BALANCE,OPENING_DATE from " + AcctTableName + " WHERE id =? ")) {

				dbSt.setString(1, AccountNumber);
				try (ResultSet dbRs = dbSt.executeQuery()) {
					boolean Exists = false;
					if (dbRs.next()) {
						Exists = true;

						String StartDate = TimeFormat2.format(DateFormtter.parse("" + StDate));
						String EndDate = TimeFormat2.format(DateFormtter.parse("" + EnDate));
						LocalDate openingDate = dbRs.getDate("OPENING_DATE").toLocalDate();

						double openingBalance = getAccountActivityBalance(dbConnection, openingDate,
								dbRs.getString("ID"), StartDate, "BK_DAY_NO", "BK_BALANCE", unitId);
						System.out.println("Opening Balance [" + openingBalance + "]");

						double ClosingBalance = getAccountActivityBalance(dbConnection, openingDate,
								dbRs.getString("ID"), EndDate, "BK_DAY_NO", "BK_BALANCE", unitId);
						System.out.println("Closing Balance [" + ClosingBalance + "]");

						/*
						 * Object OpeningBalance[] = getOpeningBalance(dbConnection, AccountNumber,
						 * StartDate, AcctActivityTableName);
						 */
						Vector<String> statementDates = getStatementDates(dbConnection, dbRs.getString("ID"),
								StartDate + "", EndDate, AcctStmtPrintTableName);

						Vector<String> modifiedDates = new Vector<String>();
						for (String stmtDate : statementDates) {
							if (!modifiedDates.contains(stmtDate)) {
								modifiedDates.add(stmtDate);
								// System.out.println(stmtDate);
							}
						}
						statementDates = modifiedDates;

						System.out.println("Account statement Dates [" + statementDates + "]");

						/*
						 * TotalTxn = generateStatement(dbConnection, AccountNumber, "", StartDate,
						 * EndDate, statementDates, Double.parseDouble(OpeningBalance[1].toString()),
						 * dbRs.getString("CURRENCY"), accountStmtList, unitId);
						 */
						TotalTxn = generateStatement(dbConnection, AccountNumber, "", StartDate, EndDate,
								statementDates, openingBalance, dbRs.getString("CURRENCY"), accountStmtList, unitId);

						System.out.println("TotalTxn=" + TotalTxn);

						if (TotalTxn == 0) {
							//System.out.println("**********transaction not found*************");
							accountStmtList = new AccountStatementList();
							ResponseMessages(accountStmtList,unitId,AccountNumber,StDate,
									EnDate,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.STATEMENT_NOT_FOUND.getValue());
							return Response.status(Status.ACCEPTED).entity(accountStmtList).build();	
						} else if (TotalTxn > 0) {

							accountStmtList.unitID = unitId;
							accountStmtList.accNumber = AccountNumber;
							accountStmtList.accName = dbRs.getString("ACCOUNT_TITLE");
							accountStmtList.fromDate = StDate;
							accountStmtList.toDate = EnDate;
							accountStmtList.currCode = dbRs.getString("CURRENCY");
							accountStmtList.openingBal = openingBalance;
							accountStmtList.closingBal = ClosingBalance;
							accountStmtList.totalTxn = TotalTxn;
						}

					}
					dbRs.close();

					if (!Exists) {
						//System.out.println("**********account not found*************");
						accountStmtList = new AccountStatementList();
						ResponseMessages(accountStmtList,unitId,AccountNumber,StDate,
								EnDate,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.ACCOUNT_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(accountStmtList).build();	
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					//System.out.println("**********catch1*************");
					accountStmtList = new AccountStatementList();
					ResponseMessages(accountStmtList,unitId,AccountNumber,StDate,
							EnDate,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(accountStmtList).build();
				}
				dbSt.close();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				//System.out.println("**********catch2*************");
				accountStmtList = new AccountStatementList();
				ResponseMessages(accountStmtList,unitId,AccountNumber,StDate,
						EnDate,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(accountStmtList).build();
			}
			
			timestamp1 = new Timestamp(System.currentTimeMillis());
			System.out.println(" [AccountSummary] Enquiry Has Been Completed on [" + timestamp1 + "]");
			
			return Response.status(Status.ACCEPTED).entity(accountStmtList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now(); 
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Account Statement Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	public double getAccountActivityBalance(Connection dbConnection, LocalDate openingDate, String accountId,
			String activityDate, String dayFld, String balanceField, String UnitId) throws SQLException {
		
		
		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		System.out.println(" [AccountSummary],[AccountActivityBalance] condition Has Been Started on [" + timestamp1 + "]");
		
		double balance = 0.00;
		
		try {
			System.out.println(
					"Fetching Account Activity Balance For Account [ " + accountId + " ] [ " + activityDate + " ]");

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
			String tableAcctActivity = ActualTableName.get(UnitId + "-ACCT_ACTIVITY");
			String tableAcctBalanceActivity = ActualTableName.get(UnitId + "-ACCT$BALANCE$ACTIVITY");
			String tableAcctActivityBalanceType = ActualTableName.get(UnitId + "-ACCT_ACTIVITY$BALANCE_TYPE");
			ACCOUNT_TYPE acctType = AccountUtilities.getAccountType(accountId, dbConnection, UnitId);
			// tableAcctActivity = AccountUtilities.getTableName(dbConnection,
			// configProperties, tableAcctActivity);
			String sql = "SELECT * FROM " + tableAcctActivity + " WHERE ID = ?";
			String accountTable = ActualTableName.get(UnitId + "-ACCOUNT");
			String arrangementReference = AccountUtilities.getStringField(dbConnection, accountTable, "ID", accountId,
					"ARRANGMENT_ID");
			if (acctType == ACCOUNT_TYPE.LENDING || acctType == ACCOUNT_TYPE.DEPOSITS
					|| (acctType == ACCOUNT_TYPE.ACCOUNTS && arrangementReference != null
							&& !arrangementReference.isEmpty())) {
				// tableAcctActivity =
				// ActualTableName.get(UnitId+"-ACCT_ACTIVITY$BALANCE_TYPE");
				// tableAcctActivity = AccountUtilities.getTableName(dbConnection,
				// configProperties, tableAcctActivity);
				// System.out.println("***************Calling Deposit
				// Accounts****************");
				String balanceType = "CURBALANCE";
				sql = "SELECT * FROM " + tableAcctActivityBalanceType + " WHERE BALANCE_TYPE = '" + balanceType
						+ "' AND ID = ?";
			}
			boolean activityFound = false;
			try (PreparedStatement acctSt = dbConnection.prepareStatement(sql)) {
				ACTIVITY_LOOP: while (openingDate.isBefore(actDate) || openingDate.isEqual(actDate)) {
					actMonth = actDate.format(fmt);

					System.out.println("Trying To Generate Activity For  [ " + accountId + " ] [ " + actMonth + " ]");
					AccountActivityByBalanceTypeGenerator.generateActivityByBalanceType(dbConnection, accountId,
							actMonth, tableAcctBalanceActivity, tableAcctActivityBalanceType);
					acctSt.setString(1, accountId + "-" + actMonth);
					// System.out.println(accountId + "-" + actMonth);
					System.out.println("Trying to Fetch Activity Record [ " + accountId + "-" + actMonth + " ]");
					requiredBalanceActivityMonth = actMonth.equals(startdate.format(fmt));
					/*
					 * read activity record for balances
					 */
					try (ResultSet acctRs = acctSt.executeQuery()) {
						if (acctRs.next()) {
							/*
							 * fetch the last day's activity
							 */
							// System.out.println("***************Record called************");
							days = acctRs.getString(dayFld);
							bals = acctRs.getString(balanceField);
							if (days != null && !days.trim().isEmpty()) {
								dayList = AccountUtilities.split(days, "^");
								balList = AccountUtilities.split(bals, "^");
								System.out.println("Trying to Fetch Activity Record [ " + accountId + "-" + actMonth
										+ " ] Activity Days [ " + activityDay + " ] [ " + dayList + " ] "
										+ " Required Month [ " + requiredBalanceActivityMonth + " ]");
								if (requiredBalanceActivityMonth) {
									/*
									 * if activity exist for the required balance month then evaluate the date
									 */

									if (dayList.contains(activityDay)) {
										int dayListIdx = dayList.indexOf(activityDay);
										if (dayListIdx != -1 && balList.size() > dayListIdx) {
											balance = Double.parseDouble(balList.get(dayListIdx));
											activityFound = true;
										}
									} else if (Integer.parseInt(dayList.lastElement()) <= Integer
											.parseInt(activityDay)) {
										balance = Double.parseDouble(balList.lastElement());
										activityFound = true;

									} else {
										int activityDayNoI = dayList.size() - 1;
										for (; activityDayNoI > 0 && Integer.parseInt(
												dayList.get(activityDayNoI)) > activityDayNo; activityDayNoI--)
											;

										if (balList.size() > activityDayNoI && activityDayNoI >= 0
												&& Integer.parseInt(dayList.get(activityDayNoI)) < Integer
														.parseInt(activityDay)) {
											balance = Double.parseDouble(balList.get(activityDayNoI));
											activityFound = true;
										}
									}

								} else {
									/*
									 * if not activity month pick the previous activity month last days activity
									 * balance
									 */
									balance = Double.parseDouble(balList.get(balList.size() - 1));
									activityFound = true;
								}
								if (activityFound) {
									System.out.println("Trying to Fetch Activity Record [ " + accountId + "-" + actMonth
											+ " ] Picked Balance [ " + balance + " ]");
									break ACTIVITY_LOOP;
								}
							}
						}
						acctRs.close();
					}
					/*
					 * read activity record for balances
					 */
					actDate = actDate.minus(1, ChronoUnit.MONTHS);
					actDate = actDate.with(TemporalAdjusters.lastDayOfMonth());
					System.out.println("Activity Record Date updated [ " + accountId + "-" + actDate.format(fmt)
							+ " ] Account Opening Date [ " + openingDate + " ]");
					requiredBalanceActivityMonth = false;
				}
				acctSt.close();
			}
		} catch (Exception except) {
			balance = 0.00;
		}
		
		timestamp1 = new Timestamp(System.currentTimeMillis());
		System.out.println(" [AccountSummary],[AccountActivityBalance] condition Has Been Completed on [" + timestamp1 + "]");
		
		return balance;
	}

	private Integer generateStatement(Connection dbConnection, String accountNumber, String tablePrefix,
			String startDate, String endDate, Vector<String> statementDates, Double openingBalance,
			String localCurrency, AccountStatementList StatementList, String UnitID) throws Exception {

		
		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		System.out.println(" [AccountSummary],[generateStatement] condition Has Been Started on [" + timestamp1 + "]");
		
		String StmtEntryTable = ActualTableName.get(UnitID + "-" + StmtEntryTableRef);
		String StmtEntryDetailsTable = ActualTableName.get(UnitID + "-" + StmtEntryDetailsTableRef);
		String StmtPrintedTable = ActualTableName.get(UnitID + "-" + StmtPrintedRef);
		String detailReferencesTable = ActualTableName.get(UnitID + "-" + StmtEntryDetailsXrefTableRef);
		String FundTransferTable = ActualTableName.get(UnitID + "-" + FundsTransferRef);
		String TellerTable = ActualTableName.get(UnitID + "-" + TellerRef);
		String FundTransferHistoryTable = ActualTableName.get(UnitID + "-" + FundsTransferHistoryRef);
		String TellerHistoryTable = ActualTableName.get(UnitID + "-" + TellerHistoryRef);

		System.out.println("Account Statement Table Names [" + StmtEntryTable + "," + StmtEntryDetailsTable + ","
				+ StmtPrintedTable + "," + detailReferencesTable + "," + FundTransferTable + ","
						+ ""+FundTransferHistoryTable+"," + TellerTable + ","+TellerHistoryTable+"]");

		Vector<String> statementInfo = new Vector<String>();

		DecimalFormat amountFormat = new DecimalFormat("0.00");

		double RunningBalance = openingBalance;

		AccountStatementObject account = null;

		// String statementTable = tablePrefix + "STATEMENT$ENTRY";
		int TransactionSeqNo = 0;

		try (PreparedStatement FTStatement = dbConnection
				.prepareStatement("select DEBIT_AMOUNT,CREDIT_AMOUNT,CHEQUE_NUMBER,TRANSACTION_TYPE,REF_TXN_ID from "
						+ FundTransferTable + " where ID = ? or ID = ?");
				
				PreparedStatement FTHistStatement = dbConnection
						.prepareStatement("select DEBIT_AMOUNT,CREDIT_AMOUNT,CHEQUE_NUMBER,TRANSACTION_TYPE,REF_TXN_ID from "
								+ FundTransferHistoryTable + " where ID = ? or ID = ?");
				
				PreparedStatement TellerStatement = dbConnection.prepareStatement("select AMOUNT_LOCAL1,AMOUNT_LOCAL2,"
						+ " TRANS_TYPE from " + TellerTable + " where id = ? or id = ? ");
				
				PreparedStatement TellerHistStatement = dbConnection.prepareStatement("select AMOUNT_LOCAL1,AMOUNT_LOCAL2,"
						+ " TRANS_TYPE from " + TellerHistoryTable + " where id = ? or id = ? ");
				
				PreparedStatement stmtStatement = dbConnection
						.prepareStatement(
								"SELECT ID,BOOKING_DATE,VALUE_DATE,PROCESSING_DATE,CRF_TYPE,NARRATIVE,"
										+ " EXCHANGE_RATE,THEIR_REFERENCE,TRANS_REFERENCE,TRANSACTION_CODE,CURRENCY,"
										+ "CUSTOMER_ID,AMOUNT_FCY,AMOUNT_LCY,CHEQUE_NUMBER,SYSTEM_ID FROM "
										+ StmtEntryTable + " WHERE ID = ?",
								ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			ResultSet stmtRecord = null;

			int no_of_statements = statementDates.size();

			// SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat timeParser = new SimpleDateFormat("yyyyMMdd");

			// SimpleDateFormat timeParser2 = new SimpleDateFormat("yyMMddHHss");

			SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");

			PreparedStatement stmtPrintedStatement = dbConnection
					.prepareStatement("SELECT * FROM " + StmtPrintedTable + " WHERE ID = ?");

			ResultSet stmtPrintedRecord = null;

			String[] entryIds = null;

			int no_of_entries = 0;

			int tempStartDate = Integer.parseInt(startDate);

			int tempEndDate = Integer.parseInt(endDate);

			// int tempReqStartDate = Integer.parseInt(startDate);

			// System.out.println("startDate "+startDate);
			int bookingDate = 0;

			HashMap<String, String> detailReferencesTables = new HashMap<String, String>();

			// String amount = "";

			// double previousRunningBal;

			Vector<String> entList = new Vector<String>();

			// PreparedStatement entrySt = dbConnection.prepareStatement("INSERT INTO
			// MISSING$ITEMS VALUES(?,?)");

			// PreparedStatement entryArc = dbConnection.prepareStatement("INSERT INTO
			// MISSING$ITEMS VALUES(?,?)");

			// boolean recordExists = false;

			PreparedStatement stmtDetailStatement = dbConnection.prepareStatement(
					"SELECT * FROM " + StmtEntryDetailsTable + " WHERE ID = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			if (detailReferencesTables.containsKey(detailReferencesTable)) {
				detailReferencesTable = detailReferencesTables.get(detailReferencesTable);
			}
			PreparedStatement stmtDetailReferenceStatement = dbConnection
					.prepareStatement("" + "SELECT DETAIL_ID FROM " + detailReferencesTable + " WHERE ID =?");
			ResultSet stmtDetailRefRs = null;

			boolean detailRecord = false;

			String temp = null;
			// String detailId = null;
			String[] newEntryList = null;
			String[] tempEntryList = null;
			String stmtPrintedDate = "";
			String entryListString = "";
			boolean r08finalStatement = false;
			// String closingBalance = "" + amountFormat.format(openingBalance);
			String opBalance = "" + amountFormat.format(openingBalance);
			String detailReferenceId = "";

			try {

				PRINTED_LOOP: for (int stmtPrintedI = 0; stmtPrintedI < no_of_statements; stmtPrintedI++) {

					stmtPrintedDate = statementDates.get(stmtPrintedI);
					//System.out.println("Statement Dates =" + stmtPrintedDate);
					entryListString = "";
					r08finalStatement = false;

					stmtPrintedStatement.setString(1, accountNumber + "-" + stmtPrintedDate);
					stmtPrintedRecord = stmtPrintedStatement.executeQuery();
					// }
	

					if (r08finalStatement
							|| (stmtPrintedRecord.next() && stmtPrintedRecord.getString("ENTRY_LIST") != null)) {

						if (r08finalStatement) {
							entryIds = entryListString.split("\\^");
						} else {
							entryIds = stmtPrintedRecord.getString("ENTRY_LIST").split("\\^");
						}
						no_of_entries = entryIds.length;

						ENTRY_LOOP: for (int entryI = 0; entryI < no_of_entries; entryI++) {

							// System.out.println("Entry Id " + entryIds[entryI]);

							account = new AccountStatementObject();

							// detailId = "";
							if (entryIds[entryI] == null || entryIds[entryI].trim().equals("")) {
								continue;
							}
							if (!entList.contains(entryIds[entryI])) {
								entList.add(entryIds[entryI]);

							} else {
								continue;
							}
							if (entryIds[entryI].length() < 15) {
								entList.add(entryIds[entryI]);
								continue;
							}
//                      System.out.println("Entry Id " + entryIds[entryI]);
							detailRecord = false;
							// detailId = "";
							if (entryIds[entryI].length() <= 25) {
								detailRecord = false;
								stmtStatement.setString(1, entryIds[entryI]);
								stmtRecord = stmtStatement.executeQuery();
								if (stmtRecord.next()) {
									stmtRecord.beforeFirst();
								} else {
									stmtRecord.close();
									// detailId = entryIds[entryI];

									stmtDetailStatement.setString(1, entryIds[entryI]);
									stmtRecord = stmtDetailStatement.executeQuery();
									detailRecord = stmtRecord.next();
									if (!detailRecord) {
										detailRecord = false;
									}
									stmtRecord.beforeFirst();
								}
							} else {
								// detailRecord = true;
								// read the stmt printed entries and confirm
								detailReferenceId = "";
								detailReferenceId = entryIds[entryI] + "-1";
								// detailId = detailReferenceId;
								if (detailReferenceId != null && !detailReferenceId.equals("")) {
									stmtDetailReferenceStatement.setString(1, detailReferenceId);
									stmtDetailRefRs = stmtDetailReferenceStatement.executeQuery();
									if (stmtDetailRefRs.next()) {
										temp = stmtDetailRefRs.getString("DETAIL_ID");
										// System.out.println(entryIds[entryI] + " " + temp);
										if (temp != null && !temp.equals("")) {
											tempEntryList = temp.split("\\^");

											newEntryList = new String[no_of_entries + tempEntryList.length - 1];

											// System.out.println("Rebuilding The Array");

											/*
											 * array copy and reassign the positions
											 */

											/*
											 * copy the array from 0 to index position
											 */
											System.arraycopy(entryIds, 0, newEntryList, 0, entryI);

											/*
											 * copy the entry detail list to the new list from index position
											 */
											try {
												System.arraycopy(tempEntryList, 0, newEntryList, entryI,
														tempEntryList.length);
											} catch (Exception except) {
												// System.out.println("Temp Entry List " + tempEntryList.length);
												// System.out.println("New Entry List Size " + newEntryList.length);

												throw except;

											}

											/*
											 * 
											 * 
											 */
											// System.arraycopy(listOne, index + 1,
											// list3, index + listTwo.length,
											// listOne.length - (index + 1));
											try {
												System.arraycopy(entryIds, entryI + 1, newEntryList,
														entryI + tempEntryList.length, entryIds.length - (entryI + 1));
											} catch (Exception except) {
												// System.out.println(entryI + " " + (entryI + tempEntryList.length));

												throw except;
											}

											entryIds = newEntryList;
											entryI--;

											no_of_entries = newEntryList.length;

											stmtDetailRefRs.close();
											stmtDetailReferenceStatement.clearParameters();

											continue;

											// detailId = temp.split("\\^")[0];
											// stmtDetailStatement.setString(1,
											// detailId);
											// stmtRecord = stmtDetailStatement
											// .executeQuery();
										}

									}
								}

							}

							if (stmtRecord.next()) {

								bookingDate = Integer.parseInt(TimeFormat2
										.format(TimeFormat.parse("" + stmtRecord.getString("BOOKING_DATE"))));
								// bookingDate =
								// Integer.parseInt(stmtRecord.getString("BKG_DATE").replaceAll("-", ""));

								System.out.println("Start Date [" + tempStartDate + "] , bookingDate [" + bookingDate
										+ " ] , End Date [" + tempEndDate + " ]");

								if (tempStartDate <= bookingDate && bookingDate <= tempEndDate) {
									System.out.println("Account Statement ID [ " + stmtRecord.getString("ID") + " ]");
									opBalance = "" + amountFormat.format(RunningBalance);
									if (!localCurrency.equalsIgnoreCase(stmtRecord.getString("CURRENCY"))
											&& stmtRecord.getString("AMOUNT_FCY") != null
											&& !stmtRecord.getString("AMOUNT_FCY").equals("")) {

										RunningBalance += Double.parseDouble(stmtRecord.getString("AMOUNT_FCY"));

									} else {
										RunningBalance += Double.parseDouble(stmtRecord.getString("AMOUNT_LCY"));
									}
									if (statementInfo.size() == 0) {
										statementInfo.add(opBalance);
									}

									account.setValDate(DateFormtter
											.format(TimeFormat.parse("" + stmtRecord.getString("VALUE_DATE"))));
									account.setTransDate(DateFormtter
											.format(TimeFormat2.parse("" + stmtRecord.getString("PROCESSING_DATE"))));
									account.setRefNo(stmtRecord.getString("ID")==null?"":stmtRecord.getString("ID"));
									account.setCrfType(stmtRecord.getString("CRF_TYPE")==null?"":stmtRecord.getString("CRF_TYPE"));

									StringBuffer description = new StringBuffer();
									String chqNo = stmtRecord.getString("CHEQUE_NUMBER");
									if (chqNo != null && !chqNo.isEmpty()) {
										description.append("CHQ-");
										description.append(chqNo);
										description.append(" ");
									}

									String systemId = stmtRecord.getString("SYSTEM_ID");
									String originalSystemId = systemId;
									if (systemId != null && systemId.length() > 2) {
										systemId = systemId.substring(0, 2);
									}
									String TransactionDesc = TransactionDescription(dbConnection,
											stmtRecord.getString("TRANSACTION_CODE"), UnitID);

									System.out.println("systemId[" + systemId + "]");
								//	System.out.println("originalSystemId[" + originalSystemId + "]");
									//System.out.println("TransactionDesc[" + TransactionDesc + "]");
									System.out.println("TransRef[" + stmtRecord.getString("TRANS_REFERENCE") + "]");
									System.out.println("description[" + description + "]");

									String transId = stmtRecord.getString("TRANS_REFERENCE");
									if (transId.indexOf("\\") != -1) {
										transId = transId.substring(0, transId.indexOf("\\"));
									}
									
									extractDescriptionField(systemId, originalSystemId, dbConnection,
											transId, TransactionDesc, description,
											stmtRecord, UnitID);
									// String stmtNarravie = getClobContent(stmtRecord, "NARRATIVE");

									String stmtNarrative = stmtRecord.getString("NARRATIVE");
									if (description.length() <= 0 && stmtNarrative != null
											&& !stmtNarrative.trim().isEmpty()) {
										description.append(stmtNarrative.trim());
									}
									if (description.length() <= 0) {
										description.append(TransactionDesc);
									}

									int NarrationLength = Integer.parseInt(descriptionMappings.get("NARRATION_LENGTH"));
									System.out.println("NarrationLength[" + NarrationLength + "]");
									String desc = description.length() < NarrationLength ? description.toString()
											: description.toString().substring(0, NarrationLength);
									if (desc != null) {
										desc = AccountUtilities.rationalizeInvalidChars(desc);
										desc = desc.replaceAll("\\^", " ");
									}
									System.out.println("desc[" + desc + "]");
									account.setNarr(desc);
									// account.setNarr(stmtRecord.getString("NARRATIVE"));
									account.setExchangeRate(stmtRecord.getInt("EXCHANGE_RATE"));
									account.setTheirRef(stmtRecord.getString("THEIR_REFERENCE")==null?"":stmtRecord.getString("THEIR_REFERENCE"));
									account.setTransReference(stmtRecord.getString("TRANS_REFERENCE")==null?"":stmtRecord.getString("TRANS_REFERENCE"));
									account.setTransCode(stmtRecord.getString("TRANSACTION_CODE")==null?"":stmtRecord.getString("TRANSACTION_CODE"));
									account.setDescription(TransactionDesc);
									account.setCurrency(stmtRecord.getString("CURRENCY")==null?"":stmtRecord.getString("CURRENCY"));
									account.setRunningBal(RunningBalance);
									account.setTransseqNo(++TransactionSeqNo);
									account.setCustID(stmtRecord.getString("CUSTOMER_ID")==null?"":stmtRecord.getString("CUSTOMER_ID"));
								//	entryInfo = "" + stmtRecord.getString("TRANS_REFERENCE");

								//	entryInfo += "*" + dateFormat2.format(timeParser.parse("" + bookingDate));

									

									// String stmtExtraDesc = "", chequeNumber = "", valueDate = "";

									// if (stmtNarravie != null) {

									// stmtExtraDesc = stmtExtraDesc + " " + stmtNarravie;

									// }

									 if (transId.startsWith("FT")) {
										// System.out.println(entryIds[entryI] + " " + transId);

										FTStatement.setString(1, transId);
										FTStatement.setString(2, transId + ";1");
										ResultSet FTEntries = FTStatement.executeQuery();
										if (FTEntries.next()) {
//                                    
											account.setDebtAmt(FTEntries.getDouble("DEBIT_AMOUNT"));
											account.setCredAmt(FTEntries.getDouble("CREDIT_AMOUNT"));

											if (FTEntries.getString("CHEQUE_NUMBER") != null) {
												// chequeNumber = currentEntries.getString("CHEQUE_NUMBER");
												account.setChequeimgFld("Y");
											} else {
												account.setChequeimgFld("N");
											}

											if (FTEntries.getString("TRANSACTION_TYPE") != null
													&& FTEntries.getString("TRANSACTION_TYPE").equals("ACTM")
													&& FTEntries.getString("TRANSACTION_TYPE").equals("OT01")) {
												account.setSwiftInd("Y");
											} else {
												account.setSwiftInd("N");
											}

											account.setFtsInd("Y");
											account.setChequeimgID(FTEntries.getString("REF_TXN_ID")==null?"":
												FTEntries.getString("REF_TXN_ID"));

											FTStatement.clearParameters();
											FTEntries.close();
										}
										else
										{
											FTHistStatement.setString(1, transId);
											FTHistStatement.setString(2, transId + ";1");
											ResultSet FTHistEntries = FTHistStatement.executeQuery();
											if (FTHistEntries.next()) {
//	                                    
												account.setDebtAmt(FTHistEntries.getDouble("DEBIT_AMOUNT"));
												account.setCredAmt(FTHistEntries.getDouble("CREDIT_AMOUNT"));

												if (FTHistEntries.getString("CHEQUE_NUMBER") != null) {
													// chequeNumber = currentEntries.getString("CHEQUE_NUMBER");
													account.setChequeimgFld("Y");
												} else {
													account.setChequeimgFld("N");
												}

												if (FTHistEntries.getString("TRANSACTION_TYPE") != null
														&& FTHistEntries.getString("TRANSACTION_TYPE").equals("ACTM")
														&& FTHistEntries.getString("TRANSACTION_TYPE").equals("OT01")) {
													account.setSwiftInd("Y");
												} else {
													account.setSwiftInd("N");
												}

												account.setFtsInd("Y");
												account.setChequeimgID(FTHistEntries.getString("REF_TXN_ID")==null?""
														:FTHistEntries.getString("REF_TXN_ID"));

												FTHistStatement.clearParameters();
												FTHistEntries.close();
											}
										}
									} else if (transId.startsWith("TT")) {

										TellerStatement.setString(1, transId);
										TellerStatement.setString(2, transId + ";1");

										ResultSet TellerEntries = TellerStatement.executeQuery();
										if (TellerEntries.next()) {

											account.setDebtAmt(TellerEntries.getDouble("AMOUNT_LOCAL1"));
											account.setCredAmt(TellerEntries.getDouble("AMOUNT_LOCAL2"));
											account.setChequeimgFld("N");

											if (TellerEntries.getString("TRANS_TYPE") != null
													&& TellerEntries.getString("TRANS_TYPE").equals("ACTM")
													&& TellerEntries.getString("TRANS_TYPE").equals("OT01")) {
												account.setSwiftInd("Y");
											} else {
												account.setSwiftInd("N");
											}

											account.setFtsInd("N");
											account.setChequeimgID(null);

											TellerStatement.clearParameters();
											TellerEntries.close();
										}
										else
										{
											TellerHistStatement.setString(1, transId);
											TellerHistStatement.setString(2, transId + ";1");

											ResultSet TellerHistEntries = TellerHistStatement.executeQuery();
											if (TellerHistEntries.next()) {

												account.setDebtAmt(TellerHistEntries.getDouble("AMOUNT_LOCAL1"));
												account.setCredAmt(TellerHistEntries.getDouble("AMOUNT_LOCAL2"));
												account.setChequeimgFld("N");

												if (TellerHistEntries.getString("TRANS_TYPE") != null
														&& TellerHistEntries.getString("TRANS_TYPE").equals("ACTM")
														&& TellerHistEntries.getString("TRANS_TYPE").equals("OT01")) {
													account.setSwiftInd("Y");
												} else {
													account.setSwiftInd("N");
												}

												account.setFtsInd("N");
												account.setChequeimgID(null);

												TellerHistStatement.clearParameters();
												TellerHistEntries.close();
											}
										}

									}
									/*else
									{
										account.setDebtAmt(0.0);
										account.setCredAmt(0.0);
										account.setSwiftInd("0");
										account.setFtsInd("0");
										account.setChequeimgFld("0");
									}*/
								}
								stmtRecord.close();
								stmtStatement.clearParameters();

								if (detailRecord) {
									stmtDetailStatement.clearParameters();
								}
							}

							// end of stmt printed logic

							if (TransactionSeqNo != 0)
								StatementList.addAccount(account);
						}
					}
					stmtPrintedRecord.close();
					stmtPrintedStatement.clearParameters();

				}
				stmtPrintedStatement.close();
				// r08StmtPrintedStatement.close();
				stmtStatement.close();
				FTStatement.close();
				FTHistStatement.close();
				TellerStatement.close();
				TellerHistStatement.close();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				// System.out.println("**************************");
				StatementList.errCode = ERROR_CODE.NOT_FOUND;
				StatementList.errorDesc =ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue();
				return -1;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// System.out.println("***********2222222222222***************");
			StatementList.errCode = ERROR_CODE.NOT_FOUND;
			StatementList.errorDesc = ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue();
			return -1;
		} finally {
			// entrySt.close();
			// entryArc.close();
		}

		timestamp1 = new Timestamp(System.currentTimeMillis());
		System.out.println(" [AccountSummary],[generateStatement] condition Has Been Completed on [" + timestamp1 + "]");
		
		return TransactionSeqNo;
	}

	private static Vector<String> getStatementDates(Connection dbConnection, String accountNumber, String startDate,
			String endDate, String AcctStmtPrintTableName) throws Exception {
		
		
		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		System.out.println(" [AccountSummary],[StatementDates] condition Has Been Started on [" + timestamp1 + "]");
		
		
		Vector<String> statementDates = new Vector<String>();

		PreparedStatement statementDatesStatement = dbConnection
				.prepareStatement("SELECT STMT_DATE_BAL FROM " + AcctStmtPrintTableName + " WHERE ID = ?");
		statementDatesStatement.setString(1, accountNumber);

		ResultSet statementDatesRecord = statementDatesStatement.executeQuery();

		String[] tempStmtDates = null;
		String tempContent = null;

		String stmtList = null;

		if (statementDatesRecord.next()) {
			// tempContent = statementDatesRecord.getString("ENTRY_LIST");
			tempContent = statementDatesRecord.getString("STMT_DATE_BAL");
			tempContent = tempContent.replaceAll("" + ((char) 65533), "^");
			stmtList = tempContent;
		}

		statementDatesRecord.close();
		statementDatesStatement.close();

		// System.out.println("stmtList=" + stmtList);

		/*
		 * Logic to merge older statements prior to 2016/08/15
		 * 
		 * PreparedStatement dbSt = dbConnection .prepareStatement("select * from  " +
		 * tablePrefix + "ACCT$STMT$PRINT$R08 where id  = ?"); dbSt.setString(1,
		 * accountNumber); ResultSet dbRs = dbSt.executeQuery(); String arcStmtDets =
		 * null; if (dbRs.next()) { arcStmtDets = dbRs.getString("ENTRY_LIST"); if
		 * (arcStmtDets != null) { arcStmtDets = arcStmtDets.replaceAll("" + ((char)
		 * 65533), "^"); if (arcStmtDets != null && !arcStmtDets.equals("")) { stmtList
		 * = mergeSTMTDates(arcStmtDets, stmtList); // stmtList = arcStmtDets + "^" +
		 * stmtList; } }
		 * 
		 * } dbRs.close(); dbSt.close();
		 * 
		 * Logic to merge older statements prior to 2016/08/15
		 */
		if (stmtList != null && !stmtList.replaceAll("\\^", "").trim().equals("")) {

			tempStmtDates = stmtList.split("\\^");

			int tempStartDate = Integer.parseInt(startDate);

			int tempEndDate = Integer.parseInt(endDate);

			int stmtDate = 0;

			int i = 0;

			for (i = tempStmtDates.length - 1; i >= 0; i--) {

				stmtDate = Integer.parseInt(tempStmtDates[i].split("/")[0]);

				if (stmtDate >= tempStartDate) {
					continue;
				} else {
					break;
				}

			}

			int startPos = i;
			if (startPos > 0) {
				startPos = i - 1;
			}

			for (; startPos < tempStmtDates.length - 1; startPos++) {

				if (!statementDates.contains("" + stmtDate) && tempStartDate <= stmtDate) {
					statementDates.add("" + stmtDate);
				}

				stmtDate = Integer.parseInt(tempStmtDates[startPos + 1].split("/")[0]);

				if (tempEndDate <= stmtDate) {
					break;
				}

			}

			if (startPos < 0 && tempStmtDates.length > 0) {
				statementDates.add(tempStmtDates[0].split("/")[0]);
			} else if (tempEndDate > Integer.parseInt(tempStmtDates[startPos].split("/")[0])) {
				statementDates.add("" + stmtDate);
			}

			int curDate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()));
			if (Integer.parseInt(statementDates.get(statementDates.size() - 1)) > curDate
					&& startPos != tempStmtDates.length && tempStmtDates.length > 1) {
				for (; startPos < tempStmtDates.length; startPos++) {
					stmtDate = Integer.parseInt(tempStmtDates[startPos].split("/")[0]);
					if (!statementDates.contains("" + stmtDate)) {
						statementDates.add("" + stmtDate);
					}
				}
			}
		} else {

		}

		timestamp1 = new Timestamp(System.currentTimeMillis());
		System.out.println(" [AccountSummary],[StatementDates] condition Has Been Completed on [" + timestamp1 + "]");
		
		// System.out.println("statementDates=" + statementDates);
		return statementDates;

	}

	private static String getClobContent(ResultSet stmtRecord, String fieldName) throws IOException, SQLException {
		Clob clob = stmtRecord.getClob(fieldName);
		if (clob != null) {
			InputStream is = clob.getAsciiStream();
			ByteArrayOutputStream clobContent = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int read = 0;
			do {
				read = is.read(data);
				if (read == -1) {
					break;
				} else {
					clobContent.write(data, 0, read);
				}

			} while (true);
			return new String(clobContent.toByteArray());
		}

		return null;
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

	private void extractDescriptionField(String systemId, String originalSystemId, Connection dbConnection,
			String txnRef, String txnDesc, StringBuffer description, ResultSet stmtEntryRs, String unitId)
			throws Exception {

		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		System.out.println(" [AccountSummary],[extractDescriptionField] condition Has Been Started on [" + timestamp1 + "]");
		
		synchronized (paymentDetailFld) {
			if (paymentDetailFld.size() <= 0) {
				initialiseDescriptionFldMappings(dbConnection, unitId);
			}
		}

		String liveTableId = liveTables.get(systemId);
		ResultSet recordRs = null;
		boolean liveRecord = false;
		boolean histTable = false;

		PreparedStatement liveSt = null;
		PreparedStatement histSt = null;
		if (liveTableId != null) {
			liveTableId = ActualTableName.get(unitId + "-" + liveTableId);
			// liveTableId = Utilities.getTableName(dbConnection, configProperties,
			// liveTableId);
			liveSt = dbConnection.prepareStatement("SELECT * FROM " + liveTableId + " WHERE ID = ?");
			System.out.println("SELECT * FROM " + liveTableId + "  WHERE ID = '" + txnRef + "'");
			liveSt.setString(1, txnRef);
			recordRs = liveSt.executeQuery();
			if (!recordRs.next()) {
				// recordRs.close();
				liveSt = null;
			} else {
				liveRecord = true;
			}
		}

		String histTableId = null;
		if (!liveRecord) {
			histTableId = historyTables.get(systemId);
			if (histTableId != null) {
				histTableId = ActualTableName.get(unitId + "-" + histTableId);
				// histTableId = Utilities.getTableName(dbConnection, configProperties,
				// histTableId);
				histSt = dbConnection.prepareStatement("SELECT * FROM " + histTableId + " WHERE ID = ?");
				System.out.println("SELECT * FROM " + histTableId + " WHERE ID = '" + txnRef + ";1'");
				histSt.setString(1, txnRef + ";1");
				recordRs = histSt.executeQuery();
				if (!recordRs.next()) {
					recordRs.close();
					recordRs = null;
				} else {
					histTable = true;
				}
			}
		}

		/*
		 * if (systemIdMap.containsKey(originalSystemId) && !liveRecord && !histTable) {
		 * if (histTableId != null && !histTableId.trim().isEmpty()) { String hitTable =
		 * ftHistTableT24; if (systemId.toString().contains("FT")) { hitTable =
		 * ftHistTableT24; } else { hitTable = ttHistTableT24;
		 * 
		 * } hitTable = Utilities.getTableName(dbConnection, configProperties,
		 * hitTable); Utilities.addExternalSystemOfflineSyncItem(dbConnection,
		 * configProperties, hitTable, txnRef + ";1"); } throw new
		 * MissingCoreTransactionException(); }
		 */

		/*
		 * record exist read the version name and fetch mappings
		 * 
		 */
		String applName = systemId;
		if (systemIdMap.containsKey(systemId)) {
			applName = systemIdMap.get(systemId);
		}

		String descriptFldValue = null;
		System.out.println("[ " + txnRef + " ] Live Record [ " + liveRecord + " ] Hist Record [ " + histTable + " ]");
		String versionName = null;
		if (liveRecord || histTable) {
			try {
				//System.out.println("version name");
				versionName = recordRs.getString("CBA_VER_NAME");
			//	System.out.println("version name["+versionName+"]");
			} catch (Exception except) {
           //System.out.println("((((((((((((((((((((((((((");
			}
		}
		if (versionName != null && !versionName.trim().isEmpty()) {
			versionName = versionName.split(" ")[0];
		}

		System.out.println("[ " + txnRef + " ] System Id [ " + systemId + " ]");
		String mappingId = (systemIdMap.containsKey(systemId) ? systemIdMap.get(systemId) : "") + "," + versionName;

		String narr = stmtEntryRs.getString("NARRATIVE");
		if (narr != null && narr.startsWith("AC-") && systemId.substring(0, 2).equals("FT")) {
			mappingId = "DEFAULT." + systemId + ".CHG";
		}

		System.out.println("Version Name [" + mappingId + " ]");
		String paymentDetailFldName = null;

		if (mappingId != null && paymentDetailFld.containsKey(mappingId)) {
			paymentDetailFldName = paymentDetailFld.get(mappingId);
		}
		System.out.println("[ " + txnRef + " ] Mapping [ " + mappingId + " ] Payment Detail Field ["
				+ paymentDetailFldName + " ]");
		if ((paymentDetailFldName == null || paymentDetailFldName.trim().isEmpty())
				&& paymentDetailFld.containsKey(applName)) {
			paymentDetailFldName = paymentDetailFld.get(applName);
			System.out.println("[ " + txnRef + " ] Mapping [ " + applName + " ] Payment Detail Field ["
					+ paymentDetailFldName + " ]");
		}

		StringBuffer paymentFldValue = null;
		if (paymentDetailFldName != null && !paymentDetailFldName.trim().isEmpty()) {
			String[] fldNames = paymentDetailFldName.split("\\^");
			for (String fldName : fldNames) {
				descriptFldValue = null;
				if (fldName.startsWith("SE!") && fldName.endsWith("NARRATIVE")) {
					descriptFldValue = txnDesc;
				} else if (fldName.startsWith("SE!") && fldName.endsWith("TRANS.REFERENCE")) {
					descriptFldValue = stmtEntryRs.getString("TRANS_REFERENCE");
				} else {

					if (fldName != null && applFldMapgs.containsKey(applName)) {
						fldName = applFldMapgs.get(applName).get(fldName);
					}
					if (fldName != null && (liveRecord || histTable)) {
						try {
							descriptFldValue = recordRs.getString(fldName);
						} catch (Exception except) {
							except.printStackTrace();
						}
					}
				}
				if (descriptFldValue != null) {
					if (paymentFldValue == null) {
						paymentFldValue = new StringBuffer();
					} else {
						paymentFldValue.append(" ");
					}
					paymentFldValue.append(descriptFldValue);
				}
			}
		}
		if (paymentFldValue != null && paymentFldValue.length() > 0) {
			descriptFldValue = paymentFldValue.toString();
		}
		// System.out.println("Payment Detail Field CM Field Value [" + descriptFldValue
		// + " ]");

		// System.out.println("Payment Detail Field CM Field Value [" + descriptFldValue
		// + " ] [ " + systemId+ " ] Live record [ " + liveRecord + " ] History REcord [
		// " + histTable + " ]");
		// if (systemId.equals("FT") && (liveRecord || histTable)) {
		// try {
		// descriptFldValue += " " + recordRs.getString("IN_PAYMENT_DETAILS");
		// } catch (Exception except) {
		// except.printStackTrace();
		// }
		//
		// }

		if (liveSt != null) {
			liveSt.close();
		}
		
		if (histSt != null) {
			histSt.close();
		}
		
		if (recordRs != null) {
			recordRs.close();
		}

		if (descriptFldValue != null && !descriptFldValue.trim().equals("null")) {
			description.append(descriptFldValue + " ");
		}
		System.out.println("description[" + description.toString() + "]");
		
		timestamp1 = new Timestamp(System.currentTimeMillis());
		System.out.println(" [AccountSummary],[extractDescriptionField] condition Has Been Completed on [" + timestamp1 + "]");
		// return description.toString();
	}

	private void loadApplFieldMappings(Connection dbConnection, String t24applName, HashMap<String, String> fldMap,
			String unitId) throws SQLException {

		System.out.println("*********loadApplFieldMappings***********");
		String cmFieldMappingTable = ActualTableName.get(unitId + "-T24$CM$FLD$MAPPING");
		try (PreparedStatement dbSt = dbConnection
				.prepareStatement("SELECT * FROM " + cmFieldMappingTable + " WHERE T24_APPL_NAME = ?")) {
			dbSt.setString(1, t24applName);
			try (ResultSet dbRs = dbSt.executeQuery()) {
				while (dbRs.next()) {
					fldMap.put(dbRs.getString("T24_FLD_NAME"), dbRs.getString("CM_FLD_NAME"));
				}
				dbRs.close();
			}
			dbSt.close();
		}

	}

	private void initialiseDescriptionFldMappings(Connection dbConnection, String unitId) throws Exception {

		System.out.println("*********initialiseDescriptionFldMappings***********");
		String paymentDetails = null;
		String remitMappingTable = ActualTableName.get(unitId + "-CBA$REMIT$FIELD$MAPPING");
		try (PreparedStatement mappingSt = dbConnection.prepareStatement("SELECT * FROM " + remitMappingTable);
				ResultSet mappingRs = mappingSt.executeQuery()) {
			while (mappingRs.next()) {
				paymentDetails = mappingRs.getString("PAYMENT_DETAILS");
				if (paymentDetails != null && !paymentDetails.trim().isEmpty()) {
					paymentDetailFld.put(mappingRs.getString("ID"), paymentDetails);
				}
			}
			mappingRs.close();
			mappingSt.close();
		}

		// [PAYMENT.DETAILS^ORDERING.CUST^TELEX.FROM.CUST^CREDIT.THEIR.REF^DEBIT.THEIR.REF^FX.REFERENCE^IN.BK.TO.BK
		// ]

		applFldMapgs = new HashMap<String, Map<String, String>>();
		HashMap<String, String> fldMap = new HashMap<String, String>();
		loadApplFieldMappings(dbConnection, ftAppl, fldMap, unitId);
		applFldMapgs.put(ftAppl, fldMap);

		fldMap = new HashMap<String, String>();
		loadApplFieldMappings(dbConnection, ttAppl, fldMap, unitId);
		applFldMapgs.put(ttAppl, fldMap);

		fldMap = new HashMap<String, String>();
		loadApplFieldMappings(dbConnection, acChgReqAppl, fldMap, unitId);
		applFldMapgs.put(acChgReqAppl, fldMap);

		historyTables.put("FT", "FUNDS$TRANSFER_HISTORY");
		historyTables.put("TT", "TELLER_HISTORY");
		historyTables.put("AC", "AC$CHARGE$REQUEST$HIS");

		liveTables.put("FT", "FUNDS$TRANSFER");
		liveTables.put("TT", "TELLER");
		liveTables.put("AC", "AC$CHARGE$REQUEST");
	
		systemIdMap.put("FT", ftAppl);
		systemIdMap.put("TT", ttAppl);
		systemIdMap.put("AC", acChgReqAppl);
		systemIdMap.put("FX", fxAppl);
	}
	
	private void ResponseMessages(AccountStatementList accountStmtList,String unitId,String AccountNumber,String StDate,
			String EnDate,ERROR_CODE ErrorCode,String ErrorDescription)
	{
		//accountStmtList = new AccountStatementList();
		accountStmtList.unitID = unitId;
		accountStmtList.accNumber = AccountNumber;
		accountStmtList.fromDate = StDate;
		accountStmtList.toDate = EnDate;
		accountStmtList.errCode = ErrorCode;
		accountStmtList.errorDesc = ErrorDescription;
		
	}

}
