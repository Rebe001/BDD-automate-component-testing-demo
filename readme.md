# BDD - Automate UI Component Demo
This project shows a simple component test of Demo Service from a microservices framework. Test case verify if data delivery by a component service - Demo Service is completed. For more details about test case please go to [Design test case](#design-test-case). Code are modified from my work project.


Here is a simplied architecture in a component level, which demonstrates how Demo Service should interact with other mock services: 
![alt text](https://github.com/Rebo001/BDD-automate-component-testing-demo/doc/SimplifiedDemoArchitecture.png?raw=true)



Here is a simple architecture in a high-level.
![alt text](https://github.com/Rebo001/BDD-automate-component-testing-demo/doc/project_archiecture.jpg?raw=true)


[More details about project](https://rebecca-li-portfolio.imfast.io/)



## Table of contents
* [Technologies](#technologies)
* [Design test case](#design-test-case)
* [Project infrastructure](#project-infrastructure)





## Technologies
Project is created with - 
* Test execuetion Framework: Behaviour Driver Development (BDD)
* BDD language: Cucumber
* Java Programming
* Java Framework: MVC
* Framework: Microservices
* Unit testing framework: Junit
* Rest Api, JMS
* Database: Oracle sql

	



## Design test case

Test case is shown in path /features/component/Demo_test.feature

It simulates - sending a JMS message to queue by mock services to Demo Service and verifies if Demo Service proceed and produces another JMS message to return to MockServices. The test verifies a JMS message with expected status code and body is consumed in an expected queue.




## Project infrastructure
	Bdd automate component testing demo/
		features/
			component/
				Demo_test.feature
		src/
			data/
				ProjectConstants.java
			library/
				_common/
					Common_library.java
					CommonObjects.java
				DemoService_Library.java
				pages/
					DemoPage_Library.java
					LoginPage_Library.java
					MainPage_Library.java
			resources/
				jms/
					schema/
						JmsMessageRequest.json
						JmsMessageResponse.json
			service/
				_common/
					BaseClient.java
				demoService/
					DemoService.java
					DemoServiceController.java
			
		read.me
   
   

   
* Contributor: Rebecca
* Date: 10/06/2020