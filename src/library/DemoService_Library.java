package src.library.responseservice;

import src.library._common.CommonObjects;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

public class DemoService_Library {

    private static Logger logger = LogManager.getLogger(this.getClass());
    private CommonObjects commonObjects;

    public ResponseService_Library(CommonObjects commonObjects) {
        this.commonObjects = commonObjects;
    }

    @Before
    public void atTheBeginningOfTheTestCase() {
        commonObjects.responseServiceController.dbCleanup();
        commonObjects.responseServiceController.cleanQueues("demo.request","demo.response");
    }
    
    @When("DemoService set configuration \"([^\"]*)\"$")
    public void demoService_set_configuration_is(String config){
        logger.info("ResponseService set configuration {}", config);
        commonObjects.demoServiceController.getDemoService().setConfiguration(config);
    }
   

    @When("^DemoService produce JMS message by MockService$")
    public void demoService_produce_jms_message_by_mock_service(){
        logger.info("DemoService produce JMS message by MockService");
        commonObjects.demoServiceController.sendProduceJMSFunction();
    }

    
    @When("^DemoService check stage is \"([^\"]*)\"$")
    public void ResponseServiceCheckStageIs(String stage) {
        logger.info("DemoService check stage is {}", stage);
        Assert.assertEquals("Something was wrong checking demo stage in database", stage, commonObjects.demoServiceController.checkStageDatabase());
    }

    @When("^DemoService POST render output file$")
    public void demoService_POST_render_output_file() {
        logger.info("DemoService POST render output file");
        commonObjects.demoServiceController.postRenderOutputFile();
    }
   
    @When("^DemoService consume JMS message by MockService$")
    public void demoService_consume_jms_message_by_mock_service(){
        logger.info("DemoService consume JMS message by Mock Service");
        commonObjects.demoServiceController.consumeJMSFunction();
    }

    @Then("^Verify DemoService JMS message$")
    public void verify_DemoService_response_JMS_message(){
        logger.info("Verify DemoService response JMS message");
        JmsMessageResponse response = commonObjects.demoServiceController.getDemoService().getLastJmsMessageResponse();
        Assert.assertNotNull("JMS response message is empty", response);
        Assert.assertEquals("Function did not complete", "COMPLETED", response.getFunctionStatus());
        Assert.assertNull("Unexpected error code", response.getFunctionErrorCode());
    }

}