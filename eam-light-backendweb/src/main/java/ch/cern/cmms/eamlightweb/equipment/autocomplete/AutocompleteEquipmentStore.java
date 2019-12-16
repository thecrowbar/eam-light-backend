package ch.cern.cmms.eamlightweb.equipment.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipmentStore extends EAMLightController {

	@GET
	@Path("/eqp/store/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		GridRequest gridRequest = new GridRequest("LVSTORA", GridRequest.GRIDTYPE.LOV, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);
		gridRequest.addFilter("store", code.toUpperCase(), "BEGINS");
		gridRequest.sortBy("store");
		return getPairListResponse(gridRequest, "store", "description");
	}

}