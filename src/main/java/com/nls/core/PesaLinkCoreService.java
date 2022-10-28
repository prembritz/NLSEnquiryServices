package com.nls.core;

import java.io.Closeable;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/gtw/exposed")
@RegisterRestClient(configKey = "PESLINK_CORE_SERVICES") // https://trinity.cbaloop.com
public interface PesaLinkCoreService extends Closeable {
	
	
	// @Timeout(value=50,unit=ChronoUnit.MILLIS)
	@POST
	@Traced
	@Path("/accountInquiry")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//public PesaLinkAccountValidationCoreObject PesaLinkAccountValidationCall(PesaLinkAccountValidationCoreRequest LoopRequest);
	public String PesaLinkAccountValidationCall(PesaLinkAccountValidationCoreRequest LoopRequest);
}