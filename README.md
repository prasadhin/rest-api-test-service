### rest-api-test-service
#### Scenario and Features
- This code is a simplified micro service that contains functionality to save blocks of arbitrary bank data to a persistence store. 
- Data is stored in-memory H2 database.
- Provision to add, delete and update is the block is exposed using Rest end Points.
- Links for the Api is given below.
- Code coverage is available for the main code, but for support code like clients to test the service is left out.
- TechTestApplication.class contains the driver class and client for checking the API calls, can be used to start the application manually if necessary.
- MVC mock tests are provides for the asserting the Controller and API.
- Junit tests are provided for all the code except the client as mentioned.

### Technologies

* Lanaguage: Java 8
* Framework: Spring Boot
* Build Tool - Gradle

### Build the app and run tests
./gradlew build

### Running the code

./gradlew bootRun

### API EndPoints
* Get: http://localhost:8090/dataserver/data/BLOCKTYPEA
* Post: http://localhost:8090/dataserver/pushdata
* Update: http://localhost:8090/dataserver/update/{name}/{newBlockType}

### Sample Tests
curl -X GET -H "Accept: application/json" http://localhost:8090/dataserver/data/BLOCKTYPEB
