package com.nls.Enquiry;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

@Path("/TransactionStatus")
public class TransactionStatus {

	private static DataSource cmDBPool;

	private static HashMap<String, String> ActualTableName;
	private static HashMap<String, String> GlobalParameters;
	private static Map<String, Integer> AccountTypes = new HashMap<String, Integer>();
	private static String ChannelDBSchema;

	public static void setDBPool(DataSource cmDBPool) {
		TransactionStatus.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		TransactionStatus.ActualTableName = ActualTableName;
	}
	
	public static void setInitiailizeGlobalParameters(HashMap<String, String> GlobalParameters) {
		TransactionStatus.GlobalParameters = GlobalParameters;
	}

	public static void setSchemaNames(String ChannelDBSchema) {
		TransactionStatus.ChannelDBSchema = ChannelDBSchema;
	}

	@Timeout(value = 3, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = TransactionStatusList.class, responseDescription = "Transaction Status Response", responseCode = "200")
	@Operation(summary = "Transaction Status Request", description = "returns Transaction Status Data")
	public Response getTransactionStatusData(
			@RequestBody(description = "Transaction Id", required = true, 
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionStatusRequest.class))) TransactionStatusRequest id) {
	
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Transaction Status Interface Started on ["+startTime+"]");
			String TransId="";
			TransactionStatusList transList = new TransactionStatusList();
			TransactionStatusObject transStatusObj = null;

			int count = 0;
			boolean Exists;

			String unitId = id.unId;
	
			Connection dbConnection = cmDBPool.getConnection();
             ArrayList<Boolean>RespStatus=new ArrayList<Boolean>();
			for (int i = 0; i < id.transData.size(); i++) {
				Exists = false;
				TransId = id.transData.get(i).transactionId;
				System.out.println("Transaction ID="+TransId);

				System.out.println("Fetching Transaction Status Using Transaction Id [ " + TransId
						+ " ]");

				try (PreparedStatement dbSt = dbConnection
						.prepareStatement("SELECT TRANS_UNIQ_REFERENCE,SERV_UNIQUE_REFERENCE "
								+ "  FROM "+ChannelDBSchema+".serv$trans$data "
								+ " WHERE TRANS_UNIQ_REFERENCE=?")) {
					dbSt.setString(1, TransId);
					try (ResultSet dbRs = dbSt.executeQuery()) {

						if (dbRs.next()) {			
							RespStatus.add(true);
							if (++count == 1)
								transList.setUnId(unitId);

							transStatusObj = new TransactionStatusObject();
							transStatusObj.setTransactionId(TransId);
							transStatusObj.setCoreReference(GlobalParameters.get("CBX_PREFIX") + "" + dbRs.getString("SERV_UNIQUE_REFERENCE"));					
							transList.addAccount(transStatusObj);
						}
						else
						{
							RespStatus.add(Exists);
							ResponseMessages(transList,transStatusObj,unitId,TransId,"",
									ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TRANSACTION_NOT_EXISTS.getValue());				
						}
						dbRs.close();
						dbSt.close();

					}
					catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						//System.out.println("&&&&&&&&&&&&");
						RespStatus.add(Exists);
						ResponseMessages(transList,transStatusObj,unitId,TransId,"",
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());	
					}

				}
				catch (Exception e) {
					e.printStackTrace();
					RespStatus.add(Exists);
					ResponseMessages(transList,transStatusObj,unitId,TransId,"",
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());		
				}
			}
			
			return Response.status(Status.ACCEPTED).entity(transList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Transaction Status Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}
	
	
	private void ResponseMessages(TransactionStatusList transList,TransactionStatusObject transStatusObj,String unitId,String TransId,String coreReference,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		transStatusObj = new TransactionStatusObject();
		transStatusObj.setTransactionId(TransId);
		transStatusObj.setCoreReference(coreReference);
		transStatusObj.setErCode(ErrorCode);
		transStatusObj.setErMsg(ErrorDescription);
		transList.setUnId(unitId);
		transList.addAccount(transStatusObj);		
	}

}
