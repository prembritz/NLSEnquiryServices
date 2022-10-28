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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

@Path("/ExportLCBill")
public class ExportLCBillCollectionDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static Map<String, Integer> AccountTypes = new HashMap<String, Integer>();
	private static String AccTableName = "ACCOUNT";
	private static String CustomerTable = "CUSTOMER";
	private static String CategoryTable = "CATEGORY";
	private static String AAArrangeTable = "AA_ARRANGEMENT";
	private static String AAArrangeDetails = "AA$ACCOUNT$DETAILS";
	private static String AaActivityTable = "ACCT_ACTIVITY";
	private static String arrTermTable = "AA$ARR$TERM$AMOUNT";
	
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");
	

	private enum AccountStatus {
		N;
	}

	static {
		AccountTypes.put("ACCOUNTS", 1);
		AccountTypes.put("DEPOSITS", 2);
		AccountTypes.put("LENDING", 3);
	}

	public static void setDBPool(DataSource cmDBPool) {
		ExportLCBillCollectionDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		ExportLCBillCollectionDetails.ActualTableName = ActualTableName;
	}


	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = ExportLCBillCollectionList.class, responseDescription = "Export LC Bill Collection Details Response", responseCode = "200")
	@Operation(summary = "Export LC Bill Collection Details Request", description = "returns Export LC Bill Collection Details data")
	public Response getExportLCBillCollectionDetails(
			@RequestBody(description = "Export LC Bill Collection Details Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExportLCBillCollectionRequest.class))) ExportLCBillCollectionRequest id) {

		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Export LC Bill Collection Interface Started on ["+startTime+"]");
			String unitId = id.unitId;
			String custId=id.customerId;
			String ref=id.referenceNumber;
			String RequestTime=id.requestTime;
			
			System.out.println("Fetching Export LC Bill Collection Request fields for Reference number: [ "
					+ ref + " ] " + "customerid: [ " + custId + " ] RequestTime: [ " + id.requestTime
					+ " ] unitid: [ " + unitId + " ]");

			System.out.println("ExportLCBillCollectionDetails Table Ref's [ " + unitId + " ] [" + AccTableName + "] ["
					+ CustomerTable + "] [" + CategoryTable + "] [" + AAArrangeTable + "] [" + AAArrangeDetails + "] ["
					+ AaActivityTable + "] ["+arrTermTable+"]");

			String accTable = ActualTableName.get(unitId + "-" + AccTableName);
			String customerTable = ActualTableName.get(unitId + "-" + CustomerTable);
			String categoryTable = ActualTableName.get(unitId + "-" + CategoryTable);
			String aarrangeTable = ActualTableName.get(unitId + "-" + AAArrangeTable);
			String arrangeDetailsTable = ActualTableName.get(unitId + "-" + AAArrangeDetails);
			String activityTable = ActualTableName.get(unitId + "-" + AaActivityTable); 
			String arrTable = ActualTableName.get(unitId + "-" + arrTermTable);

			System.out.println("ExportLCBillCollectionDetails Actual Table Names [ " + unitId + " ] [" + accTable
					+ "] [" + customerTable + "] [" + categoryTable + "] [" + aarrangeTable + "] ["
					+ arrangeDetailsTable + "] [" + activityTable + "] ["+arrTable+"]");

			ExportLCBillCollectionObject export = null;
			ExportLCBillCollectionList exportList = new ExportLCBillCollectionList();
			int i = 0;

			boolean flag = false;
			try (Connection dbConnection = cmDBPool.getConnection();
			    PreparedStatement dbSt = dbConnection.prepareStatement(
							"SELECT ID,BRANCH_CODE,CATEGORY,CURRENCY,ARRANGMENT_ID,ACCOUNT_TITLE,CUSTOMER_ID,"
							+ " ONLINE_WORKING_BALANCE,CASE WHEN INACTIV_MARKER is null THEN 'N' ELSE 'Y' END AS ACCOUNT_STATUS,OPENING_DATE "
									+ " FROM " + accTable + " WHERE CUSTOMER_ID=?")) {
				dbSt.setString(1, custId);

				try (ResultSet dbRs = dbSt.executeQuery()) {
					while (dbRs.next()) {
						flag = true;
						export = new ExportLCBillCollectionObject();
						if (++i == 1) {
							exportList.setUnitId(unitId);
							exportList.setReferenceNumber(ref);
						}
						
						String EndDate = TimeFormat2.format(DateFormtter.parse("" + RequestTime));
						LocalDate openingDate = dbRs.getDate("OPENING_DATE").toLocalDate();

						export.setInputAcctCategory(dbRs.getString("CATEGORY"));
						export.setAcctType(AccountType(dbConnection, dbRs.getString("ARRANGMENT_ID"), aarrangeTable));
						export.setAcctCategory(dbRs.getString("CATEGORY"));
						export.setDetailsAsof("");
						export.setSystemModule("");
						export.setCustomerNumber(custId);

						export.setAcctSubType("");

						export.setAcctTypeDesc(
								getCategoryName(dbConnection, dbRs.getString("CATEGORY"), categoryTable));

						export.setAccountSubTypeDesc("");
						export.setShadowInstrument("");
						export.setAccountName(dbRs.getString("ACCOUNT_TITLE"));

						export.setCustomerName(
								getCustomerData(dbConnection, dbRs.getString("CUSTOMER_ID"), customerTable));

						export.setCreditLineid(dbRs.getString("ARRANGMENT_ID"));

						export.setCreditLineDesc(
								getProductName(dbConnection, dbRs.getString("ARRANGMENT_ID"), aarrangeTable));

						export.setCreditLineAmount(DisbursementAmount(dbConnection, dbRs.getString("ARRANGMENT_ID"),arrTable));
						
						double openingBalance = getAccountActivityBalance(dbConnection,
								openingDate, dbRs.getString("ID"),
								EndDate, "BK_DAY_NO", "BK_BALANCE",unitId);
						
		              	System.out.println("Opening Balance ["+openingBalance+"]");
		              	
						export.setTotOutstnd(openingBalance);

						export.setInstrumentIdNum("");
						export.setAcctNumber(dbRs.getString("ID"));
						export.setOpenAmount(Double.parseDouble("0"));
						export.setCurrentBalance(dbRs.getDouble("ONLINE_WORKING_BALANCE"));
						export.setCurrencyCode(dbRs.getString("CURRENCY"));

						export.setExchangeRate(Double.parseDouble("0"));
						export.setMaturityDate("");

						export.setAccountStatus(
								dbRs.getString("ACCOUNT_STATUS").equalsIgnoreCase(AccountStatus.N.toString()) == true
										? AccountStatusCheck(dbConnection, dbRs.getString("ARRANGMENT_ID"),
												arrangeDetailsTable)
										: dbRs.getString("ACCOUNT_STATUS"));
						export.setDepmarginBalance(Double.parseDouble("0"));
						export.setProductName("");
						export.setOutStndinaed(Double.parseDouble("0"));
						export.setArindicator("");
						export.setRenewalDate("");
						export.setBranchNumber(dbRs.getString("BRANCH_CODE"));

						exportList.addAccount(export);
					}

					dbRs.close();

					if (!flag) {
						ResponseMessages(exportList,unitId,ref,custId,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(exportList).build();
					}
				}
				catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(exportList,unitId,ref,custId,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(exportList).build();
				}
				dbSt.close();
			}catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(exportList,unitId,ref,custId,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(exportList).build();
			}
			
			return Response.status(Status.ACCEPTED).entity(exportList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Export LC Bill Collection Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
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

	private Double DisbursementAmount(Connection dbconnection, String ArrangementID,String arrTable) {
		Double DisbursementAmt = 0.0;
		try {
			PreparedStatement ArrTermPS = dbconnection.prepareStatement("select amount from "+arrTable+" "
					+ " where arrangement_id=? order by REGEXP_SUBSTR(id, '[^-]+$') desc");
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
		System.out.println("DisbursementAmt [ " + DisbursementAmt + " ]");

		return DisbursementAmt;
	}

	private String AccountStatusCheck(Connection dbConnection, String arrId, String arrangeDetailsTable) {

		String AccStatus = "N";

		try {
			PreparedStatement dbSt = dbConnection
					.prepareStatement("SELECT ARR_DORMANCY_STATUS FROM " + arrangeDetailsTable + " WHERE ID = ?");
			dbSt.setString(1, arrId);
			ResultSet dbRs = dbSt.executeQuery();
			if (dbRs.next()) {
				if ((dbRs.getString("ARR_DORMANCY_STATUS") != null
						&& (dbRs.getString("ARR_DORMANCY_STATUS").contains("INACTIVE")
								|| dbRs.getString("ARR_DORMANCY_STATUS").contains(
										"DORMANT"))) /*
														 * || (dbRs.getString("DORMANCY_STATUS") != null &&
														 * (dbRs.getString("DORMANCY_STATUS").contains("INACTIVE") ||
														 * dbRs.getString("DORMANCY_STATUS").contains("DORMANT")))
														 * 
														 */) {

					AccStatus = "Y";

				}
			}
			dbRs.close();
			dbSt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return AccStatus;
	}

	private int AccountType(Connection dbConnection, String Arrangement_ID, String AAArrangementTable)
			throws SQLException

	{
		int AccountType = 1;

		PreparedStatement ArrangementPS = dbConnection
				.prepareStatement("select PRODUCT_LINE from " + AAArrangementTable + " where id = ? ");
		ArrangementPS.setString(1, Arrangement_ID);
		ResultSet ArrangementRS = ArrangementPS.executeQuery();
		if (ArrangementRS.next()) {
			System.out.println("Product Line [" + ArrangementRS.getString(1) + "]");
			AccountType = AccountTypes.get(ArrangementRS.getString(1)) != null
					? AccountTypes.get(ArrangementRS.getString(1))
					: 1;
		}
		ArrangementPS.close();
		ArrangementRS.close();

		System.out.println("AccountType[" + AccountType + "]");

		return AccountType;
	}

	private String getCategoryName(Connection dbConnection, String category, String categoryTab) {
		String description = "";
		try {
			PreparedStatement categoryPs = dbConnection
					.prepareStatement("SELECT DESCRIPTION FROM " + categoryTab + " WHERE ID=?");
			categoryPs.setString(1, category);
			ResultSet categoryRs = categoryPs.executeQuery();
			if (categoryRs.next()) {
				description = categoryRs.getString("DESCRIPTION");
			}
			categoryRs.close();
			categoryPs.clearParameters();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return description;
	}

	private String getCustomerData(Connection dbConnection, String customerId, String customerTab) {
		String customerName = "";
		try {
			PreparedStatement customerPs = dbConnection
					.prepareStatement("SELECT NAME FROM " + customerTab + " WHERE ID=?");
			customerPs.setString(1, customerId);
			ResultSet customerRS = customerPs.executeQuery();
			if (customerRS.next()) {
				customerName = customerRS.getString("NAME");
			}
			customerRS.close();
			customerPs.clearParameters();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return customerName;
	}

	private String getProductName(Connection dbConnection, String arrangeId, String arrangeTable) {
		String product = "";
		try {
			PreparedStatement arrangeStmt = dbConnection
					.prepareStatement("SELECT PRODUCT FROM " + arrangeTable + " WHERE ID=?");
			arrangeStmt.setString(1, arrangeId);
			ResultSet arrangeRs = arrangeStmt.executeQuery();
			if (arrangeRs.next()) {
				product = arrangeRs.getString("PRODUCT");
			}
			arrangeRs.close();
			arrangeStmt.clearParameters();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return product;
	}
	private void ResponseMessages(ExportLCBillCollectionList exportList,String unitId,String ref,String custId,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		exportList.setUnitId(unitId);
		exportList.setReferenceNumber(ref);
		exportList.setCustId(custId);
		exportList.setErrCode(ErrorCode);
		exportList.setErrorDesc(ErrorDescription);
	}
}
