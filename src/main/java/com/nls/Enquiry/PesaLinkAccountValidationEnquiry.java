package com.nls.Enquiry;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;
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
import com.nls.core.PesaLinkAccountValidationCoreObject;
import com.nls.core.PesaLinkAccountValidationCoreRequest;
import com.nls.core.PesaLinkCoreService;

@Path("/PesaLinkAccountValidationEnquiry")
public class PesaLinkAccountValidationEnquiry {
	
	@Inject
	PesaLinkCoreService coreServices;

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Counted()
	@POST
	@Traced()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = PesaLinkAccountValidationObject.class,
	responseDescription = "PesaLink Account Validation Response", responseCode = "200")
	@Operation(summary = "PesaLink Account Validation Request", description = "returns PesaLink Account Validation data")
	public Response getLoopAccountValidationDetails(
			@RequestBody(description = "Account Id", required = true, 
			content = @Content(mediaType = "application/json", 
			schema = @Schema(implementation = PesaLinkAccountValidationRequest.class))) PesaLinkAccountValidationRequest id) {

		PesaLinkAccountValidationObject LoopAccObj = new PesaLinkAccountValidationObject();
		PesaLinkAccountValidationCoreObject LoopAccCoreObj = null;
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("PesaLink Account Validation Interface Started on ["+startTime+"]");
			Gson g = new Gson();
			String AccountId = id.getAccountId();
			String uniqueId = id.getUniqueId();
			String bankId = id.getBankId();
			
			System.out.println("Fetching PesaLink Account Validation Details for Account Id [" + AccountId + "],["+uniqueId+"],["+bankId+"]");
			
		
			// Loop Account Validation

			try
			{
				String prettyJsonString = g.toJson(id);
				System.out.println("Pesalink Request ["+prettyJsonString.toString()+"]");
				 String PesalinkReponses = coreServices
					.PesaLinkAccountValidationCall(
							new PesaLinkAccountValidationCoreRequest(AccountId,bankId,uniqueId));					
					System.out.println("Connection Established Account Id ["+AccountId+"]");
				System.out.println("Pesalink Response ["+PesalinkReponses+"]");		 
				LoopAccCoreObj = g.fromJson(PesalinkReponses, PesaLinkAccountValidationCoreObject.class); 
			//	System.out.println(LoopAccCoreObj.getMsgId());
			//	System.out.println(LoopAccCoreObj.getAccountId());
				System.out.println("Response code ["+LoopAccCoreObj.getResponseCode()+"], Response Desc ["+LoopAccCoreObj.getResponseDesc()+"]");
				ResponseMessages(LoopAccObj,AccountId,uniqueId,LoopAccCoreObj.getBankId(),
						LoopAccCoreObj.getAccountName(),LoopAccCoreObj.getMsgId(),LoopAccCoreObj.getResponseCode(),
						LoopAccCoreObj.getResponseDesc());
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Connetion Timed out for Pesalink !!!!");
				ResponseMessages(LoopAccObj,AccountId,uniqueId,bankId,
						"","",ErrorResponseStatus.NOT_FOUND.getValue(),
						ErrorResponseStatus.FAILED.getValue());
			}
			
		return Response.status(Status.ACCEPTED).entity(LoopAccObj).build();
		
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("PesaLink Account Validation Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private void ResponseMessages(PesaLinkAccountValidationObject LoopAccObj,String AccountId,
			String UniqueId,String BankId,
			String AccountName,String MsgId,String ErrorCode,String ErrorDesc) {

		LoopAccObj.setUniqId(UniqueId);
		LoopAccObj.setAccId(AccountId);
		LoopAccObj.setBkId(BankId);
		LoopAccObj.setAccName(AccountName);
		LoopAccObj.setMsgId(MsgId);
		LoopAccObj.setRespCode(ErrorCode);
		LoopAccObj.setRespDesc(ErrorDesc);
	}

}
