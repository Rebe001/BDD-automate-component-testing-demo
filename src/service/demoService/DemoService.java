package src.service.demoService;;

import src.resources.jms.JmsMessageRequest;
import src.resources.jms.JmsMessageResponse;

public class DemoService {

    private String configuration;
    private JmsMessageRequest lastJmsMessageRequest;
    private JmsMessageResponse lastJmsMessageResponse;
    private Integer lastHttpStatusCode;

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public JmsMessageResponse getLastJmsMessageResponse() {
        return lastJmsMessageResponse;
    }

    public void setLastJmsMessageResponse(JmsMessageResponse lastJmsMessageResponse) {
        this.lastJmsMessageResponse = lastJmsMessageResponse;
    }

    public Integer getLastHttpStatusCode() {return lastHttpStatusCode;}

    public void setLastHttpStatusCode(Integer lastHttpStatusCode) {
        this.lastHttpStatusCode = lastHttpStatusCode;
    }


}
