package com.nls.Enquiry;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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


@Path("/CustomerOnBoardingStatus")
public class CustomerOnBoardingStatus {

	private static DataSource cmDBPool;

	public static void setDBPool(DataSource cmDBPool) {
		CustomerOnBoardingStatus.cmDBPool = cmDBPool;
	}

	
	@POST
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = CustomerOnBoardingStatusObject.class, responseDescription = "Customer Onboarding Status Response", responseCode = "200")
	@Operation(summary = "Customer Onboarding Status Request", description = "returns Customer Onboarding Status")
	public Response getAccountSummary(
			@RequestBody(description = " Customer ID ", required = true, 
			content = @Content(mediaType = "application/json", 
			schema = @Schema(implementation = CustomerOnBoardingStatusRequest.class))) CustomerOnBoardingStatusRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Customer onboarding status Interface Started on ["+startTime+"]");
			
			CustomerOnBoardingStatusObject OnboardStatus = new CustomerOnBoardingStatusObject();
				      
			try (Connection dbConnection = cmDBPool.getConnection();
	
							PreparedStatement CIBPS = dbConnection.prepareStatement(
									" SELECT TBL.* FROM "
									+ " CIB$ONBOARD$DETAILS TBL WHERE UNIT_ID = ? AND CIF = ? AND ACCOUNT_NUMBER = ? ",
									ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
				
				boolean RegistrationStatus=false;
				for (int i = 0; i <id.accts.size(); i++) {
					
					RegistrationStatus=OnBoardcustomers(dbConnection,id.unId,id.cif,id.accts.get(i).accNumber,
							CIBPS,RegistrationStatus);
				}
				CIBPS.close();
				
				            if(!RegistrationStatus)
				            {
				            	OnboardStatus.unId=id.unId;
				            	OnboardStatus.errCode=ERROR_CODE.NOT_FOUND;
				            	OnboardStatus.errorDesc=ErrorResponseStatus.FAILURE.getValue();
				            	OnboardStatus.stats="F";
				            	OnboardStatus.stats=ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue();;
				            }
				
		}
			return Response.status(Status.ACCEPTED).entity(OnboardStatus).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Customer onboarding Status Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}
	
	private synchronized boolean OnBoardcustomers(Connection dbConnection,
			String UnitID,String CustomerId,String AccountNumber,PreparedStatement CIBPS,boolean DataInserted)
	{

		try
		{
		boolean newRecord = false;
           // System.out.println(id.AcctData.get(i).AccNumber + " ");	
		CIBPS.setString(1, UnitID);
		CIBPS.setString(2, CustomerId);
		CIBPS.setString(3, AccountNumber);
		
			try (ResultSet CIBRS = CIBPS.executeQuery()) {
				newRecord = !CIBRS.next();
				if (newRecord) {
					CIBRS.moveToInsertRow();
				}
				CIBRS.updateString("UNIT_ID", UnitID);
				CIBRS.updateString("CIF", CustomerId);
				CIBRS.updateString("ACCOUNT_NUMBER", AccountNumber);
						
				if (newRecord) {
					CIBRS.insertRow();
				}
				else {
					CIBRS.updateRow();
				}
				DataInserted=true;
				if(dbConnection.getAutoCommit()==false)dbConnection.commit();
				
				CIBRS.close();
			}
			CIBPS.clearParameters();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
			
			return DataInserted;
		
	}
}
