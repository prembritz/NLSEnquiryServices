package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

@Path("/EodTriggers")
public class EodTriggersSweeperDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private String DateTable = "DATES";

	public static void setDBPool(DataSource cmDBPool) {
		EodTriggersSweeperDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		EodTriggersSweeperDetails.ActualTableName = ActualTableName;
	}


	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = EodTriggersSweepsObject.class, responseDescription = "Eod Triggers Sweeps Response", responseCode = "200")
	@Operation(summary = "Eod Triggers Sweeps Request", description = "returns Eod Triggers Sweeps data")
	public Response getEodTriggersSweeperDetails(
			@RequestBody(description = "Eod Triggers Sweeps Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = EodTriggersSweepsRequest.class))) EodTriggersSweepsRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Eod Triggers Sweeps Interface Started on ["+startTime+"]");
			String unitId = id.unitId;
			String ref=id.referenceNumber;
			System.out.println(
					"Fetching Eod Triggers Sweeps Request fields for unitid: [ " + unitId + " ] " + " processid: [ "
							+ id.processId + " ] referencenumber: [ " + id.referenceNumber + " ] eodstage: [ "
							+ id.eodstage + " ] " + " coreeoddate: [ " + id.coreEodDate + " ] nextbusinessdate: [ "
							+ id.nextBusinessDate + " ] systemdatetime: [ " + id.systemDateTime + " ]");

			System.out.println("EodTriggersSweeperDetails Table Ref's [ " + unitId + " ] [" + DateTable + "]");

			String dateTable = ActualTableName.get(unitId + "-" + DateTable);

			System.out.println("EodTriggersSweeperDetails Actual Table Name [ " + unitId + " ] [" + dateTable + "]");
			boolean flag = false;
			EodTriggersSweepsObject eodTrigger = new EodTriggersSweepsObject();
			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection.prepareStatement(
							"SELECT ID,BATCH_STATUS,TO_CHAR(CURRENT_TIMESTAMP,'DDMMYYYYHH24MISS') AS TIME_STAMP FROM "
									+ " " + dateTable + " WHERE ID=?")) {
				dbSt.setString(1, unitId);
				try (ResultSet dbRs = dbSt.executeQuery()) {
					if (dbRs.next()) {
						flag = true;
						eodTrigger.setUnitId(unitId);
						eodTrigger.setProcessId("EOD-INITIATION");
						eodTrigger.setReferenceNumber(dbRs.getString("ID"));
						if(dbRs.getString("BATCH_STATUS").equals("O"))
							eodTrigger.setEodStage("ONLINE");
						else 
							eodTrigger.setEodStage("RUNNING");
						
						eodTrigger.setStatus(ERROR_CODE.SUCCESSFUL);
						eodTrigger.setSystemDateTime(dbRs.getString("TIME_STAMP"));
					}

					if (!flag) {
						ResponseMessages(eodTrigger,unitId,ref,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(eodTrigger).build();
					}
				}catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(eodTrigger,unitId,ref,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(eodTrigger).build();
				}

			}catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(eodTrigger,unitId,ref,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(eodTrigger).build();
			}
			
			return Response.status(Status.ACCEPTED).entity(eodTrigger).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Eod Triggers Sweeper Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}
	
	private void ResponseMessages(EodTriggersSweepsObject eodTrigger,String unitId,String referenceNumber,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		eodTrigger.setUnitId(unitId);
		eodTrigger.setReferenceNumber(referenceNumber);
		eodTrigger.setErrCode(ErrorCode);
		eodTrigger.setErrorDesc(ErrorDescription);
	}

}
