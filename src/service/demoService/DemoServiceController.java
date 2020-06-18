package src.service.demoService;

import src.data.ProjectConfig;
import src.service._common.BaseClient;
import src.resources.jms.JmsMessageRequest;
import src.resources.jms.JmsMessageResponse;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.HttpClientErrorException;

import javax.jms.*;
import java.io.IOException;

public class DemoServiceController extends BaseClient {

    private DemoService demoService;
    private Logger logger = LogManager.getLogger(this.getClass());
    private JdbcTemplate jdbcTemplate = getJbdcTemplate();

    public DemoServiceController(ProjectConfig projectConfig){
        super(ePlatform.ResponseService, projectConfig);
        this.demoService = new DemoService();
    }

    public DemoService getDemoService() {
        return demoService;
    }

    /**
     * Clear up all DataBase in Demo Service
     */
    public void dbCleanup() {
        String[] tables = new String[]{};
        dbCleanup(jdbcTemplate,tables, ProjectConstants.DEMO_USER);
        logger.info("Finish Cleaning DemoService DataBase");
    }


    /**
     * Prepare JMS Request
     */
    private JmsMessageRequest prepareJMSRequest() {
        final String configId = this.getDemoService().getConfiguration();
        JmsMessageRequest request = new JmsMessageRequest();
        request.setConfigurationId(configId);
        return request;
    }

    /**
     * Create Common Function to send Produce JMS
     */
    private void sendProduceJMS(Object request, String queue_name){
        final String url = "tcp://" + getIPJms() + ":" + JMS_PORT;
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(session.createQueue(queue_name));
            TextMessage message= session.createTextMessage(request.toString());
            logger.info("sending message to queue: " + message.getText());
            producer.send(message);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     Send Produce JMS Function */
    public void sendProduceJMSFunction(){
        JmsMessageRequest request = prepareJMSRequest();
        try {
            sendProduceJMS(request,"demo.request");
        } catch (JMSException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Post JSON in Render Output File
     */
    public void postRenderOutputFile(String status, String responseId){
        ResponseEntity<String> response;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("OK");
        HttpEntity<String> httpRequest = new HttpEntity<>(jsonObject.toString(), headers);
        try {
            response = exchangePostEndpoint(httpRequest,"/demo-service/render-output");
            this.getDemoService().setLastHttpStatusCode(response.getStatusCodeValue());
            this.getDemoService().setLastJmsMessageResponse(response);
        }
    } catch (HttpClientErrorException e) {
        this.getDemoService().setLastHttpStatusCode(e.getStatusCode().value());
    }
}

    /**
     * Consume JMS produced by Job Service
     * @throws JMSException JMSException produced when consuming the queue
     * @throws IOException IOException produced when consuming the queue
     */
    public void consumeJMSFunction() throws JMSException, IOException {
        JmsMessageResponse responseJMS = super.sendConsumeJMS(JmsMessageResponse.class, "response.response");
        this.getDemoService().setLastJmsMessageResponse(responseJMS);
        logger.info("consume JMS {}", responseJMS);
    }

    /**
     * Method to check if stage
     * @return stage
     */
    public String checkStageDatabase()  {
        String query = String.format("SELECT STAGE FROM TBL_DEMO");
        String result = jdbcTemplate.queryForObject(query, String.class);
        return result;
    }
}
