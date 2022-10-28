package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

@Path("/CreditFacilitySummaryInterface")
public class CreditFacilitySummaryInterfaceDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String customerLimit = "CUSTOMER$LIMIT";
	private static String limitRefTable = "LIMIT$REFERENCE";
	private static String customerTable = "CUSTOMER";
	private static String AccTable = "ACCOUNT";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static void setDBPool(DataSource cmDBPool) {
		CreditFacilitySummaryInterfaceDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		CreditFacilitySummaryInterfaceDetails.ActualTableName = ActualTableName;
	}

	NumberFormat amountFormat = new DecimalFormat("#,##0.00");

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = CreditFacilitySummaryInterfaceList.class, responseDescription = "Credit Facility Summary Interface Details Response", responseCode = "200")
	@Operation(summary = "Credit Facility Summary Interface Details Request", description = "returns Credit Facility Summary Interface Details data")
	public Response getCreditFacilitySummaryInterfaceDetails(
			@RequestBody(description = "Credit Facility Summary Interface Details Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditFacilitySummaryInterfaceRequest.class))) CreditFacilitySummaryInterfaceRequest id) {

		String unitid = id.unitID;
		String cif = id.cif;
		String ref = id.referenceNum;
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Credit Facility Summary Interface Details Started on ["+startTime+"]");
			
			System.out.println("Fetching Credit Facility Summary Interface Details Request Fields For UnitId: [ "
					+ id.unitID + " ]" + " idType: [ " + id.idType + " ] CIF: [ " + cif + " ] referenceNum: [ " + ref
					+ " ] ");

			boolean flag = false;
			int i = 0;
			CreditFacilitySummaryInterfaceObject creditFacility = null;
			CreditFacilitySummaryInterfaceList creditList = new CreditFacilitySummaryInterfaceList();

			System.out.println("Credit Facility Summary Details Interface Table Ref's [ " + unitid + " ] ["
					+ customerLimit + "] [" + limitRefTable + "] [" + customerTable + "] [" + AccTable + "]");

			String customerLimitTable = ActualTableName.get(unitid + "-" + customerLimit);
			String limitTable = ActualTableName.get(unitid + "-" + limitRefTable);
			String custTable = ActualTableName.get(unitid + "-" + customerTable);
			String accountTable = ActualTableName.get(unitid + "-" + AccTable);

			System.out.println("Credit Facility Summary Details Interface Actual Table Names [ " + unitid + " ] ["
					+ customerLimitTable + "] [" + limitRefTable + "] [" + custTable + "]  [" + accountTable + "]");

			System.out.println("SELECT * FROM " + accountTable + " WHERE CUSTOMER_ID='" + cif + "'");
			System.out.println("CustomerFacilitySummary Interface Query:["
					+ "SELECT LIMIT_PRODUCT,CURRENCY,MAXIMUM_TOTAL,ONLINE_LIMIT_AMOUNT,APPROVAL_DATE,AVAIL_AMT,AMT_LAST_EXCESS,DATE_TIME,LIMIT_PRODUCT FROM \"\r\n"
					+ customerLimitTable + " WHERE REGEXP_SUBSTR(ID,'[^.]+',1,1)='" + cif
					+ "' ] AND ONLINE_LIMIT_AMOUNT>0");
			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection.prepareStatement(
							"SELECT LIMIT_PRODUCT,CURRENCY,MAXIMUM_TOTAL,ONLINE_LIMIT_AMOUNT,APPROVAL_DATE,AVAIL_AMT,AMT_LAST_EXCESS,DATE_TIME,LIMIT_PRODUCT FROM "
									+ customerLimitTable
									+ " WHERE REGEXP_SUBSTR(ID,'[^.]+',1,1)=? AND ONLINE_LIMIT_AMOUNT>0")) {
				dbSt.setString(1, cif);

				try (ResultSet dbRs = dbSt.executeQuery()) {
					while (dbRs.next()) {
						flag = true;
						if (++i == 1) {
							creditList.setUnitId(unitid);
							creditList.setCif(cif);
							creditList.setRef(ref);
						}

						creditFacility = new CreditFacilitySummaryInterfaceObject();
						creditFacility.setLimitID(dbRs.getString("LIMIT_PRODUCT"));
						creditFacility.setLimitDescription(
								getLimitDescription(dbConnection, dbRs.getString("LIMIT_PRODUCT"), limitTable));
						creditFacility.setCurrency(dbRs.getString("CURRENCY"));
						creditFacility.setCustomerName(getCustomerName(dbConnection, cif, custTable));

						creditFacility.setLimitAmount(getLimitAmount(dbConnection, cif,accountTable, customerLimitTable));
						creditFacility.setTotalLiability(dbRs.getDouble("MAXIMUM_TOTAL"));

						if (dbRs.getString("AMT_LAST_EXCESS") == null || dbRs.getString("AMT_LAST_EXCESS").equals("")) {
							creditFacility.setLimitMarginAmount(0.0);
						} else {
							creditFacility.setLimitMarginAmount(Double.parseDouble(dbRs.getString("AMT_LAST_EXCESS")));
						}

						creditFacility.setAvailableSanctionLimit(dbRs.getDouble("AVAIL_AMT"));

						double TotalAvailableDrawAmount = (dbRs.getDouble("ONLINE_LIMIT_AMOUNT")
								/ dbRs.getDouble("AVAIL_AMT")) * 100;

						if (Double.isNaN(TotalAvailableDrawAmount)) {
							TotalAvailableDrawAmount = 0.0;
						}
						creditFacility.setAvailableDrawingLimit(TotalAvailableDrawAmount);

						creditFacility.setLimitApprovalDate(dbRs.getString("APPROVAL_DATE") == null ? ""
								: DateFormtter.format(DateFormtter.parse("" + dbRs.getString("APPROVAL_DATE"))));
						creditFacility.setLimitReviewDate(dbRs.getString("DATE_TIME") == null ? ""
								: DateFormtter.format(DateFormtter.parse("" + dbRs.getString("DATE_TIME"))));
						creditFacility.setLimitType(dbRs.getString("LIMIT_PRODUCT"));

						creditList.addAccount(creditFacility);
					}
					dbSt.clearParameters();
					dbRs.close();

					if (!flag) {
						ResponseMessages(creditList,
								unitid,cif,ref,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.SUMMARY_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(creditList).build();	
					}

				} catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(creditList,
							unitid,cif,ref,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(creditList).build();
				}
				dbSt.close();
			} catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(creditList,
						unitid,cif,ref,ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(creditList).build();
			}

			return Response.status(Status.ACCEPTED).entity(creditList).build();
		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Credit Facility Summary Interface Details Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
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

	private String getCustomerName(Connection dbConnection, String cif, String custTable) {
		PreparedStatement customerPs = null;
		ResultSet customerRs = null;
		String name = "";
		try {
			customerPs = dbConnection.prepareStatement("SELECT SHORT_NAME FROM " + custTable + " WHERE ID=?");
			customerPs.setString(1, cif);
			customerRs = customerPs.executeQuery();
			if (customerRs.next()) {
				name = customerRs.getString("SHORT_NAME");
			}
			customerRs.close();
			customerPs.clearParameters();
			customerPs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return name;
	}

	public double getLimitAmount(java.sql.Connection connection, String customerID,String accTable, String CustomerLimitTable)
			throws Exception {
		DecimalFormat limitFormat = new DecimalFormat("0000000.00");
		double online_limit = 0.00;
		String limitReference = "", customerId = "";
		PreparedStatement accPs = connection.prepareStatement(
				"SELECT LIMIT_REFERENCE,CUSTOMER_ID FROM BNK_ACCOUNT WHERE "
				+ " CUSTOMER_ID=? AND LIMIT_REFERENCE IS NOT NULL and rownum<=1 order by id desc");
		accPs.setString(1, customerID);
		ResultSet accRs = accPs.executeQuery();
		if (accRs.next()) {
			limitReference = accRs.getString("LIMIT_REFERENCE");
			customerId = accRs.getString("CUSTOMER_ID");
		}
		accRs.close();
		accPs.close();

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
						online_limit = limit.getDouble("ONLINE_LIMIT_AMOUNT");
					}
				}

			}

			limit.close();
			limitStatement.close();

			System.out.println("OD Amount [" + online_limit + "]");

		}
		return online_limit;
	}

	private void ResponseMessages(CreditFacilitySummaryInterfaceList creditList,
			String unitid,String cif,String ref,ERROR_CODE ErrorCode,String ErrorDescription)
	{
		creditList.setUnitId(unitid);
		creditList.setCif(cif);
		creditList.setRef(ref);
		creditList.setErrCode(ErrorCode);
		creditList.setErrorDesc(ErrorDescription);		
	}
}
