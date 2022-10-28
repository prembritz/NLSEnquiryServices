package com.nls.Enquiry;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Collectors;

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

//@Path("/CreditCardDetails")
public class CreditCardDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName;
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static String AcctTableRef = "ACCOUNT";

	
	public static void setDBPool(DataSource cmDBPool) {
		CreditCardDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		CreditCardDetails.ActualTableName = ActualTableName;
	}

	@POST
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = CreditCardDetailsObject.class, responseDescription = "Credit Card Details Response", responseCode = "200")
	@Operation(summary = "Credit Card Details Request", description = "returns Credit Card Details data")
	public Response getCreditCardDetails(
			@RequestBody(description = "Credit Card Account Number", required = true, 
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditCardDetailsRequest.class))) 
			CreditCardDetailsRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Credit Card Details Interface Started on ["+startTime+"]");
			System.out.println("Fetching Credit Card Details For [ " + id.ccAccountNumber + " ]");

			CreditCardDetailsObject CCDetailObj = new CreditCardDetailsObject();
			
			Properties configProperties = new Properties();
			configProperties.load(new FileInputStream("CreditCardServices.properties"));
			
			
			System.out.println("Credit Card Details Table Ref [" + AcctTableRef + "]");

			String unitId = id.unId;
			String AcctTable = ActualTableName.get(unitId + "-" + AcctTableRef);

			System.out.println("Credit Card Details Table Names [" + AcctTable + "]");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection.prepareStatement(
							"select ID,CUSTOMER_ID," + " ACCOUNT_TITLE,CURRENCY,CATEGORY,ARRANGMENT_ID " + " from "
									+ AcctTable + " WHERE id = ? ")) {
				dbSt.setString(1, id.ccAccountNumber);

				try (ResultSet dbRs = dbSt.executeQuery()) {
					if (dbRs.next()) {

						CCDetailObj.setUnId(id.unId);
						
                        CCAcquireTicket.initialiseInteface(configProperties);
                        
                        LinkedHashMap<String, String> TicketHeaderMap = new LinkedHashMap<String, String>();
                		TicketHeaderMap.put("MessageID", "Msg3");
                		TicketHeaderMap.put("CorrelationID", "");
                		TicketHeaderMap.put("SystemID", "");
                		TicketHeaderMap.put("RequestorID", "ICON");
                		TicketHeaderMap.put("Ticket",
                				"PFRpY2tldD4NCiAgICA8aG9zdElQPjwvaG9zdElQPg0KICAgIDxhcHBsaWNhdGlvbk5hbWU+SUNPTjwvYXBwbGljYXRpb25OYW1lPg0KPC9UaWNrZXQ+");
                		TicketHeaderMap.put("CallerRef", "Msg1");
                		TicketHeaderMap.put("Origin", "Internal");
                		TicketHeaderMap.put("Culture", "en-us");

                		LinkedHashMap<String, String> TicketMap = new LinkedHashMap<String, String>();
                		TicketMap.put("hostIP", "");
                		TicketMap.put("applicationName", "ICON");
                		
                		ArrayList TicketInfo = CCAcquireTicket.GetTicket(TicketHeaderMap,TicketMap);
                        
                	     if (!TicketInfo.isEmpty()) {
                	         for (int i = 0; i < TicketInfo.size(); i++) {
                	        CCAcquireTicketObjects TicketDetails = (CCAcquireTicketObjects) TicketInfo.get(i);

                	        System.out.println("Message Id [" + TicketDetails.getMessageId() + "], Ticket [" + TicketDetails.getTicket() + "]");
                	        
                	        LinkedHashMap<String, String> EntityHeaderMap = new LinkedHashMap<String, String>();
                			
                				EntityHeaderMap.put("MessageID", TicketDetails.getMessageId());
                				EntityHeaderMap.put("Ticket", TicketDetails.getTicket());

                			LinkedHashMap<String, String> EntityMap = new LinkedHashMap<String, String>();
                			
                			EntityMap.put("Entity", "Card");
                			EntityMap.put("Reference", "C");
                			EntityMap.put("Number", "4025370000004390");
                			
                	         }} 

					} else {
						CCDetailObj.setErrCode(ERROR_CODE.NOT_FOUND);
						CCDetailObj.setErrorDesc("Credit Card Account Doesn't Exist");
					}
					dbRs.close();
					dbSt.close();

				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return Response.status(Status.ACCEPTED).entity(CCDetailObj).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Credit Card Details Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	public Vector<String> nlsSplit(String expression, String delimeter) {
		Vector<String> tokens = new Vector<String>();
		if (expression == null) {
			tokens.add("");
			return tokens;
		}
		int index = 0;
		String tempString = "";
		while ((index = expression.indexOf(delimeter)) != -1) {
			tempString = expression.substring(0, index);
			expression = expression.substring(index + 1, expression.length());
			tokens.add(tempString);
		}
		tokens.add(expression);
		return tokens;
	}

	public static Vector<String> split(String expression, String delimeter) {
		Vector<String> tokens = new Vector<String>();
		if (expression == null) {
			tokens.add("");
			return tokens;
		}
		int index = 0;
		String tempString = "";
		do {
			index = expression.indexOf(delimeter);
			if (index == -1) {
				break;
			} else {
				tempString = expression.substring(0, index);
				expression = expression.substring(index + 1, expression.length());
				tokens.add(tempString);
			}
		} while (true);
		tokens.add(expression);

		return tokens;
	}
}
