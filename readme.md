# BDD - Automate UI Component Demo
This project shows a simple component test of Demo Service from a microservices framework. Code are modified from my [work project](https://rebecca-li-portfolio.imfast.io/)

<br>
This is a simplied architecture diagram based on my work project. It shows how front end connects with back end, production site and SAP in a higher level:
<br>
![alt text](https://github.com/Rebe001/BDD-automate-component-testing-demo/blob/master/images/project_archiecture.jpg?raw=true)) <!-- .element width="50%" -->




## Table of contents
* [Flow diagram of Demo Service](#flow-diagram-of-demo-service)
* [Design test case](#design-test-case)
* [Built with](#built-with)
* [Directory tree structure](#directory-tree-structure)
* [Author](#author)

## Flow diagram of Demo Service

This is a simplied flow diagram of Demo Service in component level. It demonstrates how Demo Service interacts with other mock services.
<br> 
![alt text](https://github.com/Rebe001/BDD-automate-component-testing-demo/blob/master/images/demo_service_flow_diagram.png?raw=true) <!-- .element width="50%" -->




## Design test case

Test case [Demo_test.feature](/features/component/Demo_test.feature) is shown under path `/features/component/Demo_test.feature`.

Automated component test is created to verify if data delivery by a component service - Demo Service is completed. Here shows a step by step series of steps that explains the design of test case using Behaviour Driver Development (BDD) as the framework.

This step makes use of `@Given` Cucumber annotation, which describes pre-requisite for the test to be executed. The aim of this step is to check whether the connection from framework to `Demo Service` is established.

```
Given Connection with "DemoService" is checked
```
This step makes use of `@When` Cucumber annotation, which defines the trigger point for any test scenario execution. It sets a desired `<configuration>` parameter that will be later use in other step.

```
When DemoService set configuration "<configuration>"
```
This step makes use of `@And` Cucumber annotation, which provides the logical AND condition between any two statements. It produces a JMS message to a specified queue from `Mock Service` to `Demo Service`.
```
And DemoService produce JMS message by MockService
```
This step makes use of sql scripts to extract data from table. It selects column `Stage` from a specified table and verify whether it has value `Prepared`.
```
And DemoService check stage is "PREPARED"
```
This step used REST `POST` method to create new subordinate resources. Request URI `/demo-service/render-output` and request json body is called.
```
And DemoService POST render output file
```
This step verify whether response is responded with HTTP `200` success code. `Demo Service` is parameterised to follow open-closde principle, so it can easily extended to other service.
```
And HTTP status code for "DemoService" is "200"
```
This step consumes JMS message that sent by `Demo Service` to queue and uses by other `Mock Services`. Message is stored to in `DemoSerive.java` in verification step.
```
And DemoService consume JMS message by MockService
```
This last step makes use of `@Then` Cucumber annotation, holds the expected result for the test to be executed. JMS message that stored in previous step will be verified with expected values.
```
Then Verify DemoService JMS message
```
`Sceanrio Outline` keyword can be used to run the same Scenario multiple times, with different combinations of values. `Èxamples` is container for different values.

```
Examples:
| configuration     |
| configuration2    |
| configuration3  	|
```

In short, this test case simulates - sending a JMS message to queue by mock services to Demo Service and verifies if Demo Service proceed and produces another JMS message to return to MockServices. The test verifies a JMS message with expected status code and body is consumed in an expected queue.



## Built with
 
* Test execuetion Framework: [BDD](https://cucumber.io/docs/bdd/)
* BDD language: [Cucumber](https://cucumber.io/docs/guides/overview/)
* [Java Programming](https://java.com/en/download/faq/whatis_java.xml)
* Java Framework: [MVC](https://www.oracle.com/technical-resources/articles/javase/mvc.html)
* Framework: [Microservices](https://spring.io/blog/2015/07/14/microservices-with-spring)
* Unit testing framework: [Junit](https://junit.org/junit4/)
* [Rest Api](https://www.restapitutorial.com/), [JMS](https://www.oracle.com/java/technologies/java-message-service.html)
* Database: [Oracle sql](https://www.oracle.com/database/technologies/appdev/sqldeveloper-landing.html)

	




## Directory tree structure
```bash
	└─ Bdd automate component testing demo/
		├─ features/
		|	└─ component/
		|		└─ Demo_test.feature
		├─src/
		|	├─data/
		|	|	└─ProjectConstants.java
		|	├─library/
		|	|	├─_common/
		|	|	|	├─Common_library.java
		|	|	|	└─CommonObjects.java
		|	|	└─DemoService_Library.java
		|	|	
		|	├─resources/
		|	|	└─jms/
		|	|		└─schema/
		|	|			├─JmsMessageRequest.json
		|	|			└JmsMessageResponse.json
		|	└─service/
		|		├─_common/
		|		|	└BaseClient.java
		|		└─demoService/
		|			├─DemoService.java
		|			└DemoServiceController.java
		|	
		└─readme.md
``` 
   

## Author

* Author: Rebecca
* Date: 10/06/2020