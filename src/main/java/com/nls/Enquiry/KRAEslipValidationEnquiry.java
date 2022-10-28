package com.nls.Enquiry;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

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

import com.google.gson.Gson;

@Path("/KRAEslipValidation")
public class KRAEslipValidationEnquiry {
	
	private static DataSource cmDBPool;
	private static String channelDBSchema;
	
	public static void setDBPool(DataSource cmDBPool) {
		KRAEslipValidationEnquiry.cmDBPool = cmDBPool;
	}

	public static void SetSchemaNames(String channelDBSchema) {
		KRAEslipValidationEnquiry.channelDBSchema=channelDBSchema;
	}
	
	@Timeout(value = 4, unit = ChronoUnit.MINUTES)
	@Counted()
	@POST
	@Traced()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = KRAEslipValidationObject.class, responseDescription = "KRA Eslip Response", responseCode = "200")
	@Operation(summary = "KRA Eslip Request", description = "returns KRA Eslip data")
	public Response getKRAEslipValidationDetails(
			@RequestBody(description = "E-Slip Number", required = true, 
			content = @Content(mediaType = "application/json", 
			schema = @Schema(implementation = KRAEslipValidationRequest.class))) KRAEslipValidationRequest id) {

		KRAEslipValidationObject eslipObj = new KRAEslipValidationObject();	

		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("KRA Eslip Validation Interface Started on ["+startTime+"]");
			
			Properties configProperties = new Properties();
			configProperties.load(new FileInputStream("EslipValidation.properties"));
	
			String EslipNumber = id.getEslipNumber();
	
			System.out.println("Fetching KRA Eslip Validation Details for E-Slip Number [" + EslipNumber + "]");
			
			
			try
			{
			KRASOAPValidation.initialiseInteface(configProperties);
            
            LinkedHashMap<String, String> RequestMap = new LinkedHashMap<String, String>();
    		RequestMap.put("EslipNumber", EslipNumber);
    
    		ArrayList EslipResponseInfo = KRASOAPValidation.GetEslipDetails(RequestMap);
            
    	     if (!EslipResponseInfo.isEmpty()) {
    	         for (int i = 0; i < EslipResponseInfo.size(); i++) {
    	        	 KRASOAPValidationObjects EslipDetails = (KRASOAPValidationObjects) EslipResponseInfo.get(i);

    	        System.out.println("Status [" + EslipDetails.getStatus() + "], Tax Payer Full Name [" + EslipDetails.getTaxPayerFullname() + "]");
    	             insertEslipDetails(EslipDetails,EslipNumber);
    	             ResponseMessages(eslipObj,EslipDetails,EslipNumber);
    	         }
    	         }
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println("KRA Eslip Validation Connection Timed Out!!!!!!!");
				eslipObj.setEslipNumber(EslipNumber);
				eslipObj.setErrorCode(ERROR_CODE.NOT_FOUND);
				eslipObj.setErrorMessage(ErrorResponseStatus.FAILED.getValue());
			}
		
		return Response.status(Status.ACCEPTED).entity(eslipObj).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("KRA Eslip Validation Enquiry Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private void ResponseMessages(KRAEslipValidationObject eslipObj,
			KRASOAPValidationObjects EslipDetails,String EslipNumber) {

		eslipObj.setEslipNumber(EslipNumber);
		eslipObj.setStatus(EslipDetails.getStatus());
		eslipObj.setTaxPayerFullname(EslipDetails.getTaxPayerFullname());
		eslipObj.setPaymentAdvicedate(EslipDetails.getPaymentAdvicedate());
		eslipObj.setTaxPayerPin(EslipDetails.getTaxPayerPin());
		eslipObj.setTotalAmount(EslipDetails.getTotalAmount());
		eslipObj.setCurrency(EslipDetails.getCurrency());
		eslipObj.setTaxCode(EslipDetails.getTaxCode());
		eslipObj.setTaxHead(EslipDetails.getTaxHead());
		eslipObj.setTaxComponent(EslipDetails.getTaxComponent());
		eslipObj.setAmountPerTax(EslipDetails.getAmountPerTax());
		eslipObj.setTaxPeriod(EslipDetails.getTaxPeriod());
	}
	
	public void insertEslipDetails(KRASOAPValidationObjects eslipObj,String EslipNumber) throws SQLException {
		
		
		    Connection connectionLocal = cmDBPool.getConnection();		
			Map<String, String> EslipObj=new LinkedHashMap<>();
			EslipObj.put("Status",""+eslipObj.getStatus());
			EslipObj.put("TaxpayerFullName",""+eslipObj.getTaxPayerFullname());
			EslipObj.put("PaymentAdviceDate",""+eslipObj.getPaymentAdvicedate());
			EslipObj.put("TaxpayerPin",""+eslipObj.getTaxPayerPin());
			EslipObj.put("TotalAmount",eslipObj.getTotalAmount());
			EslipObj.put("Currency",""+eslipObj.getCurrency());
			EslipObj.put("TaxCode",""+eslipObj.getTaxCode());
			EslipObj.put("TaxHead",""+eslipObj.getTaxHead());
			EslipObj.put("TaxComponent",""+eslipObj.getTaxComponent());
			EslipObj.put("AmountPerTax",eslipObj.getAmountPerTax());
			EslipObj.put("TaxPeriod",eslipObj.getTaxPeriod());
			
			Gson gson = new Gson();
			String msgRequest = gson.toJson(EslipObj);
			
			PreparedStatement EslipStatement = connectionLocal.prepareStatement("select tbl.* from "+channelDBSchema+"."
					+ " eslip$details tbl where tbl.ESLIP_NO=? ", 1005, 1008);
			EslipStatement.setString(1, EslipNumber);
		    ResultSet EslipDetails = EslipStatement.executeQuery();
		    if(!EslipDetails.next())
		    {
		    EslipDetails.moveToInsertRow();
		    EslipDetails.updateString("ESLIP_NO", EslipNumber);
		    EslipDetails.updateString("ESLIP_DETAIL", msgRequest);
		    EslipDetails.insertRow();
		    }
		    else
		    {
			    EslipDetails.updateString("ESLIP_DETAIL", msgRequest);
			    EslipDetails.updateRow();
		    }
		   //if(connectionLocal.getAutoCommit()==false)connectionLocal.commit();
		    EslipStatement.close();
		    EslipDetails.close();

		    System.out.println(" Eslip Details Inserted:[ " + EslipNumber + "] [" + msgRequest + "]");
			
			}

}
