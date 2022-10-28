package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
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

@Path("/MiniStatement")
public class MiniStatement {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static String AccountTableRef="ACCOUNT";
	
	public static void setDBPool(DataSource cmDBPool) {
		MiniStatement.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		MiniStatement.ActualTableName = ActualTableName;
	}

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = MinStatementList.class, responseDescription = "Mini Statement Response", responseCode = "200")
	@Operation(summary = "Mini Statement Request", description = "returns Mini Statement Data")
	public Response getMiniStatementDetails(
			@RequestBody(description = "Mini Statement Request Id", required = true, 
			content = @Content(mediaType = "application/json", 
			schema = @Schema(implementation = MiniStatementRequest.class))) MiniStatementRequest id) {
		
		MiniStatementObject miniStmtObj = null;
		MinStatementList MinistmtList=new MinStatementList();
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Mini Statement Interface Started on ["+startTime+"]");
			String unitId = id.unId;
			String AccountNumber=id.accountNo;
			
			String AccountTable=ActualTableName.get(unitId+"-"+AccountTableRef);
			System.out.println("Fetching Mini Statement Request Fields Unitid: [ "
					+ unitId + " ]" + " AccNumber: [ " + AccountNumber+ " ]");

			Connection dbConnection = cmDBPool.getConnection();
			PreparedStatement PS=dbConnection.prepareStatement("select id from "+AccountTable+" where id=? ");
			PS.setString(1, AccountNumber);
			ResultSet RS=PS.executeQuery();
			if(!RS.next())
			{
				 MinistmtList.unitID=unitId;
				 MinistmtList.accNumber=AccountNumber;
				 MinistmtList.errCode=ERROR_CODE.NOT_FOUND;
				 MinistmtList.errorDesc=ErrorResponseStatus.ACCOUNT_NOT_FOUND.getValue();
				 return Response.status(Status.ACCEPTED).entity(MinistmtList).build();
			}
			RS.close();
			PS.close();
			
			boolean DataExists=false;
			 MiniStatementMobi statementInterface = new MiniStatementMobi();
			String generateMiniStatement[][] = statementInterface.generateMiniStatement(
					dbConnection, "" + AccountNumber, 10,unitId, null);
			 int entryCnt = generateMiniStatement.length;
			 System.out.println("entryCnt ="+entryCnt);
			 
				 MinistmtList.unitID=unitId;
				 MinistmtList.accNumber=AccountNumber;
				 
			 for (int i = 0; i < generateMiniStatement.length; i++) {
			    // for (int j = 0; j < generateMiniStatement[i].length; j++) {
			     //System.out.print(generateMiniStatement[i][j] + "\t");
				 if(generateMiniStatement[i][0]!=null)
				 {
				 DataExists=true;
			     miniStmtObj = new MiniStatementObject();
			     miniStmtObj.setValDate(generateMiniStatement[i][0]);
			     miniStmtObj.setTransCode(generateMiniStatement[i][1]);
			     miniStmtObj.setTransAmt(Double.parseDouble(generateMiniStatement[i][2]));
			     miniStmtObj.setCrfType(generateMiniStatement[i][3]);
			     miniStmtObj.setCurrency(generateMiniStatement[i][4]);
			     miniStmtObj.setTransReference(generateMiniStatement[i][5]);
			     miniStmtObj.setNarr(generateMiniStatement[i][6]);
			     MinistmtList.addAccount(miniStmtObj);
				 }
			    // }
			    // System.out.println();
			     }
			 
			 if(!DataExists)
			 {
				 MinistmtList.errCode=ERROR_CODE.NOT_FOUND;
				 MinistmtList.errorDesc=ErrorResponseStatus.DATA_NOT_FOUND.getValue();
			 }
			
			return Response.status(Status.ACCEPTED).entity(MinistmtList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Mini Statement Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}
}
