Description
LandRoute API is a Spring Boot application that finds the shortest land route between two countries using the BFS (Breadth-First Search) algorithm.
 Prerequisites
    • Java JDK 21 
    • Apache Maven 3.8+
 Import The project
	Import the project from Eclipse (as maven project)
Compile the project
Run as mvn clean
Run as mvn install
lanch application
Run as Spring Boot App

The application is accessible at: http://localhost:8080
Verify the application is running with the browser
http://localhost:8080/health
Expected response:
{"status":"OK","message":"Country search service operational"}
Test the routing API
Example: Find the route between Czech Republic and Italy
http://localhost:8080/routing/CZE/ITA
Expected response:
{"route":["CZE","AUT","ITA"]}
If there is no land crossing, the endpoint returns HTTP 400
In the browser, press F12 to see the error.
