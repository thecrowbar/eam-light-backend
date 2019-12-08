package ch.cern.cmms.eamlightweb.application;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.user.ScreenLayoutService;
import ch.cern.cmms.eamlightweb.user.ScreenService;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.impl.InforGrids;

@Path("/application")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class ApplicationController extends WSHubController {

	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;

	@GET
	@Path("/applicationdata")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readApplicationData() {
		try {
			GridRequest gridRequest = new GridRequest("BSINST");
			gridRequest.addFilter("installcode", "EL_", "BEGINS");
			return ok(inforClient.getTools().getGridTools().convertGridResultToMap("installcode", "value",
					inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/refreshCache")
	@Produces("application/json")
	@Consumes("application/json")
	public Response cleanCache() {
		ScreenLayoutService.screenLayoutCache.clear();
		ScreenLayoutService.screenLayoutLabelCache.clear();
		ScreenService.screenCache.clear();
		InforGrids.gridFieldCache.clear();
		return ok("EAM Light cache has been successfully refreshed.");
	}

}