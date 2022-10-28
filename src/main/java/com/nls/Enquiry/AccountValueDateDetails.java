package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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


@Path("/AccountValue")
public class AccountValueDateDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String AccTablename = "ACCOUNT";
	private static String AccActivityRef = "ACCT_ACTIVITY";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("ddMMyyyyhhmmss");
	private static Map<String, String> fldMap = new HashMap<String, String>();
	static {
		
		fldMap.put("DAY_NO", "1");
		fldMap.put("TURNOVER_CREDIT", "2");
		fldMap.put("TURNOVER_DEBIT", "3");
		fldMap.put("BALANCE", "4");

		fldMap.put("BK_DAY_NO", "15");
		fldMap.put("BK_BALANCE", "16");
		fldMap.put("BK_CREDIT_MVMT", "17");
		fldMap.put("BK_DEBIT_MVMT", "18");
	}

	public static void setDBPool(DataSource cmDBPool) {
		AccountValueDateDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		AccountValueDateDetails.ActualTableName = ActualTableName;
	}
	
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = AccountValueDateList.class, responseDescription = "Account ValueDate Response", responseCode = "200")
	@Operation(summary = "Account Value Request", description = "returns Account ValueDate data")
	public Response getAccountValueDateDetails(
			@RequestBody(description = "Account Value Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountValueDateRequest.class))) AccountValueDateRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Account ValueDate Details Interface Started on ["+startTime+"]");
			AccountValueDateObject accountValue = null;
			AccountValueDateList accountList = new AccountValueDateList();
			boolean flag;
			
			Connection dbConnection = cmDBPool.getConnection();

			String unitIds = "";
			String unitidnumber = "";
			String accountNumber = "";
			String valuedate = "";
			String AccTable = "";
			String AccActivityTable="";
			ArrayList<Boolean>RespStatus=new ArrayList<Boolean>();
			for (int i = 0; i < id.accountValue.size(); i++) {
				
				flag = false;
						
				unitIds = id.accountValue.get(i).unitId;
				accountNumber = id.accountValue.get(i).accountNo;
				valuedate = id.accountValue.get(i).valueDate;

				if (!unitIds.equalsIgnoreCase(unitidnumber)) {
					unitidnumber = unitIds;
					System.out.println(
							"AccountValueDateDetails Tables Ref [ " + unitidnumber + " ]  [" + AccTablename + "],["+AccActivityRef+"]");
					AccTable = ActualTableName.get(unitidnumber + "-" + AccTablename);
					AccActivityTable = ActualTableName.get(unitidnumber + "-" + AccActivityRef);
					System.out.println(
							"AccountValueDateDetails Actual Table Ref [ " + unitidnumber + " ]  [" + AccTable + "],["+AccActivityTable+"]");
				}

				System.out.println("Fetching Account ValueDate Request Fields For Procode: [ " + id.procCode
						+ " ] UnitId: [ " + unitidnumber + " ]" + " Accountno: [ " + accountNumber + " ] Valuedate: [ "
						+ valuedate + " ]");

				try (PreparedStatement dbSt = dbConnection
								.prepareStatement("SELECT ID,CURRENCY,TO_CHAR(TIME_STAMP,'DDMMYYYYHH24MISS') as TIMESTAMP,"
										+ " BRANCH_CODE,OPENING_DATE FROM " + AccTable + " WHERE ID=?")) {
					dbSt.setString(1, accountNumber);
					try (ResultSet dbRs = dbSt.executeQuery()) {
						
						if (dbRs.next()) {
							flag = true;
							RespStatus.add(flag);
							accountValue = new AccountValueDateObject();
							accountValue.setUnitId(unitidnumber);
							accountValue.setAccountNo(dbRs.getString("ID"));
							accountValue.setCurrency(dbRs.getString("CURRENCY"));
							accountValue.setAccountBranch(dbRs.getString("BRANCH_CODE"));
							accountValue.setValueDate(valuedate);
							LocalDate openingDate = dbRs.getDate("OPENING_DATE").toLocalDate();
							String EndDate = TimeFormat2.format(DateFormtter.parse("" + valuedate));
							double openingBalance = getAccountActivityBalance(dbConnection,
									openingDate, dbRs.getString("ID"),
									EndDate, "BK_DAY_NO", "BK_BALANCE",unitidnumber);
			              	System.out.println("Opening Balance ["+openingBalance+"]");
							accountValue.setValueDateBalance(openingBalance);
							
							accountValue.setLastBalanceTimestamp(dbRs.getString("TIMESTAMP"));

							accountList.addAccount(accountValue);
						}
						else
						{
							RespStatus.add(flag);
							ResponseMessages(accountList,accountValue,
									unitidnumber,accountNumber,valuedate,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.ACCOUNT_NOT_FOUND.getValue());
						}
						dbRs.close();
					}
					catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						RespStatus.add(flag);
						ResponseMessages(accountList,accountValue,
								unitidnumber,accountNumber,valuedate,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
						accountList.addAccount(accountValue);						
					}
					
				}catch (Exception except) {
					except.printStackTrace();
					RespStatus.add(flag);				
					ResponseMessages(accountList,accountValue,
							unitidnumber,accountNumber,valuedate,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				}

			}
			
			if(!RespStatus.contains(true))
			{
				accountList.setErrCode(ERROR_CODE.NOT_FOUND);
				accountList.setErrorDesc(ErrorResponseStatus.FAILURE.getValue());
			}

			return Response.status(Status.ACCEPTED).entity(accountList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Account ValueDate Details Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
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
	
	private void ResponseMessages(AccountValueDateList accountList,AccountValueDateObject accountValue,
			String unitidnumber,String accountNumber,String valuedate,ERROR_CODE ErrorCode,String ErrorDescription)
	{
		accountValue = new AccountValueDateObject();
		accountValue.setUnitId(unitidnumber);
		accountValue.setAccountNo(accountNumber);
		accountValue.setValueDate(valuedate);
		accountValue.setErCode(ErrorCode);
		accountValue.setErMsg(ErrorDescription);
		accountList.addAccount(accountValue);						
	}

}
