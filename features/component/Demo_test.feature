Feature: Demo-test
  Created by : Rebecca

  Verify MockService initiates DemoService by producing JMS message to queue 
  and after proceed DemoService produces another JMS message to queue back to MockService. Happy Path

  Objetive : Verify Response Data Delivery after Processing Completion

  Precondition : Demo Service is active
  PostCondition : 
  Automated : true
  Type : Functional

  Scenario Outline: IDSP-Demo-test
    Given Connection with "DemoService" is checked
    When DemoService set configuration "<configuration>"
    And DemoService produce JMS message by MockService
    And DemoService check stage is "PREPARED"
    And DemoService POST render output file
    And HTTP status code for "DemoService" is "200"
    And DemoService consume JMS message by MockService
    Then Verify DemoService JMS message


    Examples:
    | configuration     |
    | configuration2    |
    | configuration3  	|




