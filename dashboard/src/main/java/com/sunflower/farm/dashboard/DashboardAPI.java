package com.sunflower.farm.dashboard;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;

/**
 * REST API for dashboard frontend
 */
@Path("/")
public class DashboardAPI {

    private DashboardDataManager dataManager = new DashboardDataManager();

    @GET
    @Path("/dashboard")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboardData() {
        Map<String, Object> data = dataManager.getDashboardData();
        return Response.ok(data).build();
    }

    @GET
    @Path("/humidity/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHumidityHistory(@QueryParam("hours") @DefaultValue("24") int hours) {
        List<Map<String, Object>> history = dataManager.getHumidityHistory(hours);
        return Response.ok(history).build();
    }

    @GET
    @Path("/temperature/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemperatureHistory(@QueryParam("hours") @DefaultValue("24") int hours) {
        List<Map<String, Object>> history = dataManager.getTemperatureHistory(hours);
        return Response.ok(history).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "online");
        status.put("message", "Sunflower Farm Dashboard API is running");
        status.put("version", "1.0");
        return Response.ok(status).build();
    }
}