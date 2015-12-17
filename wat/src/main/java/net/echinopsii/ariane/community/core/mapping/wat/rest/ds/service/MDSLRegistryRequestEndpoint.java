package net.echinopsii.ariane.community.core.mapping.wat.rest.ds.service;

/**
 * Created by sagar on 17/12/15.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.mapping.wat.helper.MDSLRegistryRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

@Path("/mapping/registryRequest")
public class MDSLRegistryRequestEndpoint {
    private static final Logger log = LoggerFactory.getLogger(MDSLRegistryRequestEndpoint.class);
    private EntityManager em;

    @POST
    @Path("/deleteRequest")
    public Response deleteRequest(@QueryParam("data") String params) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> postData = mapper.readValue(params, Map.class);
        long requestID = Long.valueOf((String) postData.get("requestID"));
        MDSLRegistryRequestHelper md = new MDSLRegistryRequestHelper();
        Boolean responseVal = md.deleteRequest(requestID);
        return Response.status(Response.Status.OK).entity("Request " + requestID + "has been successfully deleted").build();
    }
}
