package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

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

@Path("/StandingInstructions")
public class StandingInstructionsDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String AccTableName = "ACCOUNT";
	private static String CustTableName = "CUSTOMER";
	private static String StandTableName = "STANDING$ORDER";

	private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("ddMMyyyyhhmmss");

	public static void setDBPool(DataSource cmDBPool) {
		StandingInstructionsDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		StandingInstructionsDetails.ActualTableName = ActualTableName;
	}

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = StandingInstructionsList.class, responseDescription = "Standing Instructions Response", responseCode = "200")
	@Operation(summary = "Standing Instructions Request", description = "returns Standing Instructions data")
	public Response getStandingInstructionsDetails(
			@RequestBody(description = "Standing Instructions Id", required = true, content = @Content(mediaType = "application/json", 
			schema = @Schema(implementation = StandingInstructionsRequest.class))) StandingInstructionsRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Standing Instructions Interface Started on ["+startTime+"]");
			String unitId = id.unitId;
			String accno=id.accountNumber;
			String customerId=id.customerId;
			String ref=id.referenceNum;

			System.out.println("Fetching Standing Instructions Request Fields For Reference Number: [ "
					+ ref + " ] " + "UnitId: [ " + unitId + " ]" + " Customer Id: [ " + id.customerId
					+ " ] Account Number: [ " + accno + " ] Request Time: [ " + id.requestTime + " ]");

			StandingInstructionsObject standDetails = null;
			StandingInstructionsList standingList = new StandingInstructionsList();
			int j = 0;
			boolean flag = false;

			System.out.println("StandingInstructionsDetails Table Ref's [ " + unitId + " ]  [" + AccTableName + "] ["
					+ CustTableName + "]" + "[" + StandTableName + "]");

			String accTable = ActualTableName.get(unitId + "-" + AccTableName);
			String custTable = ActualTableName.get(unitId + "-" + CustTableName);
			String standTable = ActualTableName.get(unitId + "-" + StandTableName);

			System.out.println("StandingInstructionsDetails Actual Table Names [ " + unitId + " ]  [" + accTable + "] ["
					+ custTable + "]" + "[" + standTable + "]");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement standPs = dbConnection.prepareStatement(
							"SELECT ORDERING_CUST,CURRENCY,CURRENT_AMOUNT_BAL,ID,BEN_ACCOUNT,DEST_ACCT_NAME,DEST_BNK_CODE,DATE_TIME FROM "
									+ standTable + " WHERE REGEXP_SUBSTR(ID,'[^.]+',1,1) =?")) {

				standPs.setString(1, accno);
				try (ResultSet standRS = standPs.executeQuery()) {
					while (standRS.next()) {
						flag = true;
						standDetails = new StandingInstructionsObject();
						if (++j == 1) {
							standingList.setReferenceNum(ref);
							standingList.setUnitId(unitId);
						}

						System.out.println("Customer ID [" + customerId + "]");
						
						String Details[]=getAccountDetails(dbConnection,accno,accTable);						
						standDetails.setBranchId(Details[0]);
						standDetails.setCustomerId(customerId);
						standDetails.setStndDebAccount(Details[1]);
						
						String customer[] = getCustomerData(dbConnection, customerId, custTable);
						standDetails.setCustGivenName(customer[0]);
						standDetails.setCustMidName(customer[1]);
						
						standDetails.setStndIng("");
						standDetails.setStndChargeAmt(0);
						standDetails.setStndCurrency(standRS.getString("CURRENCY")==null?"":standRS.getString("CURRENCY"));
						standDetails.setStndAmt(standRS.getDouble("CURRENT_AMOUNT_BAL"));
						standDetails.setStndCurrency(standRS.getString("CURRENCY")==null?"":standRS.getString("CURRENCY"));
						standDetails.setStndTranBy("");
						standDetails.setStndRefNo(standRS.getString("ID")==null?"":standRS.getString("ID"));
						standDetails.setTranByDesc("");
						standDetails.setStndBenName("");
						standDetails.setStndBenAddr1("");
						standDetails.setStndBenAddr2("");
						standDetails.setStndBenAcct(standRS.getString("BEN_ACCOUNT")==null?"":standRS.getString("BEN_ACCOUNT"));
						standDetails.setStndBenBankName1(standRS.getString("DEST_ACCT_NAME")==null?"":standRS.getString("DEST_ACCT_NAME"));
						standDetails.setStndBenBankName2(standRS.getString("DEST_BNK_CODE")==null?"":standRS.getString("DEST_BNK_CODE"));
						standDetails.setStndPayDetail1("");
						standDetails.setStndPayDetail2("");
						standDetails.setStndPeriod("");
						standDetails.setStndFistPay("");
						standDetails.setStndEffectDate("");
						standDetails.setStndLastPay("");
						standDetails.setStndOrderDate(standRS.getString("DATE_TIME")==null?"":
								dateFormat2.format(dateFormat1.parse(standRS.getString("DATE_TIME"))));
						standDetails.setStndOPId("");
						standDetails.setStndAppDate("");
						standDetails.setStndAppId("");
						standDetails.setStndFirstPayNew("");
						standDetails.setStndLastPayNew("");
						standDetails.setStndAmtNew("");
						standDetails.setStndCurrencyNew("");
						standDetails.setStndComment1("");
						standDetails.setStndComment2("");
						standDetails.setStndComment1("");
						standDetails.setBnkCode("");
						standDetails.setCityId("");
						standDetails.setTtCountryId("");
						standDetails.setStndNextPay("");
						standDetails.setStndNextPayNew("");
						standDetails.setStndProcStatus("");
						standDetails.setStndClose("");
						standDetails.setStndCloseId("");
						standDetails.setStndCCloseId("");
						standDetails.setStndCloseDate("");
						standDetails.setStndCCloseDate("");
						standDetails.setStndOrderType("");
						standDetails.setStndCharity("");
						standDetails.setStndCharityDesc("");
						standDetails.setTranDesc("");
						standDetails.setBicCode("");
						standDetails.setBnkName("");
						standDetails.setChrgCode("");
						standDetails.setTranTypeCode("");
						standDetails.setTlrSwift701("");
						standDetails.setStndFailCounter("");
						standDetails.setStndLastSuccessDate("");
						standDetails.setTlrSwift701("");
						standDetails.setTlrSwift701("");
						standDetails.setStndCode("");

						standingList.addAccount(standDetails);

					}
					standRS.close();
				}
				catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(standingList,unitId,ref,accno,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(standingList).build();
				}			
				standPs.close();
			} catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(standingList,unitId,ref,accno,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(standingList).build();
			}
			
			if (!flag) {
				ResponseMessages(standingList,unitId,ref,accno,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(standingList).build();
			}
			
			return Response.status(Status.ACCEPTED).entity(standingList).build();
		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Standing Instructions Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private String[] getCustomerData(Connection dbConnection, String customerId, String customerTab) {
		String data[] = new String[2];
		try {
			PreparedStatement customerPs = dbConnection
					.prepareStatement("SELECT GIVEN_NAME,NAME FROM " + customerTab + " WHERE ID=?");
			customerPs.setString(1, customerId);
			ResultSet customerRS = customerPs.executeQuery();
			if (customerRS.next()) {
				data[0] = customerRS.getString("GIVEN_NAME");
				data[1] = customerRS.getString("NAME");
			}
			customerRS.close();
			customerPs.clearParameters();

			System.out.println("CUSTOMER GIVEN NAME [" + data[0] + "] NAME: [" + data[1] + "]");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	
	private String[] getAccountDetails(Connection dbConnection ,String accno,String accTable) {
		String Details[]=new String[2];
		try {
			System.out.println("SELECT ID,BRANCH_CODE,CURRENCY,COMPANY_ID,CUSTOMER_ID FROM " + accTable + " "
							+ " WHERE ID='"+accno+"' ");
			PreparedStatement dbSt = dbConnection
					.prepareStatement("SELECT ID,BRANCH_CODE,CURRENCY,COMPANY_ID,CUSTOMER_ID FROM " + accTable + " "
							+ " WHERE ID=? ");
			ResultSet dbRs = null;
			dbSt.setString(1, accno);
			dbRs = dbSt.executeQuery();
			if (dbRs.next()) {
				Details[0]=dbRs.getString("BRANCH_CODE");
				Details[1]=dbRs.getString("ID");
			}else {
				Details[0]="";
				Details[1]="";
			}
			dbRs.close();
			dbSt.clearParameters();
			dbSt.close();

		}catch(Exception e) {
			e.printStackTrace();
		}
		return Details;
	}
	private void ResponseMessages(StandingInstructionsList standingList,String unitId,String ref,String accno,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		standingList.setUnitId(unitId);
		standingList.setReferenceNum(ref);
		standingList.setAccNo(accno);
		standingList.setErrCode(ErrorCode);
		standingList.setErrorDesc(ErrorDescription);
	}
}
