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

@Path("/DealRateDetails")
public class DealRateDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	private static String ForexTable = "FOREX";

	public static void setDBPool(DataSource cmDBPool) {
		DealRateDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		DealRateDetails.ActualTableName = ActualTableName;
	}

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = DealRateObject.class, responseDescription = "Deal Rate Response", responseCode = "200")
	@Operation(summary = "Deal Rate Request", description = "returns DealRate Data")
	public Response getDealRateDetails(
			@RequestBody(description = "Deal Rate Request Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealRateRequest.class))) DealRateRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Deal Rate interface Started on ["+startTime+"]");
			String unitId = id.unitID;
			String dealNo=id.dealReferenceNo;
			String cif=id.cif;
			
			boolean flag = false;
			int i = 1;

			System.out.println("Fetching Deal Rate Request Fields for ProdCode: [ " + id.proCode + " ] Unitid: [ "
					+ unitId + " ]" + " cif: [ " + cif+ " ] DealReferenceNo: [ " + dealNo + " ]");

			System.out.println("DealRateDetails Table Ref [ " + unitId + " ] [" + ForexTable + "]");

			String forex = ActualTableName.get(unitId + "-" + ForexTable);

			System.out.println("DealRateDetails Actual Table Ref [ " + unitId + " ] [" + forex + "]");

			DealRateObject dealObj = new DealRateObject();
			String query = "";
			if (dealNo.equals("")) {
				query = "SELECT ID,SPOT_RATE,DEAL_DATE,CURRENCY_BOUGHT,CURRENCY_SOLD,AMOUNT_BOUGHT,COUNTER_PARTY "
						+ " FROM " + forex + " WHERE COUNTER_PARTY='" + id.cif
						+ "' and notes IS NOT NULL and dealer_notes IS NOT NULL and FX_BALANCE >10";
			} else {
				query = "SELECT ID,SPOT_RATE,DEAL_DATE,CURRENCY_BOUGHT,CURRENCY_SOLD,AMOUNT_BOUGHT,COUNTER_PARTY "
						+ " FROM " + forex + " WHERE ID='" + dealNo
						+ "' and notes IS NOT NULL and dealer_notes IS NOT NULL and FX_BALANCE >10";
			}
			System.out.println("DealRateDetails query [" + query + "]");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection.prepareStatement(query)) {
				try (ResultSet dbRs = dbSt.executeQuery()) {
					if (dbRs.next()) {
						flag = true;
					//	System.out.println("**********************");
						dealObj.setUnitID(unitId);
						dealObj.setCif(id.cif);
						dealObj.setDealReferenceNo(dbRs.getString("ID"));
						dealObj.setDealRate(dbRs.getString("SPOT_RATE"));
						dealObj.setDealDate(DateFormtter.format(TimeFormat2.parse("" + dbRs.getString("DEAL_DATE"))));
						dealObj.setDealCurrency(dbRs.getString("CURRENCY_BOUGHT"));
						dealObj.setDealCurrencyTo(dbRs.getString("CURRENCY_SOLD"));
						dealObj.setDealBookingAmount(Double.parseDouble(
								dbRs.getString("AMOUNT_BOUGHT") == null ? "0.0" : dbRs.getString("AMOUNT_BOUGHT")));

					}

					if (!flag) {
						ResponseMessages(dealObj,unitId,cif,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(dealObj).build();
					}

				} catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(dealObj,unitId,cif,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(dealObj).build();
				}

			} catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(dealObj,unitId,cif,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(dealObj).build();
			}

			return Response.status(Status.ACCEPTED).entity(dealObj).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Deal Rate Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private void ResponseMessages(DealRateObject dealObj,String unitId,String cif,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		dealObj.setUnitID(unitId);
		dealObj.setCif(cif);
		dealObj.setErrCode(ErrorCode);
		dealObj.setErrorDesc(ErrorDescription);
	}
}
