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

@Path("/CustomerOnboarding")
public class CustomerOnboarding {

	private static DataSource cmDBPool;
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static Map<String, Integer> AccountTypes = new HashMap<String, Integer>();
	private static HashMap<String, String> ActualTableName;

	private static String AcctTableRef = "ACCOUNT";
	private static String CategoryTableRef = "CATEGORY";
	private static String AAArrangementTableRef = "AA_ARRANGEMENT";
	private static String DeptAccOffTableRef = "DEPT$ACC$OFF";
	private static String AAAccountTableRef = "AA$ACCOUNT$DETAILS";
	private static String CustomerTableRef = "CUSTOMER";
	private static String SegmentTableRef = "CBA$CUSTOMER$SEGMENT";
	private static String PostingRestrictTableRef = "POSTING_RESTRICT";
	private static String AABillDetailsRef = "AA$BILL$DETAILS";

	enum Accountstatus {
		N;
	}

	static {
		AccountTypes.put("ACCOUNTS", 1);
		AccountTypes.put("DEPOSITS", 2);
		AccountTypes.put("LENDING", 3);
	}

	public static void setDBPool(DataSource cmDBPool) {
		CustomerOnboarding.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		CustomerOnboarding.ActualTableName = ActualTableName;
	}

	@POST
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = CustomerOnboardingList.class, responseDescription = "CustomerOnboarding Response", responseCode = "200")
	@Operation(summary = "Customer Onboarding Request", description = "returns CustomerOnboarding data")
	public Response getCustomerOnboarding(
			@RequestBody(description = "Customer Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerOnboardingRequest.class))) CustomerOnboardingRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Customer onboarding Interface Started on ["+startTime+"]");
			System.out.println("Fetching CustomerOnboarding Data For [ " + id.cif + " ]");

			CustomerOnboardingList CusOnboardList = new CustomerOnboardingList();
			//CustomerOnboardingInformation customer = null;
			AccountOnboardingInformation Account = null;
			String BranchDetails[] = null;

			System.out.println("Customer Onboarding Table Ref [" + AcctTableRef + "," + AAArrangementTableRef + ","
					+ CategoryTableRef + "," + AAAccountTableRef + "," + DeptAccOffTableRef + "," + SegmentTableRef
					+ "," + CustomerTableRef + ","+PostingRestrictTableRef+","+AABillDetailsRef+"]");

			String unitId = id.unId;
			String CustomerId=id.cif;
			String AcctTableName = ActualTableName.get(unitId + "-" + AcctTableRef);
			String CategoryTableName = ActualTableName.get(unitId + "-" + CategoryTableRef);
			String AAArrangementTableName = ActualTableName.get(unitId + "-" + AAArrangementTableRef);
			String AAAccountTableName = ActualTableName.get(unitId + "-" + AAAccountTableRef);
			String DeptAccOffTableName = ActualTableName.get(unitId + "-" + DeptAccOffTableRef);
			String CustomerTableName = ActualTableName.get(unitId + "-" + CustomerTableRef);
			String SegmentTableName = ActualTableName.get(unitId + "-" + SegmentTableRef);
			String PostingRestrictTableName = ActualTableName.get(unitId + "-" + PostingRestrictTableRef);
			String AABillDetailsTable = ActualTableName.get(unitId + "-" + AABillDetailsRef);

			System.out.println("Customer Onboarding Table Names [" + AcctTableName + "," + AAArrangementTableName + ","
					+ CategoryTableName + "," + AAAccountTableName + "," + DeptAccOffTableName + "," + SegmentTableName
					+ "," + CustomerTableName + ","+PostingRestrictTableName+","+PostingRestrictTableName+","+AABillDetailsTable+"]");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement AccountPS = dbConnection.prepareStatement("SELECT ID,CUSTOMER_ID,CURRENCY,"
							+ " BRANCH_CODE,ACCOUNT_TITLE,OPENING_DATE,"
							+ "	CATEGORY,POSTING_RESTRICT,DEPARTMENT_ACCOUNT_OFFICER,CASE WHEN INACTIV_MARKER is null"
							+ " THEN 'N' ELSE 'Y' END AS ACCOUNT_STATUS,ARRANGMENT_ID,CLOSED_ONLINE,CLOSURE_DATE FROM "
							+ AcctTableName + " WHERE CUSTOMER_ID = ? ");

					PreparedStatement dbSt = dbConnection.prepareStatement("SELECT COMPANY_BOOK,ID,SHORT_NAME,"
							+ " ACCOUNT_OFFICER,STREET,POST_CODE,BUILDING,COUNTRY1,COUNTRY,CUST_MOBILE_PHONE,"
							+ "CUST_EMAIL,BRANCH,case when SECTOR='1000' then 'I' else 'C' end SECTOR,"
							+ " SEGMENT,PIN_NUMBER,POST_CODE,FAX,CUST_MOBILE_PHONE FROM " + CustomerTableName + " WHERE id = ? ")) {
				dbSt.setString(1, CustomerId);
				try (ResultSet dbRs = dbSt.executeQuery()) {

					if (dbRs.next()) {

						//customer = new CustomerOnboardingInformation();
						CusOnboardList.unitID = unitId;
						CusOnboardList.cifRef = CustomerId;
						CusOnboardList.custName = dbRs.getString("SHORT_NAME")==null?"":dbRs.getString("SHORT_NAME");
						CusOnboardList.primaryId = CustomerId;
						CusOnboardList.hostMachine = "T24";
						CusOnboardList.ctPerson = dbRs.getString("SHORT_NAME")==null?"":dbRs.getString("SHORT_NAME");
						CusOnboardList.rmCode = dbRs.getString("ACCOUNT_OFFICER")==null?"":dbRs.getString("ACCOUNT_OFFICER");
						String RmDetails[] = DeptAccOfficerData(dbConnection, dbRs.getString("ACCOUNT_OFFICER"),
								DeptAccOffTableName);
						CusOnboardList.rmName = RmDetails[0];
						CusOnboardList.rmMail = RmDetails[1];
						CusOnboardList.add1 = dbRs.getString("STREET")==null?"":dbRs.getString("STREET");
						CusOnboardList.add2 = dbRs.getString("POST_CODE")==null?"":dbRs.getString("POST_CODE");
						CusOnboardList.add3 = dbRs.getString("BUILDING")==null?"":dbRs.getString("BUILDING");
						CusOnboardList.add4 = "";
						CusOnboardList.cityTown = dbRs.getString("COUNTRY1")==null?"":dbRs.getString("COUNTRY1");
						CusOnboardList.countryCode = dbRs.getString("COUNTRY")==null?"":dbRs.getString("COUNTRY");
						CusOnboardList.zip = dbRs.getString("POST_CODE")==null?"":dbRs.getString("POST_CODE");
						CusOnboardList.faxNo = dbRs.getString("FAX")==null?"":dbRs.getString("FAX");
						CusOnboardList.phNumber = dbRs.getString("CUST_MOBILE_PHONE")==null?"":dbRs.getString("CUST_MOBILE_PHONE");
						CusOnboardList.cellNo = dbRs.getString("CUST_MOBILE_PHONE")==null?"":dbRs.getString("CUST_MOBILE_PHONE");
						CusOnboardList.emailID = dbRs.getString("CUST_EMAIL")==null?"":dbRs.getString("CUST_EMAIL");
						BranchDetails = DeptAccOfficerData(dbConnection, dbRs.getString("BRANCH"), DeptAccOffTableName);
						CusOnboardList.branchCode = BranchDetails[0];
						CusOnboardList.custCategory = dbRs.getString("SECTOR")==null?"":dbRs.getString("SECTOR");
						CusOnboardList.stat = "A";
						CusOnboardList.locallangCustname = "";
						CusOnboardList.sicCode = Segment(dbConnection, dbRs.getString("SEGMENT"), SegmentTableName);
						CusOnboardList.tin = dbRs.getString("PIN_NUMBER")==null?"":dbRs.getString("PIN_NUMBER");

						int AccountType=0;
						AccountPS.setString(1, CustomerId);
						ResultSet AccountRS = AccountPS.executeQuery();
						while (AccountRS.next()) {
							// System.out.println("account call ="+AccountRS.getString("ID"));
							Account = new AccountOnboardingInformation();
							Account.unitID = unitId;
							Account.accNumber = AccountRS.getString("ID")==null?"":AccountRS.getString("ID");
							Account.accCur = AccountRS.getString("CURRENCY")==null?"":AccountRS.getString("CURRENCY");
							BranchDetails = DeptAccOfficerData(dbConnection, AccountRS.getString("BRANCH_CODE"),
									DeptAccOffTableName);
							Account.branch = BranchDetails[0];
							Account.accName = AccountRS.getString("ACCOUNT_TITLE")==null?"":AccountRS.getString("ACCOUNT_TITLE");
							Account.openDt = DateFormtter
									.format(TimeFormat.parse("" + AccountRS.getString("OPENING_DATE")));
							Account.cifRef = AccountRS.getString("CUSTOMER_ID")==null?"":AccountRS.getString("CUSTOMER_ID");
							AccountType=AccountType(dbConnection, AccountRS.getString("ARRANGMENT_ID"),
									AAArrangementTableName);
							Account.accType = AccountType;
							Account.proCode = AccountRS.getString("CATEGORY")==null?"":AccountRS.getString("CATEGORY");
							Account.accCategory = DescriptionPick(dbConnection, AccountRS.getString("CATEGORY"),
									CategoryTableName);
							Account.closureFlag = AccountRS.getString("CLOSED_ONLINE")==null?"N":AccountRS.getString("CLOSED_ONLINE");

							Account.closureDate = AccountRS.getString("CLOSURE_DATE")==null?"":AccountRS.getString("CLOSURE_DATE");
							Account.nocredStatus = PostingRestrict(dbConnection, AccountRS.getString("POSTING_RESTRICT"), 
									PostingRestrictTableName,"CREDIT");
							Account.nodebtStatus = PostingRestrict(dbConnection, AccountRS.getString("POSTING_RESTRICT"), 
									PostingRestrictTableName,"DEBIT");
							Account.delinquencyStat = AccountType==3?DelinquencyStatusCheck(dbConnection, AccountRS.getString("ARRANGMENT_ID"),
									AABillDetailsTable):"N";
							Account.inactiveStat = AccountRS.getString("ACCOUNT_STATUS")
									.equalsIgnoreCase(Accountstatus.N.toString()) == true
											? AccountStatusCheck(dbConnection, AccountRS.getString("ARRANGMENT_ID"),
													AAAccountTableName)
											: AccountRS.getString("ACCOUNT_STATUS");
							Account.rmCode = AccountRS.getString("DEPARTMENT_ACCOUNT_OFFICER")==null?"":AccountRS.getString("DEPARTMENT_ACCOUNT_OFFICER");
							String RmAccountDetails[] = DeptAccOfficerData(dbConnection,
									AccountRS.getString("DEPARTMENT_ACCOUNT_OFFICER"), DeptAccOffTableName);
							Account.rmName = RmAccountDetails[0];
							Account.rmMailID = RmAccountDetails[1];
							CusOnboardList.addAccount(Account);

						}
						AccountRS.close();
						AccountPS.close();
					} else {
						ResponseMessages(CusOnboardList,Account,unitId,CustomerId,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.CUSTOMER_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(CusOnboardList).build();
					}
					dbRs.close();
				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					ResponseMessages(CusOnboardList,Account,unitId,CustomerId,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(CusOnboardList).build();
				}
				dbSt.close();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				ResponseMessages(CusOnboardList,Account,unitId,CustomerId,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(CusOnboardList).build();
			}
			return Response.status(Status.ACCEPTED).entity(CusOnboardList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Customer Onboarding Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private String DescriptionPick(Connection dbConnection, String Category, String CategoryTableName)
			throws SQLException

	{
		String Description = "";

		PreparedStatement CategoryPS = dbConnection
				.prepareStatement("select DESCRIPTION from " + CategoryTableName + " where id = ? ");
		CategoryPS.setString(1, Category);
		ResultSet CategoryRS = CategoryPS.executeQuery();
		if (CategoryRS.next()) {
			Description = CategoryRS.getString(1)==null?"":CategoryRS.getString(1);;
		}
		CategoryPS.close();
		CategoryRS.close();

		return Description;
	}

	private String[] DeptAccOfficerData(Connection dbconnection, String DeptAccOfficerCode,
			String DeptAccOffTableName) {
		
		String RMDetails[] = new String[2];
		
     String RMName="";
     String RMEmail="";
     
		try {
			
			PreparedStatement RMDetailPS = dbconnection
					.prepareStatement("SELECT NAME,RM_EMAIL FROM " + DeptAccOffTableName + " where id = ? ");
			RMDetailPS.setString(1, DeptAccOfficerCode);
			ResultSet RMDetailRS = RMDetailPS.executeQuery();
			if (RMDetailRS.next()) {
				
				RMName = RMDetailRS.getString(1)==null?"":RMDetailRS.getString(1);
				RMEmail = RMDetailRS.getString(2)==null?"":RMDetailRS.getString(2);
			}
			RMDetailPS.close();
			RMDetailRS.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		RMDetails[0]=RMName;
		RMDetails[1]=RMEmail;

		return RMDetails;
	}

	private String Segment(Connection dbconnection, String SegmentID, String SegmentTableName) {

		String SegmentDesc = "";

		try {
			PreparedStatement SegmentPS = dbconnection
					.prepareStatement("SELECT DESCRIPTION FROM " + SegmentTableName + " where id = ? ");
			SegmentPS.setString(1, SegmentID);
			ResultSet SegmentRS = SegmentPS.executeQuery();
			if (SegmentRS.next()) {
				SegmentDesc = SegmentRS.getString(1)==null?"":SegmentRS.getString(1);
			}
			SegmentPS.close();
			SegmentRS.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return SegmentDesc;
	}

	private String AccountStatusCheck(Connection dbConnection, String arrId, String AAAccountTableName) {

		String AccStatus = "N";
		try {
			PreparedStatement dbSt = dbConnection
					.prepareStatement("SELECT ARR_DORMANCY_STATUS FROM " + AAAccountTableName + " WHERE ID = ?");
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
														 */) {
					AccStatus = "Y";
				}
			}
			dbRs.close();
			dbSt.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return AccStatus;
	}

	private int AccountType(Connection dbConnection, String Arrangement_ID, String AAArrangementTableName)
			throws SQLException

	{
		int AccountType = 1;

		PreparedStatement ArrangementPS = dbConnection
				.prepareStatement("select PRODUCT_LINE from " + AAArrangementTableName + "  where id = ? ");
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
	
	private String DelinquencyStatusCheck(Connection dbConnection, String Arrangement_ID, String AABillDetailsTable)
			throws SQLException
	{
		String Status="N";
		try {
			PreparedStatement ArrInterestPS = dbConnection
					.prepareStatement(" select * from "+AABillDetailsTable+" "
							+ " where arrangement_id=? and OS_TOTAL_AMOUNT>0 order by FINANCIAL_DATE desc");
			ArrInterestPS.setString(1, Arrangement_ID);
			ResultSet ArrInterestRS = ArrInterestPS.executeQuery();
			if (ArrInterestRS.next()) {
				Status="Y";
			}
			ArrInterestRS.close();
			ArrInterestPS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Delinquency Status [ " + Status + " ]");
		
		return Status;
	}
	
	private void ResponseMessages(CustomerOnboardingList CusOnboardList,AccountOnboardingInformation Account,String unitId,String CustomerId,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		Account = new AccountOnboardingInformation();
		Account.unitID = unitId;
		Account.cifRef=CustomerId;
		CusOnboardList.addAccount(Account);
		CusOnboardList.errCode = ErrorCode;
		CusOnboardList.errorDesc = ErrorDescription;
	}
	private String PostingRestrict(Connection dbconnection, String PostingRestrictId, String PostingRestrictTable,String Type) {

		String RestrictType = "N";

		try {
			PreparedStatement PostRestrictPS = dbconnection.prepareStatement(
					"SELECT RESTRICT_TYPE FROM " + PostingRestrictTable + "  where id = ? ");
			PostRestrictPS.setString(1, PostingRestrictId);
			ResultSet PostRestrictRS = PostRestrictPS.executeQuery();
			if (PostRestrictRS.next()) {
				RestrictType = (PostRestrictRS.getString(1).equalsIgnoreCase("ALL") 
						|| PostRestrictRS.getString(1).equalsIgnoreCase(Type))?"Y":"N";
			}
			PostRestrictPS.close();
			PostRestrictRS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return RestrictType;
	}
}
