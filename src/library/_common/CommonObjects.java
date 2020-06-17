package src.library._common;

import src.data.ProjectConstants;
import com.gi_de.idspit.client.servicegroup.api.ProductionApi.DemoServiceController;
import com.gi_de.idspit.fw.data.Service;
import org.apache.logging.log4j.Logger;

public class CommonObjects implements DockerObjects {

    private Logger logger = LogManager.getLogger(this.getClass());

    // Project Config
    private ProjectConfig projectConfig;

    // Service clients
    public DemoSerivceController demoSeriviceController;

    /** 
    initialise demo service controller */
    public CommonObjects() {
        demoSeriviceController = new DemoServiceController(projectConfig);
    }

    /**
    Get Latest status code from specified service
    */
    public Integer getLatestStatusCode(String sService)
    {
        switch (sService){
            case ProjectConstants.DEMO_SERVICE:
                return this.demoServiceController.getdemoService().getLastHttpStatusCode();
        }
    }

    
}
