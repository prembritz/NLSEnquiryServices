package com.nls.Enquiry;

import java.sql.Connection;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

@Liveness
@Readiness
@ApplicationScoped
public class ServiceHealthCheck implements HealthCheck {

	private static DataSource cmDBPool;

	@Override
	public HealthCheckResponse call() {
		try (Connection dbConnection = cmDBPool.getConnection()) {
			return HealthCheckResponse.up("Enquiry Service is Up");
		} catch (Exception except) {
			return HealthCheckResponse.down("Unable To Acess DB Pool");
		}
	}

	public static void setDBPool(DataSource cmDBPool) {
		ServiceHealthCheck.cmDBPool = cmDBPool;

	}
}