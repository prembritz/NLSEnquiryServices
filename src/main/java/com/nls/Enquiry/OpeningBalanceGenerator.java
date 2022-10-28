package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Vector;

public class OpeningBalanceGenerator {
	
	private static HashMap<String, String> ActualTableName = null;
	
	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		OpeningBalanceGenerator.ActualTableName = ActualTableName;
	}
	
	public static double getAccountActivityBalance(Connection dbConnection,
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
}
