# restassured-almosafer
In order to run cases, please run testng.xml as testng suite..
Reports will be generated at restassured/test-output/index.html
# Project Structure
There are two packages in src/test/java as following:
 1: demo : flights class is implemented to construct body of POST flight endpoint.
 2: test: It implents two classes for testing flights POST and hotels GET endpoint.
# Test Class flights_test:
It implements test cases for flights POST endpoint as https://www.almosafer.com/api/v3/flights/flight/get-fares-calender. It has 29 cases. One is failling as known issue.

# Test Class test_hotels:
This class tests GET endpoint for hotel module as https://www.almosafer.com/api/enigma/hotel/lookup. It has 9 cases are all are passing.

Kindly check report at restassured/test-output/index.html for last execution's result.

Thanks,
Hira
