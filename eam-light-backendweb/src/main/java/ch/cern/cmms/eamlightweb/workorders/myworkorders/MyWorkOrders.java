package ch.cern.cmms.eamlightweb.workorders.myworkorders;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.EAMUser;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.logging.Level;

@ApplicationScoped
public class MyWorkOrders {

    @Inject
    private AuthenticationTools authenticationTools;
    @Inject
    private InforClient inforClient;

    public List<MyWorkOrder> getMyOpenWorkOrders() throws InforException {
        String userCode = authenticationTools.getInforContext().getCredentials().getUsername();
        EAMUser eamUser = inforClient.getUserSetupService().readUserSetup(authenticationTools.getInforContext(), userCode);
        GridRequest gridRequest = new GridRequest("93", "WSJOBS", "2005");
		gridRequest.setUserFunctionName(gridRequest.getGridName());
        gridRequest.addFilter("assignedto", eamUser.getUserCode(), "=", GridRequestFilter.JOINER.AND);
        gridRequest.addFilter("evt_rstatus", "R", "=");
        return inforClient.getTools().getGridTools().convertGridResultToObject(MyWorkOrder.class,
                null,
                inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
    }

    public List<MyWorkOrder> getMyTeamsWorkOrders() throws InforException {
        String userDepartments = readUserDepartments();
        if (userDepartments.isEmpty()) {
            inforClient.getTools().log(Level.SEVERE, "No userDepartment found!");
            return new LinkedList<>();
        }

        GridRequest gridRequest = new GridRequest("93", "WSJOBS", "2005");
		gridRequest.setUserFunctionName(gridRequest.getGridName());
        gridRequest.addFilter("department", userDepartments, "IN", GridRequestFilter.JOINER.AND);
        gridRequest.addFilter("evt_rstatus", "R", "=");
        return inforClient.getTools().getGridTools().convertGridResultToObject(MyWorkOrder.class,
                null,
                inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
    }

    public List<MyWorkOrder> getObjectWorkOrders(String equipmentCode) throws InforException {
        GridRequest gridRequest = new GridRequest("93", "WSJOBS", "2005");
        gridRequest.setRowCount(2000);
        gridRequest.setUseNative(false);
        gridRequest.addFilter("equipment", equipmentCode, "=");
        gridRequest.sortBy("datecreated", "DESC");
        return inforClient.getTools().getGridTools().convertGridResultToObject(MyWorkOrder.class,
                null,
                inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
    }


    private String readUserDepartments() throws InforException {
        String userCode = authenticationTools.getInforContext().getCredentials().getUsername();
        EAMUser eamUser = inforClient.getUserSetupService().readUserSetup(authenticationTools.getInforContext(), userCode);
        String departments = "";
        if (eamUser.getDepartment() != null) {
            departments = eamUser.getDepartment();
        }
        if (eamUser.getUserDefinedFields().getUdfchar10() != null) {
            if (!departments.isEmpty()) {
                departments += ",";
            }
            departments += eamUser.getUserDefinedFields().getUdfchar10();
        }
        return departments;
    }

}
