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

@Path("/FXFutures")
public class FXFutureDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	private static String ForexTable = "FOREX";

	public static void setDBPool(DataSource cmDBPool) {
		FXFutureDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		FXFutureDetails.ActualTableName = ActualTableName;
	}

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = FXFutureList.class, responseDescription = "FX Future Response", responseCode = "200")
	@Operation(summary = "FX Future Request", description = "returns FX Future data")
	public Response getFXFutureDetails(
			@RequestBody(description = "FX Future Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = FXFutureRequest.class))) FXFutureRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("FX Future Interface Started on ["+startTime+"]");
			String unitId = id.unitId;
			String ReferenceNumber = id.referencenum;
			boolean flag = false;
			int i = 1;

			System.out.println("Fetching FX Future Request Fields for Referencenum: [ " + id.referencenum
					+ " ] Unitid: [ " + unitId + " ]" + " Customer Id: [ " + id.customerId + " ] Request Time: [ "
					+ id.requestTime + " ]");

			System.out.println("FXFutureDetails Table Ref [ " + unitId + " ] [" + ForexTable + "]");

			String forex = ActualTableName.get(unitId + "-" + ForexTable);

			System.out.println("FXFutureDetails Actual Table Ref [ " + unitId + " ] [" + forex + "]");

			FXFutureObject fxFuture = null;
			FXFutureList fxList = new FXFutureList();

			String query = "SELECT ID,DEAL_TYPE,DEAL_DATE,VALUE_DATE_SELL,CURRENCY_BOUGHT,AMOUNT_BOUGHT,CURRENCY_SOLD,AMOUNT_SOLD,SPOT_RATE,COUNTER_PARTY "
					+ " FROM " + forex + " WHERE COUNTER_PARTY='" + id.customerId
					+ "' and notes IS NOT NULL and dealer_notes IS NOT NULL and FX_BALANCE >10";
			System.out.println("Fx Future query [" + query + "]");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection.prepareStatement(query)) {
				try (ResultSet dbRs = dbSt.executeQuery()) {
					while (dbRs.next()) {
						flag = true;
						// System.out.println("**********************");
						fxFuture = new FXFutureObject();
						if (1 == i++) {
							fxList.setUnitId(unitId);
							fxList.setReferenceNum(id.referencenum);
						}

						fxFuture.setDealNo(dbRs.getString("ID") == null ? "" : dbRs.getString("ID"));
						fxFuture.setInstrument(dbRs.getString("DEAL_TYPE") == null ? "" : dbRs.getString("DEAL_TYPE"));
						fxFuture.setDealDate(dbRs.getString("DEAL_DATE") == null ? ""
								: DateFormtter.format(TimeFormat2.parse("" + dbRs.getString("DEAL_DATE"))));
						fxFuture.setSettlementDate(dbRs.getString("VALUE_DATE_SELL") == null ? ""
								: DateFormtter.format(TimeFormat2.parse("" + dbRs.getString("VALUE_DATE_SELL"))));
						fxFuture.setMaturityDate("");
						fxFuture.setBranch("");
						fxFuture.setBuyccy(
								dbRs.getString("CURRENCY_BOUGHT") == null ? "" : dbRs.getString("CURRENCY_BOUGHT"));
						fxFuture.setBuyAmount(dbRs.getDouble("AMOUNT_BOUGHT"));
						fxFuture.setSellccy(
								dbRs.getString("CURRENCY_SOLD") == null ? "" : dbRs.getString("CURRENCY_SOLD"));
						fxFuture.setSellAmount(dbRs.getDouble("AMOUNT_SOLD"));
						fxFuture.setContrRate("");
						fxFuture.setFwdRate(dbRs.getString("SPOT_RATE") == null ? "" : dbRs.getString("SPOT_RATE"));
						fxFuture.setCounterPartyname(
								dbRs.getString("COUNTER_PARTY") == null ? "" : dbRs.getString("COUNTER_PARTY"));

						fxList.addAccount(fxFuture);

					}

					if (!flag) {
						ResponseMessages(fxList, unitId, ReferenceNumber, ERROR_CODE.NOT_FOUND,
								ErrorResponseStatus.DATA_NOT_FOUND.getValue(), ERROR_CODE.NOT_FOUND,
								ErrorResponseStatus.ACCOUNT_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(fxList).build();
					}

				} catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(fxList, unitId, ReferenceNumber, ERROR_CODE.NOT_FOUND,
							ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue(), ERROR_CODE.NOT_FOUND,
							ErrorResponseStatus.FAILED.getValue());
					return Response.status(Status.ACCEPTED).entity(fxList).build();
				}

			} catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(fxList, unitId, ReferenceNumber, ERROR_CODE.NOT_FOUND,
						ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue(), ERROR_CODE.NOT_FOUND,
						ErrorResponseStatus.FAILED.getValue());
				return Response.status(Status.ACCEPTED).entity(fxList).build();
			}

			return Response.status(Status.ACCEPTED).entity(fxList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("FX Future Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private void ResponseMessages(FXFutureList fxList, String unitId, String ReferenceNumber, ERROR_CODE ErrorCode,
			String ErrorDescription, ERROR_CODE returnCode, String returnDesc) {
		fxList.setUnitId(unitId);
		fxList.setReferenceNum(ReferenceNumber);
		fxList.setErrCode(ErrorCode);
		fxList.setErrorDesc(ErrorDescription);
	}

}
