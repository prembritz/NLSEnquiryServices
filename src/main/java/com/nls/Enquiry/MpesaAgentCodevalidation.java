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

@Path("/MpesaAgentCodeValidation")
public class MpesaAgentCodevalidation {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	private static String AgentInfoTableRef = "CBA$CORP$AGENTS$INFO";

	public static void setDBPool(DataSource cmDBPool) {
		MpesaAgentCodevalidation.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		MpesaAgentCodevalidation.ActualTableName = ActualTableName;
	}

	@Timeout(value = 5, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = MpesaAgentCodevalidationObject.class, responseDescription = "Mpesa Agent Validation Response", responseCode = "200")
	@Operation(summary = "Mpesa Agent Validation Request", description = "returns Mpesa Agent Validation Data")
	public Response getMpesaAgentCodevalidationDetails(
			@RequestBody(description = "Mpesa Agent Validation Request Id", required = true,
			content = @Content(mediaType = "application/json", 
			schema = @Schema(implementation = MpesaAgentCodevalidationRequest.class))) MpesaAgentCodevalidationRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Deal Rate interface Started on ["+startTime+"]");
			String unitId = id.unitID;
			String AgentCode=id.agentCode;
		
			boolean flag = false;
			int i = 1;

			System.out.println("Fetching Mpesa Agent Validation Request Fields for Unitid: [ "
					+ unitId + " ]" + " Agent code: [ " + AgentCode+ " ] ");

			System.out.println("Mpesa Agent Validation Details Table Ref [ " + unitId + " ] [" + AgentInfoTableRef + "]");

			String AgentInfoTable = ActualTableName.get(unitId + "-" + AgentInfoTableRef);

			System.out.println("Mpesa Agent Validation Details Actual Table Ref [ " + unitId + " ] [" + AgentInfoTable + "]");

			MpesaAgentCodevalidationObject dealObj = new MpesaAgentCodevalidationObject();
			String query = "SELECT REGEXP_SUBSTR(ID, '[^*]+') AS AGENT_ACCOUNT,AGENT_NAME,ID FROM " + AgentInfoTable;
			if (!AgentCode.equals("")) {
			  query += " WHERE AGENT_CODE='" + AgentCode + "'";
			}
			System.out.println("Mpesa Agent Validation Details query [" + query + "]");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection.prepareStatement(query)) {
				try (ResultSet dbRs = dbSt.executeQuery()) {
					if (dbRs.next()) {
						flag = true;
				System.out.println("**********************");
						dealObj.agentCode=AgentCode;
						dealObj.agentName=dbRs.getString("AGENT_NAME");
						dealObj.agentAccount=dbRs.getString("AGENT_ACCOUNT");
					}
					dbRs.close();
					if (!flag) {
						ResponseMessages(dealObj,unitId,AgentCode,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(dealObj).build();
					}

				} catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(dealObj,unitId,AgentCode,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(dealObj).build();
				}

			} catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(dealObj,unitId,AgentCode,
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
			System.out.println("Mpesa Agent Validation Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private void ResponseMessages(MpesaAgentCodevalidationObject dealObj,String unitId,String AgentCode,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		dealObj.agentCode=AgentCode;
		dealObj.unitId=unitId;
		dealObj.errCode=ErrorCode;
		dealObj.errorDesc=ErrorDescription;
	}
}
