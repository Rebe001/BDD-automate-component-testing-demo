package src.library._common;

import src.data.ProjectConstants;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

public class Common_Library {

    private Logger logger = LogManager.getLogger(this.getClass());
    CommonObjects commonObjects;

    public Common_Library(CommonObjects commonObjects) {
        this.commonObjects = commonObjects;
    }

    @Given("^Connection with \"(.*?)\" is checked$")
    public void connection_with_is_checked(String sService) {
        logger.info("Checking connection to service " + sService + "...");
        switch (sService) {
            case ProjectConstants.DEMO_SERVICE:
                Assert.assertTrue(commonObjects.demoServiceController.checkCurrentServiceHealth());
                break;
      }
    }

    @When("^HTTP status code for \"([^\"]*)\" is \"([^\"]*)\"$")
    public void http_status_code_for_is(String sService, String sCode) {
        logger.info("Verifying HTTP status expected code for " + sService + " is '" + sCode + "'");
        final Integer latestStatusCode = commonObjects.getLatestStatusCode(sService);
        Assert.assertTrue("Status code '" + sCode + "' is expected but '" + latestStatusCode + "' is found", sCode, latestStatusCode);
    }

}
