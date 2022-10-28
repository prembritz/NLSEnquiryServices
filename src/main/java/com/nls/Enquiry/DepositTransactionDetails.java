package com.nls.Enquiry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
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

@Path("/DepositTransaction")
public class DepositTransactionDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;

	private static String AccountTable = "ACCOUNT";
	private static String ActivityTable = "ACCT_ACTIVITY";
	private static String StatementEntryTable = "STATEMENT$ENTRY";
	private static String FundTransferTable = "FUNDS$TRANSFER";
	private static String StmtPrintedTable = "STMT$PRINTED";
	private static String BnkTellerTable = "TELLER";
	private static String AcctStmtPrintTable = "ACCT$STMT$PRINT";
	private static String StmtEntryDetailsTable = "STATEMENT$ENTRY$DETAILS";
	private static String StmtEntryDetailXREFTable = "STMT_ENTRY_DETAIL_XREF";
	private static String MissItemsTable = "MISSING$ITEMS";
	
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static void setDBPool(DataSource cmDBPool) {
		DepositTransactionDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		DepositTransactionDetails.ActualTableName = ActualTableName;
	}


	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = DepositTransactionList.class, responseDescription = "Deposit Transaction Response", responseCode = "200")
	@Operation(summary = "Deposit Transaction Request", description = "returns Deposit Transaction")
	public Response getDepositTransactionDetails(
			@RequestBody(description = "Loan Account Number", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepositTransactionRequest.class))) DepositTransactionRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Deposit Transaction Details Interface Started on ["+startTime+"]");
			
			String unitId=id.unitId;
			String ProCode=id.proCode;
			String DepositAccNumber=id.depositAccountno;
			String stDate=id.fromDate;
			String enDate=id.toDate;
			
			System.out.println("Fetching Deposit Transaction For [ " + ProCode + " ] UnitId: [ " + unitId + " ] [ " + DepositAccNumber
					+ " ] " + " [ " + stDate + " ] [ " + enDate + " ]");
			
			System.out.println("DepositSummaryDetails Table Ref's [ " + unitId + " ] [" + AccountTable + "] [" + AcctStmtPrintTable + "] ["
					+ BnkTellerTable + "] [" + StmtPrintedTable + "] [" + StatementEntryTable + "]"
					+ "["+ActivityTable+"] ["+FundTransferTable+"] ["+StmtEntryDetailsTable+"] ["+StmtEntryDetailXREFTable+"] ["
					+ MissItemsTable + "]");

			String accTab = ActualTableName.get(unitId + "-" + AccountTable);
			String acctStmtPrintTab = ActualTableName.get(unitId + "-" + AcctStmtPrintTable);
			String bnkTellerTab = ActualTableName.get(unitId + "-" + BnkTellerTable);
			String stmtPrintedTab = ActualTableName.get(unitId + "-" + StmtPrintedTable);
			String stmtEntryTab = ActualTableName.get(unitId + "-" + StatementEntryTable);
			String activityTab = ActualTableName.get(unitId + "-" + ActivityTable);
			String fundTab = ActualTableName.get(unitId + "-" + FundTransferTable);
			String stmtEntryDetailsTab = ActualTableName.get(unitId + "-" + StmtEntryDetailsTable);
			String stmtEntryDetailXREFTab = ActualTableName.get(unitId + "-" + StmtEntryDetailXREFTable);
			String missItemTab = ActualTableName.get(unitId + "-" + MissItemsTable);

			System.out.println("DepositSummaryDetails Actual Table Names [" + unitId+ " ] [" + accTab + "] [" + acctStmtPrintTab + "] ["
					+ bnkTellerTab + "] [" + stmtPrintedTab + "] [" + stmtEntryTab + "]"
					+ "["+activityTab+"] ["+fundTab+"] ["+stmtEntryDetailsTab+"] ["+stmtEntryDetailXREFTab+"] ["
					+ missItemTab + "]");


			DepositTransactionList depositList = new DepositTransactionList();
			DepositTransactionObject deposit = null;
			int TotalTxn = 0;
			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection
							.prepareStatement("select ID,CURRENCY,OPENING_DATE from " + accTab + " WHERE id =? ")) {
				dbSt.setString(1, DepositAccNumber);
				try (ResultSet dbRs = dbSt.executeQuery()) {
					boolean Exists = false;
					if (dbRs.next()) {
						Exists = true;
				
						String StartDate = TimeFormat2.format(DateFormtter.parse("" + stDate));
						String EndDate = TimeFormat2.format(DateFormtter.parse("" + enDate));
						LocalDate openingDate = dbRs.getDate("OPENING_DATE").toLocalDate();

						
						double openingBalance = getAccountActivityBalance(dbConnection,
								openingDate, dbRs.getString("ID"),
								StartDate, "BK_DAY_NO", "BK_BALANCE",unitId);
		              	System.out.println("Opening Balance ["+openingBalance+"]");
		              			     
						Vector<String> statementDates = getStatementDates(dbConnection, DepositAccNumber,
								StartDate + "", EndDate, "", acctStmtPrintTab);

						Vector<String> modifiedDates = new Vector<String>();
						for (String stmtDate : statementDates) {
							if (!modifiedDates.contains(stmtDate)) {
								modifiedDates.add(stmtDate);
								// System.out.println(stmtDate);
							}
						}
						statementDates = modifiedDates;
						// System.out.println("statement Dates ="+statementDates);

						TotalTxn=generateStatement(dbConnection, DepositAccNumber, "", StartDate, EndDate, statementDates,
								openingBalance, dbRs.getString("CURRENCY"), deposit,
								depositList, stmtEntryTab, stmtPrintedTab, missItemTab, stmtEntryDetailXREFTab, fundTab,
								bnkTellerTab, stmtEntryDetailsTab);

						System.out.println("TotalTxn=" + TotalTxn);
						
						if (TotalTxn == 0) {
							ResponseMessages(depositList,unitId,DepositAccNumber,
									ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TRANSACTION_NOT_FOUND.getValue());
							return Response.status(Status.ACCEPTED).entity(depositList).build();
						} 
						else if(TotalTxn>0){
							depositList.setUnitId(unitId);
							depositList.setAccNo(DepositAccNumber);	
							}

					}
					if (!Exists) {
						ResponseMessages(depositList,unitId,DepositAccNumber,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(depositList).build();
					}

					dbRs.close();
					dbSt.clearParameters();
				}
				catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(depositList,unitId,DepositAccNumber,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(depositList).build();
				}
				dbSt.close();
			}catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(depositList,unitId,DepositAccNumber,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(depositList).build();
			}
			return Response.status(Status.ACCEPTED).entity(depositList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Deposit Transaction Details Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}

	}
	
	public double getAccountActivityBalance(Connection dbConnection,
			LocalDate openingDate, String accountId, String activityDate, String dayFld, String balanceField,
			String UnitId)
			throws SQLException {
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
			String tableAcctActivity = ActualTableName.get(UnitId+"-ACCT_ACTIVITY");
			String tableAcctBalanceActivity = ActualTableName.get(UnitId+"-ACCT$BALANCE$ACTIVITY");
			String tableAcctActivityBalanceType = ActualTableName.get(UnitId+"-ACCT_ACTIVITY$BALANCE_TYPE");
			ACCOUNT_TYPE acctType = AccountUtilities.getAccountType(accountId, dbConnection,UnitId);
			//tableAcctActivity = AccountUtilities.getTableName(dbConnection, configProperties, tableAcctActivity);
			String sql = "SELECT * FROM " + tableAcctActivity + " WHERE ID = ?";
			String accountTable = ActualTableName.get(UnitId+"-ACCOUNT");
			String arrangementReference = AccountUtilities.getStringField(dbConnection, accountTable, "ID",
					accountId, "ARRANGMENT_ID");
			if (acctType == ACCOUNT_TYPE.LENDING || acctType == ACCOUNT_TYPE.DEPOSITS
					|| (acctType == ACCOUNT_TYPE.ACCOUNTS && arrangementReference != null
							&& !arrangementReference.isEmpty())) {
				//tableAcctActivity = ActualTableName.get(UnitId+"-ACCT_ACTIVITY$BALANCE_TYPE");
				//tableAcctActivity = AccountUtilities.getTableName(dbConnection, configProperties, tableAcctActivity);
				//System.out.println("***************Calling Deposit Accounts****************");
				String balanceType = "CURBALANCE";
				sql = "SELECT * FROM " + tableAcctActivityBalanceType + " WHERE BALANCE_TYPE = '" + balanceType
						+ "' AND ID = ?";
			}
			boolean activityFound = false;
			try (PreparedStatement acctSt = dbConnection.prepareStatement(sql)) 
			{
				ACTIVITY_LOOP: while (openingDate.isBefore(actDate) || openingDate.isEqual(actDate)) {
					actMonth = actDate.format(fmt);
				
					System.out.println("Trying To Generate Activity For  [ " + accountId + " ] [ " + actMonth + " ]");
					AccountActivityByBalanceTypeGenerator.generateActivityByBalanceType(dbConnection,
							accountId, actMonth,tableAcctBalanceActivity,tableAcctActivityBalanceType);
					acctSt.setString(1, accountId + "-" + actMonth);
				//	System.out.println(accountId + "-" + actMonth);
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
						//	System.out.println("***************Record called************");
							days = acctRs.getString(dayFld);
							bals = acctRs.getString(balanceField);
							if (days != null && !days.trim().isEmpty()) {
								dayList = AccountUtilities.split(days, "^");
								balList = AccountUtilities.split(bals, "^");
								System.out.println("Trying to Fetch Activity Record [ " + accountId + "-" + actMonth
										+ " ] Activity Days [ " + activityDay + " ] [ " + dayList + " ] "
										+ " Required Month [ " + requiredBalanceActivityMonth + " ]");
								if (requiredBalanceActivityMonth)
								{
									/*
									 * if activity exist for the required balance month then evaluate the date
									 */

									if (dayList.contains(activityDay)) 
									{
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
										for (; activityDayNoI > 0 && Integer.parseInt(dayList.get(activityDayNoI)) > activityDayNo; activityDayNoI--);
										
										
										if (balList.size() > activityDayNoI && activityDayNoI >= 0	&& Integer.parseInt(dayList.get(activityDayNoI)) < Integer.parseInt(activityDay)) 
										{
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
			}
		} catch (Exception except) {
			balance = 0.00;
		}
		return balance;
	}

	private Integer generateStatement(Connection dbConnection, String accountNumber, String tablePrefix,
			String startDate, String endDate, Vector<String> statementDates, Double openingBalance,
			String localCurrency, DepositTransactionObject Loan, DepositTransactionList LoanStmtList,
			String StatementEntryTable, String stmtPrint, String missItemTab, String stmtEntryDetailXREFTab,
			String fundTab, String bnkTellerTab, String stmtEntryDetailsTab) throws Exception {

		Vector<String> statementInfo = new Vector<String>();

		DecimalFormat amountFormat = new DecimalFormat("0.00");

		double RunningBalance = openingBalance;

		PreparedStatement stmtStatement = dbConnection
				.prepareStatement("SELECT ID,to_char(to_date(booking_date),'yyyyMMdd') AS BKG_DATE,"
						+ " TO_CHAR(to_date(PROCESSING_DATE,'yyyy-mm-dd'),'DDMMYYYY') as PROCESSING_DATE,NARRATIVE,"
						+ " CUSTOMER_ID,AMOUNT_FCY,AMOUNT_LCY,CURRENCY,TRANS_REFERENCE FROM " + StatementEntryTable
						+ " WHERE ID = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet stmtRecord = null;

		int no_of_statements = statementDates.size();

		PreparedStatement stmtPrintedStatement = dbConnection
				.prepareStatement("SELECT * FROM " + stmtPrint + " WHERE ID = ?");

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

		PreparedStatement entrySt = dbConnection.prepareStatement("INSERT INTO " + missItemTab + " VALUES(?,?)");

		PreparedStatement entryArc = dbConnection.prepareStatement("INSERT INTO " + missItemTab + " VALUES(?,?)");

		// boolean recordExists = false;

		PreparedStatement stmtDetailStatement = dbConnection.prepareStatement(
				"SELECT * FROM " + stmtEntryDetailsTab + " WHERE ID = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		String detailReferencesTable = null;
		detailReferencesTable = stmtEntryDetailXREFTab;
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

		int TransactionSeqNo = 0;
		try {
			PRINTED_LOOP: for (int stmtPrintedI = 0; stmtPrintedI < no_of_statements; stmtPrintedI++) {

				stmtPrintedDate = statementDates.get(stmtPrintedI);
				// System.out.println("Statement Dates =" + stmtPrintedDate);
				entryListString = "";
				r08finalStatement = false;

				stmtPrintedStatement.setString(1, accountNumber + "-" + stmtPrintedDate);
				stmtPrintedRecord = stmtPrintedStatement.executeQuery();
				// }

				String entryInfo = "";

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

						Loan = new DepositTransactionObject();

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
//	                      System.out.println("Entry Id " + entryIds[entryI]);
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

							// System.out.println("****************calling statement
							// Entry******************"+stmtRecord.getString("BKG_DATE"));
							bookingDate = Integer.parseInt(stmtRecord.getString("BKG_DATE").replaceAll("-", ""));

							System.out.println(tempStartDate);
							System.out.println(bookingDate);
							System.out.println(tempEndDate);
							if (tempStartDate <= bookingDate && bookingDate <= tempEndDate) {
								System.out.println("****************************************");
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

								Loan.setTransactionDate(stmtRecord.getString("PROCESSING_DATE").replaceAll("-", ""));
								Loan.setReferenceNo(stmtRecord.getString("ID"));
								String stmtNarravie = getClobContent(stmtRecord, "NARRATIVE");
								Loan.setNarrative(stmtNarravie);
								Loan.setRunningBalance(RunningBalance);
								Loan.setTxnSeqNo(++TransactionSeqNo);

								String transId = stmtRecord.getString("TRANS_REFERENCE");
								if (transId.indexOf("\\") != -1) {
									transId = transId.substring(0, transId.indexOf("\\"));
								}

								if (transId.startsWith("FT")) {
									// System.out.println(entryIds[entryI] + " " + transId);
									PreparedStatement currentStatement = dbConnection.prepareStatement(
											"select DEBIT_AMOUNT,CREDIT_AMOUNT,CHEQUE_NUMBER,TRANSACTION_TYPE"
													+ " from " + fundTab + " where ID = ? or ID = ?");
									currentStatement.setString(1, transId);
									currentStatement.setString(2, transId + ";1");
									ResultSet currentEntries = currentStatement.executeQuery();
									if (currentEntries.next()) {

										Loan.setDebitAmount(currentEntries.getDouble("DEBIT_AMOUNT"));
										Loan.setCreditAmount(currentEntries.getDouble("CREDIT_AMOUNT"));

										currentStatement.clearParameters();
										currentEntries.close();
									}
								} else if (transId.startsWith("TT")) {

									PreparedStatement currentStatement = dbConnection
											.prepareStatement("select AMOUNT_LOCAL1,AMOUNT_LOCAL2,TRANS_TYPE from "
													+ bnkTellerTab + " where id = ? or id = ?");

									currentStatement.setString(1, transId);
									currentStatement.setString(2, transId + ";1");

									ResultSet currentEntries = currentStatement.executeQuery();
									if (currentEntries.next()) {

										Loan.setDebitAmount(currentEntries.getDouble("AMOUNT_LOCAL1"));
										Loan.setCreditAmount(currentEntries.getDouble("AMOUNT_LOCAL2"));

										currentStatement.close();
										currentEntries.close();
									}
								}
							}
							stmtRecord.close();
							stmtStatement.clearParameters();

							if (detailRecord) {
								stmtDetailStatement.clearParameters();
							}
						}

						// end of stmt printed logic

						LoanStmtList.addAccount(Loan);
					}
				}
				stmtPrintedRecord.close();
				stmtPrintedStatement.clearParameters();

			}
			stmtPrintedStatement.close();
			// r08StmtPrintedStatement.close();
			stmtStatement.close();

		} finally {
			entrySt.close();
			entryArc.close();
		}

		return TransactionSeqNo;
	}

	private static Vector<String> getStatementDates(Connection dbConnection, String accountNumber, String startDate,
			String endDate, String tablePrefix, String accountPrintTab) throws Exception {
		Vector<String> statementDates = new Vector<String>();

		/*
		 * PreparedStatement accountStatementStatement = dbConnection
		 * .prepareStatement("SELECT * FROM " + tablePrefix +
		 * "ACCOUNT$STATEMENT WHERE ID = ?"); accountStatementStatement.setString(1,
		 * accountNumber);
		 * 
		 * ResultSet accountStatement = accountStatementStatement.executeQuery();
		 * 
		 * int currentStmtDate = 0;
		 * 
		 * if (accountStatement.next()) { currentStmtDate =
		 * Integer.parseInt(accountStatement.getString("STMNT_FREQ").substring(0, 8));
		 * 
		 * }
		 * 
		 * accountStatement.close(); accountStatementStatement.close();
		 */

		PreparedStatement statementDatesStatement = dbConnection
				.prepareStatement("SELECT STMT_DATE_BAL FROM " + accountPrintTab + " WHERE ID = ?");
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

		System.out.println("stmtList=" + stmtList);

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

		System.out.println("statementDates=" + statementDates);
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

	private void ResponseMessages(DepositTransactionList depositList,String unitId,String DepositAccNumber,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		depositList.setUnitId(unitId);
		depositList.setAccNo(DepositAccNumber);	
		depositList.setErrCode(ErrorCode);
		depositList.setErrorDesc(ErrorDescription);
	}
}
