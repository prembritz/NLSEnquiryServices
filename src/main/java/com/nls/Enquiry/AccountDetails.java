package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

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

@Path("/AccountDetails")
public class AccountDetails {

	private static Map<String, Integer> AccountTypes = new HashMap<String, Integer>();
	private static DataSource cmDBPool;
	private static NumberFormat numberFormat = new DecimalFormat("###0.00");
	private static HashMap<String, String> ActualTableName;
	private static HashMap<String, String> GlobalParameters;

	private static String AcctTableRef = "ACCOUNT";
	private static String CategoryTableRef = "CATEGORY";
	private static String AAArrangementTableRef = "AA_ARRANGEMENT";
	private static String DeptAccOffTableRef = "DEPT$ACC$OFF";
	private static String AAAccountTableRef = "AA$ACCOUNT$DETAILS";
	private static String CustomerLimitTableRef = "CUSTOMER$LIMIT";
	private static String LockedEventsTableRef = "AC$LOCKED$EVENTS";
	private static String PostingRestrictTableRef = "POSTING_RESTRICT";

	enum Accountstatus {
	     N;
	}

	static {
		AccountTypes.put("ACCOUNTS", 1);
		AccountTypes.put("DEPOSITS", 2);
		AccountTypes.put("LENDING", 3);
	}

	public static void setDBPool(DataSource cmDBPool) {
		AccountDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		AccountDetails.ActualTableName = ActualTableName;
	}

	public static void setInitiailizeGlobalParameters(HashMap<String, String> GlobalParameters) {
		AccountDetails.GlobalParameters = GlobalParameters;
	}
    
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = AccountDetailsObject.class, responseDescription = "Account Details Response", responseCode = "200")
	@Operation(summary = "Account Details Request", description = "returns Account Details data")
	public Response getAccountDetails(
			@RequestBody(description = "Account Number", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDetailsRequest.class))) AccountDetailsRequest id) {
		
		AccountDetailsObject account = new AccountDetailsObject();
		String unitId = id.unId;
		String AccountNumber=id.accountNo;
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Account Details Interface Started on ["+startTime+"]");
			System.out.println("Fetching Account Details For [ " + id.accountNo + " ]");
			
			System.out.println("Account Details Table Ref [" + AcctTableRef + "," + AAArrangementTableRef + ","
					+ CategoryTableRef + "," + AAAccountTableRef + "," + DeptAccOffTableRef + ","
					+ CustomerLimitTableRef + "," + LockedEventsTableRef + "],["+PostingRestrictTableRef+"]");

			String AcctTableName = ActualTableName.get(unitId + "-" + AcctTableRef);
			String CategoryTableName = ActualTableName.get(unitId + "-" + CategoryTableRef);
			String AAArrangementTableName = ActualTableName.get(unitId + "-" + AAArrangementTableRef);
			String AAAccountTableName = ActualTableName.get(unitId + "-" + AAAccountTableRef);
			String DeptAccOffTableName = ActualTableName.get(unitId + "-" + DeptAccOffTableRef);
			String CustomerLimitTableName = ActualTableName.get(unitId + "-" + CustomerLimitTableRef);
			String LockedEventsTableName = ActualTableName.get(unitId + "-" + LockedEventsTableRef);
			String PostingRestrictTableName = ActualTableName.get(unitId + "-" + PostingRestrictTableRef);
			
			System.out.println("Account Details Table Names [" + AcctTableName + "," + AAArrangementTableName + ","
					+ CategoryTableName + "," + AAAccountTableName + "," + DeptAccOffTableName + ","
					+ CustomerLimitTableName + "," + LockedEventsTableName + "],["+PostingRestrictTableName+"]");

			System.out.println(
					"Account Details Enquiry =" + "select ID,ACCOUNT_TITLE,ARRANGMENT_ID,CURRENCY,CUSTOMER_ID,CATEGORY,"
							+ " BRANCH_CODE,ONLINE_WORKING_BALANCE,UNCLEAR_BALANCE,LOCKED_AMOUNT,LIMIT_REFERENCE,"
							+ "OPEN_ACTUAL_BALANCE,to_char(DATE_TIME,'DDMMYYYYHH24MISS') TIMESTAMP,"
							+ " CASE WHEN INACTIV_MARKER is null THEN 'N' ELSE 'Y' END AS ACCOUNT_STATUS,"
							+ "CASE WHEN  INACTIV_MARKER is null THEN '0' ELSE '1' END AS ACCOUNT_STATUS_DESC,POSTING_RESTRICT "
							+ " from " + AcctTableName + " where id = '" + AccountNumber + "' ");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection.prepareStatement("select ID,ACCOUNT_TITLE,CURRENCY,"
							+ "CUSTOMER_ID,CATEGORY,ARRANGMENT_ID,"
							+ " BRANCH_CODE,ONLINE_WORKING_BALANCE,UNCLEAR_BALANCE,LOCKED_AMOUNT,LIMIT_REFERENCE,"
							+ "OPEN_ACTUAL_BALANCE,to_char(DATE_TIME,'DDMMYYYYHH24MISS') TIMESTAMP,"
							+ " CASE WHEN INACTIV_MARKER is null THEN 'N' ELSE 'Y' END AS ACCOUNT_STATUS,"
							+ "CASE WHEN INACTIV_MARKER is null THEN '0' ELSE '1' END AS ACCOUNT_STATUS_DESC,"
							+ " concat('" + GlobalParameters.get("NCBA_SWIFT_CODE") + "',BRANCH_CODE) AS BIC,POSTING_RESTRICT "
							+ " from " + AcctTableName + " where id = ? ")) {

				dbSt.setString(1, AccountNumber);
				try (ResultSet dbRs = dbSt.executeQuery()) {
					if (dbRs.next()) {
						account.setuId(unitId);
						account.setAccNo(dbRs.getString("ID"));
						account.setAccName(dbRs.getString("ACCOUNT_TITLE"));
						account.setCur(dbRs.getString("CURRENCY"));
						account.setCustId(dbRs.getString("CUSTOMER_ID"));
						account.setAccType(
								AccountType(dbConnection, dbRs.getString("ARRANGMENT_ID"), AAArrangementTableName));
						account.setAcctypeDesc(
								DescriptionPick(dbConnection, dbRs.getString("CATEGORY"), CategoryTableName));
						String BranchDetails[] = DeptAccOfficerData(dbConnection, dbRs.getString("BRANCH_CODE"),
								DeptAccOffTableName);
						account.setbCode(BranchDetails[0]);
						
						Double LockedAmt = LockedAmount(dbConnection, dbRs.getString("ID"), LockedEventsTableName);
						Double ODAmount=getLimitAmount(dbConnection, dbRs,CustomerLimitTableName);
						Double AvailableBalance=ODAmount+dbRs.getDouble("ONLINE_WORKING_BALANCE") - LockedAmt;
						Double TotalBalance=AvailableBalance+LockedAmt+dbRs.getDouble("UNCLEAR_BALANCE");
						
						account.setAvailBal((AvailableBalance));
						account.setTotalBalance(TotalBalance);
						account.setUnclearedBalance(dbRs.getDouble("UNCLEAR_BALANCE")>0?dbRs.getDouble("UNCLEAR_BALANCE"):0.0);
						account.setFrozenAmt(LockedAmt);

						account.setOdLmt(getLimitAmount(dbConnection, dbRs, CustomerLimitTableName));
						account.setBicId(dbRs.getString("BIC"));
						account.setPrevdayBal(dbRs.getDouble("OPEN_ACTUAL_BALANCE")>0?dbRs.getDouble("OPEN_ACTUAL_BALANCE"):0.0);
						account.setCurBal(dbRs.getDouble("ONLINE_WORKING_BALANCE")>0?dbRs.getDouble("ONLINE_WORKING_BALANCE"):0.0);
						account.setOpenBal(dbRs.getDouble("OPEN_ACTUAL_BALANCE")>0?dbRs.getDouble("OPEN_ACTUAL_BALANCE"):0.0);
						account.setAccStatus(dbRs.getString("ACCOUNT_STATUS")
								.equalsIgnoreCase(Accountstatus.N.toString()) == true
										? AccountStatusCheck(dbConnection, dbRs.getString("ARRANGMENT_ID"), 1,
												AAAccountTableName)
										: dbRs.getString("ACCOUNT_STATUS"));
						account.setPostingRestrict(PostingRestrict(dbConnection, dbRs.getString("POSTING_RESTRICT"), PostingRestrictTableName));
						account.setAccstatusDesc(
								dbRs.getInt("ACCOUNT_STATUS_DESC") == 0
										? AccountStatusCheck(dbConnection, dbRs.getString("ARRANGMENT_ID"), 2,
												AAAccountTableName)
										: "Inactive");
						account.setTimeStamp(dbRs.getString("TIMESTAMP"));
					} else {
						ResponseMessages(account,unitId,AccountNumber,ERROR_CODE.NOT_FOUND,
								ErrorResponseStatus.ACCOUNT_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(account).build();
					}
					dbRs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
					ResponseMessages(account,unitId,AccountNumber,ERROR_CODE.NOT_FOUND,
							ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(account).build();
				}
				dbSt.close();

			} catch (Exception e) {
				e.printStackTrace();
				ResponseMessages(account,unitId,AccountNumber,ERROR_CODE.NOT_FOUND,
						ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(account).build();
			}
			return Response.status(Status.ACCEPTED).entity(account).build();

		} catch (Exception except) {
			except.printStackTrace();		
			ResponseMessages(account,unitId,AccountNumber,ERROR_CODE.NOT_FOUND,
					ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		finally {
			LocalDateTime endTime = LocalDateTime.now(); 
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Account Details Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}
	
	private String PostingRestrict(Connection dbconnection, String PostingRestrictId, String PostingRestrictTable) {

		String RestrictType = "";

		try {
			PreparedStatement PostRestrictPS = dbconnection.prepareStatement(
					"SELECT RESTRICT_TYPE FROM " + PostingRestrictTable + "  where id = ? ");
			PostRestrictPS.setString(1, PostingRestrictId);
			ResultSet PostRestrictRS = PostRestrictPS.executeQuery();
			if (PostRestrictRS.next()) {
				RestrictType = PostRestrictRS.getString(1)==null?"":PostRestrictRS.getString(1);
			}
			PostRestrictPS.close();
			PostRestrictRS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return RestrictType;
	}

	private String DescriptionPick(Connection dbConnection, String Category, String CategoryTable) throws SQLException

	{
		String Description = "";

		PreparedStatement CategoryPS = dbConnection
				.prepareStatement("select DESCRIPTION from " + CategoryTable + " where id = ? ");
		CategoryPS.setString(1, Category);
		ResultSet CategoryRS = CategoryPS.executeQuery();
		if (CategoryRS.next()) {
			Description = CategoryRS.getString(1)==null?"":CategoryRS.getString(1);
		}
		CategoryPS.close();
		CategoryRS.close();

		return Description;
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

	private String[] DeptAccOfficerData(Connection dbconnection, String DeptAccOfficerCode,
			String AccountOfficerTable) {
		String RMDetails[] = new String[2];
        boolean exist=false;
		try {
			PreparedStatement RMDetailPS = dbconnection
					.prepareStatement("SELECT NAME,RM_EMAIL FROM " + AccountOfficerTable + " where id = ? ");
			RMDetailPS.setString(1, DeptAccOfficerCode);
			ResultSet RMDetailRS = RMDetailPS.executeQuery();
			if (RMDetailRS.next()) {
				exist=true;
				RMDetails[0] = RMDetailRS.getString(1)==null?"":RMDetailRS.getString(1);
				RMDetails[1] = RMDetailRS.getString(2)==null?"":RMDetailRS.getString(2);
			}
			RMDetailPS.close();
			RMDetailRS.close();
			
			if(!exist) {
				RMDetails[0]="";
				RMDetails[1]="";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return RMDetails;
	}

	private double LockedAmount(Connection dbconnection, String AccNumber, String LockedEventTable) {

		double FrozenAmt = 0.00;

		try {
			PreparedStatement LockedAmtPS = dbconnection.prepareStatement(
					"SELECT sum(amount) frozen_amt FROM " + LockedEventTable + "  where account = ? group by account");
			LockedAmtPS.setString(1, AccNumber);
			ResultSet LockedAmtRS = LockedAmtPS.executeQuery();
			if (LockedAmtRS.next()) {
				FrozenAmt = LockedAmtRS.getDouble(1)>0?LockedAmtRS.getDouble(1):0.0;
			}
			LockedAmtPS.close();
			LockedAmtRS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return FrozenAmt;
	}

	private String AccountStatusCheck(Connection dbConnection, String arrId, Integer status,
			String AAAccountDetailsTable) {

		String AccStatus = "Active";

		if (status == 1)
			AccStatus = "N";

		try {
			PreparedStatement dbSt = dbConnection
					.prepareStatement("SELECT ARR_DORMANCY_STATUS FROM " + AAAccountDetailsTable + " WHERE ID = ?");
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
					if (status == 1)
						AccStatus = "Y";
					else
						AccStatus = "Inactive";
				}
			}
			dbRs.close();
			dbSt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return AccStatus;
	}

	public double getLimitAmount(java.sql.Connection connection, ResultSet accRec, String CustomerLimitTable)
			throws Exception {
		DecimalFormat limitFormat = new DecimalFormat("0000000.00");
		double online_limit = 0.00;
		// accRec.next();

		String limitReference = accRec.getString("LIMIT_REFERENCE");
		String customerId = accRec.getString("CUSTOMER_ID");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		int valDate = Integer.parseInt(dateFormat.format(new java.util.Date()));
		// System.out.println(limitReference+" Ref:CustId "+customerId);
		if (limitReference != null && !limitReference.trim().equals("")) {
			if (!limitReference.equalsIgnoreCase("NOSTRO")) {
				limitReference = customerId + "." + limitFormat.format(Double.parseDouble(limitReference));
			}
			System.out.println("Limit Ref [" + limitReference + "]");

			PreparedStatement limitStatement = connection.prepareStatement(
					"select CURRENCY,ONLINE_LIMIT_AMOUNT,EXPIRY_DATE from " + CustomerLimitTable + " where ID =?  ");
			limitStatement.setString(1, limitReference);

			ResultSet limit = limitStatement.executeQuery();

			if (limit.next()) {
				String toDate = limit.getString("EXPIRY_DATE");
				if (toDate != null && !toDate.trim().equals("")) {

					int to_date = Integer.parseInt(dateFormat.format(limit.getDate("EXPIRY_DATE")));
					if (valDate <= to_date) {
						online_limit = limit.getDouble("ONLINE_LIMIT_AMOUNT")==0?0.0:limit.getDouble("ONLINE_LIMIT_AMOUNT");
					}
				}

			}

			limit.close();
			limitStatement.close();

			System.out.println("OD Amount [" + online_limit + "]");

		}
		return online_limit;
	}
	
	private void ResponseMessages(AccountDetailsObject account,String unitId,String AccountNumber,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		account.setuId(unitId);
		account.setAccNo(AccountNumber);
		account.setErrCode(ErrorCode);
		account.setErrorDesc(ErrorDescription);
	}
}
