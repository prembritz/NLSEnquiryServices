package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

@Path("/CreditFacilitySummary")
public class CreditFacilitySummaryDetails {
	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String CustomerLimit = "CUSTOMER$LIMIT";
	private static String LimitRefTable = "LIMIT$REFERENCE";
	private static String AccountOverdrawnRef = "ACCOUNT$OVERDRAWN";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("ddMMyyyyhhmmss");

	public static void setDBPool(DataSource cmDBPool) {
		CreditFacilitySummaryDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		CreditFacilitySummaryDetails.ActualTableName = ActualTableName;
	}

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = CreditFacilitySummaryList.class, responseDescription = "Credit Facility Summary Details Response", responseCode = "200")
	@Operation(summary = "Credit Facility Summary Details Request", description = "returns Credit Facility Summary Details data")
	public Response getCreditFacilitySummaryDetails(
			@RequestBody(description = "Credit Facility Summary Details Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditFacilitySummaryRequest.class))) CreditFacilitySummaryRequest id) {

		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Credit Facility Summary Details Started on ["+startTime+"]");
			
			CreditFacilitySummaryObject creditFacility = null;
			CreditFacilitySummaryList creditList = new CreditFacilitySummaryList();
			boolean flag;
			Connection dbConnection = cmDBPool.getConnection();

			Double percentage = 0.0;
			String unitIds = "";
			String unitidnumber = "";
			String cif = "";
			String customerLimit = "";
			String AccountOverdrawnTable="";
			String limitRef = "";
			String riskAmount = "";
			String query = "";
			ArrayList<Boolean> RespStatus = new ArrayList<Boolean>();
			//System.out.println(id.creditData.size());
			for (int i = 0; i < id.creditData.size(); i++) {
				flag = false;
				unitIds = id.creditData.get(i).unitId;
				cif = id.creditData.get(i).cif;
				
				if (!unitIds.equalsIgnoreCase(unitidnumber)) {
					unitidnumber = unitIds;
					System.out.println("CreditFacilitySummaryDetails Tables Ref [ " + unitidnumber + " ]  ["
							+ CustomerLimit + "] [" + LimitRefTable + "] ["+AccountOverdrawnRef+"]");
					customerLimit = ActualTableName.get(unitidnumber + "-" + CustomerLimit);
					AccountOverdrawnTable = ActualTableName.get(unitidnumber + "-" + AccountOverdrawnRef);
					limitRef = ActualTableName.get(unitidnumber + "-" + LimitRefTable);
					System.out.println("CreditFacilitySummaryDetails Actual Table Ref [ " + unitidnumber + " ]  ["
							+ customerLimit + "] [" + limitRef + "] ["+AccountOverdrawnTable+"]");
				 }

				System.out.println("Fetching CreditFacilitySummaryDetails Procode: [ " + id.procode + " ] UnitId: [ "
						+ unitidnumber + " ]" + " Cif: [ " + cif + " ]");

				query = "SELECT LIMIT_PRODUCT,AVAILABLE_MARKER,CURRENCY,ONLINE_LIMIT_AMOUNT,AVAIL_AMT,AMT_LAST_EXCESS,EXPIRY_DATE FROM "
						+ customerLimit + " WHERE REGEXP_SUBSTR(ID,'[^.]+',1,1)='" + cif
						+ "' AND ONLINE_LIMIT_AMOUNT>0";
				System.out.println("CustomerFacilitySummary Query:[" + query + "]");
				try (PreparedStatement dbSt = dbConnection.prepareStatement(query)) {
					try (ResultSet dbRs = dbSt.executeQuery()) {
						if (dbRs.next()) {
							flag = true;
							RespStatus.add(flag);
								
							creditFacility = new CreditFacilitySummaryObject();
							creditFacility.setUnitId(unitidnumber);
							creditFacility.setCif(cif);
							creditFacility.setLimitCategory(dbRs.getString("LIMIT_PRODUCT"));
							creditFacility.setLimitDescription(
									getLimitDescription(dbConnection, dbRs.getString("LIMIT_PRODUCT"), limitRef));
							creditFacility.setCurrency(dbRs.getString("CURRENCY"));
							creditFacility.setLimitExpiryDate(dbRs.getString("EXPIRY_DATE") == null ? ""
									: dateFormat2.format(dateFormat1.parse(dbRs.getString("EXPIRY_DATE"))));
							riskAmount = dbRs.getString("ONLINE_LIMIT_AMOUNT") == null ? "0.0"
									: dbRs.getString("ONLINE_LIMIT_AMOUNT");
							creditFacility.setRiskAmount(Double.parseDouble(riskAmount));
							creditFacility.setLimitStatus(dbRs.getString("AVAILABLE_MARKER") == null ? ""
									: dbRs.getString("AVAILABLE_MARKER"));
							creditFacility.setAvailableAmount(Double.parseDouble(
									dbRs.getString("AVAIL_AMT") == null ? "0.0" : dbRs.getString("AVAIL_AMT")));
							creditFacility.setDebitExposure(Double.parseDouble(dbRs.getString("AMT_LAST_EXCESS") == null
									|| dbRs.getString("AMT_LAST_EXCESS").equals("") ? "0.0"
											: dbRs.getString("AMT_LAST_EXCESS")));

							if (dbRs.getString("AVAIL_AMT") != null && dbRs.getString("ONLINE_LIMIT_AMOUNT") != null)
								percentage = (dbRs.getDouble("AVAIL_AMT") / dbRs.getDouble("ONLINE_LIMIT_AMOUNT"))
										* 100;

							creditFacility.setUtilizationPercentage(percentage);
							creditFacility.setGuaranteeAmt(0.0);
							creditFacility.setCollateralAmt(0.0);
							creditFacility.setExposure(getExposure(dbConnection, cif,AccountOverdrawnTable));
							creditFacility.setCreditExposure(getExposure(dbConnection,cif,AccountOverdrawnTable));
							creditList.addAccount(creditFacility);
						} else {
							RespStatus.add(flag);
							ResponseMessages(creditList,creditFacility,
									unitidnumber,cif,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.CUSTOMER_NOT_FOUND.getValue());							
						}
						dbSt.clearParameters();
						dbRs.close();
					} catch (Exception except) {
						except.printStackTrace();
						RespStatus.add(flag);
						ResponseMessages(creditList,creditFacility,
								unitidnumber,cif,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					}
					dbSt.close();
				} catch (Exception except) {
					except.printStackTrace();
					RespStatus.add(flag);
					ResponseMessages(creditList,creditFacility,
							unitidnumber,cif,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				}
			}

			if (!RespStatus.contains(true)) {
				creditList.setErrCode(ERROR_CODE.NOT_FOUND);
				creditList.setErrorDesc(ErrorResponseStatus.FAILURE.getValue());
			}

			return Response.status(Status.ACCEPTED).entity(creditList).build();
		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Credit Facility Summary Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private String getLimitDescription(Connection dbConnection, String product, String limitRefTable) {
		PreparedStatement descPs = null;
		ResultSet descRs = null;
		String description = "";
		try {
			descPs = dbConnection.prepareStatement("SELECT SHORT_NAME FROM " + limitRefTable + " WHERE ID=?");
			descPs.setString(1, product);
			descRs = descPs.executeQuery();
			if (descRs.next()) {
				description = descRs.getString("SHORT_NAME");
			}
			descRs.close();
			descPs.clearParameters();
			descPs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return description;
	}
	
	private Double getExposure(Connection dbConnection, String CustomerId, String AccountOverdrawnTable) {
		PreparedStatement descPs = null;
		ResultSet descRs = null;
		String Amount = "0.0";
		try {
			descPs = dbConnection.prepareStatement("SELECT ACT_BAL_TOT_OUT FROM " + AccountOverdrawnTable + " WHERE CUSTOMER=?");
			descPs.setString(1, CustomerId);
			descRs = descPs.executeQuery();
			if (descRs.next()) {
				Amount = descRs.getString("ACT_BAL_TOT_OUT");
			}
			descRs.close();
			descPs.clearParameters();
			descPs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("amount="+Amount);

		return Double.parseDouble(Amount);
	}
	
	private void ResponseMessages(CreditFacilitySummaryList creditList,CreditFacilitySummaryObject creditFacility,
			String unitidnumber,String cif,ERROR_CODE ErrorCode,String ErrorDescription)
	{
		creditFacility = new CreditFacilitySummaryObject();
		creditFacility.setUnitId(unitidnumber);
		creditFacility.setCif(cif);
		creditFacility.setErCode(ErrorCode);
		creditFacility.setErMsg(ErrorDescription);
		creditList.addAccount(creditFacility);					
	}
}
