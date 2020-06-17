package src.service._common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.gi_de.idspit.client.data.ProjectConfig;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.jms.*;
import javax.jms.Queue;
import java.io.*;
import java.lang.IllegalStateException;
import java.net.URI;
import java.util.*;

import static src.data.ProjectConfig.ACTIVEMQ_PORT;
import static src.data.ProjectConstants.DEMO_SERVICE;

public class BaseClient {

    private Logger logger = LogManager.getLogger(this.getClass());
    protected enum ePlatform{ DemoService;}
    private ProjectConfig projectConfig;
    private JdbcTemplate jdbcTemplate;
    protected HttpHeaders headers;
    private String sUser;
    private String sPass;
    private String sIPAddress;
    private String sPortNumber;
    private UriComponentsBuilder uriBuilder;

    public BaseClient(ePlatform platform,  ProjectConfig projectConfig) throws IOException {
        this.projectConfig = projectConfig;
        getCredentials(platform);
        this.jdbcTemplate = new JdbcTemplate();
    }

    private void getCredentials(ePlatform sService) throws IOException {
        Properties props = new Properties();
        switch (sService) {
            case DemoService:
                this.sUser = props.getProperty("DEMO_SERVICE_USER");
                this.sPass = props.getProperty("DEMO_SERVICE_PASSWORD");
                break;
        }
    }

    public String getsUser() {
        return this.sUser;
    }

    public String getsPass() {
        return this.sPass;
    }

    public String getsIPAddress() {
        return sIPAddress;
    }


    public String getsPortNumber() {
        return sPortNumber;
    }

    //Rest call related
    /**
     * Execute HTTP POST directly to an endpoint
     * @param httpRequestBody request body
     * @param sEndPoint endpoint after the port
     * @return responseTest response from the http
     * @throws HttpClientErrorException
     */
    public ResponseEntity<String> exchangePostEndpoint(HttpEntity<?> httpRequestBody, String sEndPoint) throws HttpClientErrorException {

        RestTemplate restTemplate = new RestTemplate();

        logger.debug("Sending POST to: " + getsIPAddress() + ":" + getsPortNumber());

        uriBuilder = UriComponentsBuilder.fromHttpUrl(getsIPAddress()
                + ":" + getsPortNumber()
                + sEndPoint);

        ResponseEntity<String> responseTest = restTemplate.exchange(
                uriBuilder.build().encode().toUri(),
                HttpMethod.POST,
                httpRequestBody,
                String.class);
        return responseTest;
    }


    /**
     Check current service Health */
    public boolean checkCurrentServiceHealth() {
        boolean healthy = false;
        ResponseEntity<String> responseTest;
        String url = getsIPAddress() + ":" + getsPortNumber();
        try {
            responseTest = new RestTemplate().exchange(URI.create(url), HttpMethod.GET, null, String.class);
            healthy = true;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
        }
        return healthy;
    }


    //JMS Related
    /**
     Send request in specified queueName */
    public void sendProduceJMSFunction(Object request, String queueName) throws JMSException, IOException {
        logger.info("Sending reuest {} to queue {}", request, queueName);
        switch (queueName) {
            case "demo.request":
                this.sendProduceJMS(request, queueName);
                break;
            default:
                throw new IllegalStateException("Not Existing function");
        }

    }

    /**
     common produce JMS function
     */
    protected void sendProduceJMS(Object request, String queueName) throws JMSException, IOException
    {
        final String url;
        url= "tcp://" + getsIPAddress() + ":" + ACTIVEMQ_PORT;

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(session.createQueue(queueName));
            ObjectMapper om= new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            TextMessage message =  session.createTextMessage(request.toString());
            logger.info("sending message to queue: " + message.getText());
            producer.send(message);
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     common function for consume JMS in specified queueName
     */
    public <T> T sendConsumeJMS(Class<T> valueType, String queueName) throws JMSException, IOException {
        Connection connection = null;
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    "tcp://" + getsIPAddress() + ":" + ACTIVEMQ_PORT);
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue queue = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            TextMessage message = (TextMessage) consumer.receive( 10000);
            ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
            T response = mapper.readValue(message.getText(), (Class<T>) valueType);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.close();
        }
        return null;
    }
    /**
     * Clean the specified queue
     * @param queuesName
     */
    public void cleanQueues(String... queuesName) {
        if (queuesName != null){
            try {
                for (String queueName : queuesName)
                    consumeAllMessage(queueName);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Consume all the messages from the queue in the parameter
     * This function is made to clear a queue list before every test
     * @param queueName
     */
    private void consumeAllMessage(String queueName)  throws JMSException {
        Connection connection = null;

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + getIPJms() + ":" + ACTIVEMQ_PORT);
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            MessageConsumer messageConsumer = session.createConsumer(queue);
            messageConsumer.receive(5);
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            if (connection != null)
                connection.close();
        }

    }
    // DB RELATED:
    public JdbcTemplate getJbdcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String sUrl = "jdbc:oracle:thin:@" + getIPOracle() + ":21521:xe";
        dataSource.setUrl(sUrl);
        dataSource.setUsername(getsUser());
        dataSource.setPassword(getsPass());
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        return new JdbcTemplate(dataSource);
    }

    /**
     * Database clean up before start
     */
    public void dbCleanup(JdbcTemplate jdbcTemplate, String[] tables) {
        this.jdbcTemplate = jdbcTemplate;
        String truncates = "begin\n" +
                "for cur in (Select * FROM user_tables fk WHERE fk.TABLE_NAME " + tables + ")\n" +
                "    loop\n" +
                "        EXECUTE IMMEDIATE 'TRUNCATE TABLE '|| cur.table_name;\n" +
                "    end loop;\n" +
                "end;";
        this.jdbcTemplate.execute(truncates);
    }

}
