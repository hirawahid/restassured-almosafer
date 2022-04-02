package test;
import demo.flights;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import java.io.File;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import junit.framework.Assert;

import java.time.LocalDate;
import java.util.ArrayList;
public class flights_test {
	
	@DataProvider(name = "sanity")
	public Object[] get_payload_sanity() {
		LocalDate dep_to = LocalDate.now();
		LocalDate dep_from=LocalDate.now().plusDays(4);
		//request body JSON
		flights fl=new flights("RUH","JED",dep_to.toString(),dep_from.toString(),"Economy",1,0,0);
		JSONObject res=new JSONObject();
		res=fl.rest_body();
		baseURI="https://www.almosafer.com";
		Response response= given().header("Content-Type","application/json;charset=UTF-8")
				.basePath("/api/v3/flights/flight/get-fares-calender")
		.body(res)
		.when()
		.post()
		.then().extract().response();
		return new Object[] {response};
	}
	
	@DataProvider(name = "schema")
	public Object[] get_data() {
		//provide response for schema validation
		LocalDate dep_to = LocalDate.now();
		LocalDate dep_from=LocalDate.now().plusDays(4);
		flights fl=new flights("RUH","JED",dep_to.toString(),dep_from.toString(),"Economy",1,0,0);
		JSONObject res=new JSONObject();
		res=fl.rest_body();
		return new Object[] {res.toJSONString()};
	}
	
	@Test(dataProvider="schema")
	public void flights_json_schema_validation(String result) throws ParseException {
		//validate response with schema
		baseURI="https://www.almosafer.com";
		given().header("Content-Type","application/json;charset=UTF-8")
		.basePath("/api/v3/flights/flight/get-fares-calender")
		.body(result)
		.when()
		.post()
		.then()
		.assertThat()
		.body(JsonSchemaValidator.
			      matchesJsonSchema(new File(getClass().getResource("schema_flights.json").getFile())));
		} 
	
	@Test(dataProvider ="sanity" )
	public void flights_status_code_validation(Response result) throws ParseException {
		Assert.assertEquals(result.statusCode(),200);
		
	 } 
	
	@Test(dataProvider ="sanity" )
	public void flights_response_time_validation(Response result) throws ParseException {
		long time= result.time();
		assertThat((int)time,lessThan(5000));
		} 
	
	@Test(dataProvider ="sanity" )
	public void flights_verify_date_format_response(Response result) throws ParseException {
		
		//parsing response into JSON		
		JSONParser parser = new JSONParser();  
		JSONObject json = (JSONObject) parser.parse(result.asString()); 
		
		//looking for node-names
		for (Object key : json.keySet()) {
			
	        //based on you key types
	        String keyStr = (String)key;
	        
	       //Checking dates as node-names in json to be in date format
	        Assert.assertEquals(keyStr.toString().matches("^\\d{4}-\\d{2}-\\d{2}$"), true);

	    }
	}
	
	public ArrayList<String> get_inner_json_values(JSONObject json, String k) throws ParseException {
		
		//helper function to extract next level json properties from response
		ArrayList<String> result = new ArrayList<String>();
		for (Object key : json.keySet()) {
	        String keyStr = (String)key;
	        Object keyvalue = json.get(keyStr);
	        JSONParser parser = new JSONParser();
	        JSONObject innerjson = (JSONObject) parser.parse(keyvalue.toString());
	        result.add(innerjson.get(k).toString()); 

	    }
		return result;
	}
	
	@Test(dataProvider ="sanity" )
	public void flights_verify_price_format_response(Response result) throws ParseException {
		
		//System.out.println(result.getBody().asPrettyString());
		JSONParser parser = new JSONParser();  
		JSONObject json = (JSONObject) parser.parse(result.asString()); 
		ArrayList <String> prices= new ArrayList<String>();
		prices = get_inner_json_values(json,"price"); 
		for (int i = 0; i < prices.size(); i++) {
			
			//verify that prices are in correct format, either all digits or float
		    	  boolean n=prices.get(i).matches("[+-]?([0-9]*[.])?[0-9]+");
		    	  Assert.assertEquals(n, true);
		    }
		
	}
	
	@Test(dataProvider ="sanity" )
	public void flights_verify_updatedAt_format_response(Response result) throws ParseException {
		
		//System.out.println(result.getBody().asPrettyString());
		JSONParser parser = new JSONParser();  
		JSONObject json = (JSONObject) parser.parse(result.asString()); 
		ArrayList <String> updated_at= new ArrayList<String>();
		updated_at = get_inner_json_values(json,"updatedAt"); 
		for (int i = 0; i < updated_at.size(); i++) {
				 //System.out.println(prices.get(i));
			
			//checking updated_At to be in valid DATETIME format
		    	  boolean n=updated_at.get(i).matches("^(20)\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d.\\d\\d\\dZ$");
		    	  Assert.assertEquals(n, true);
		    }
		
	}

	@Test(dataProvider ="sanity" )
	public void flights_content_type_header_validation(Response result) throws ParseException {
		
		//System.out.println(result.header("Content-Type"));
		Assert.assertEquals(result.header("Content-Type"),"application/json");
	 } 
	
	@Test(dataProvider ="sanity" )
	public void flights_response_not_null_validation(Response result) throws ParseException {
		assertThat(result.asByteArray(),not(nullValue()));
	 } 
	
	@DataProvider(name = "negative-departureFrom")
	public Object[] get_negative_departureFrom() {
		return new Object[] {"2022-0s-34","2022-0","1987-04-34"," "};
	}

	@Test(dataProvider ="negative-departureFrom" )
	public void flights_invalid_departureFrom_validation(String date) throws ParseException {
		
		//this test case will check invalid combinations of departureFrom date
		LocalDate dep_to=LocalDate.now().plusDays(4);
		flights fl=new flights("RUH","JED",date,dep_to.toString(),"Economy",1,0,0);
		JSONObject res=new JSONObject();
		res=fl.rest_body();
		baseURI="https://www.almosafer.com";
		Response response= given().header("Content-Type","application/json;charset=UTF-8")
				.basePath("/api/v3/flights/flight/get-fares-calender")
		.body(res)
		.when()
		.post()
		.then().extract().response();
		//response.prettyPrint();
		Assert.assertEquals((int)response.statusCode(),400);
		JsonPath jp=response.jsonPath();
		
		//checking for approriate error messages in response
		String[] er_messages=jp.get("detail").toString().split("=")[1].replace("[","").replace("]", "").split(",");
		Assert.assertEquals(er_messages[0].contains("departureFrom is not a valid date"), true);
		Assert.assertEquals(er_messages[1].contains("departureFrom does not match the format Y-m-d."), true);
		LocalDate current_date = LocalDate.now();
		
		//System.out.println(current_date);
		//System.out.println(er_messages[2]);
		
		//due to time-zone difference current date can be different
		er_messages[2].equals(anyOf(is("departureFrom must be a date after or equal to "+current_date.toString()+"."),
				is("departureFrom must be a date after or equal to "+current_date.plusDays(-1).toString()+".")));
		//Assert.assertEquals(er_messages[2].contains("departureFrom must be a date after or equal to "+current_date.toString()+"."), true);
		
		/* I think last test case must pass as departureFrom should never be a blank string(but I see its being controlled through UI)
		 * FAILED: invalid_departureFrom_validation(" ")
		junit.framework.AssertionFailedError: expected:<200> but was:<400>*/  
	 }
	
	@DataProvider(name = "negative-OriginID")
	public Object[] get_negative_negative_OriginID() {
		return new Object[] {"ruh","2022","RUH#","abcd "};
	}
	
	@Test(dataProvider ="negative-OriginID" )
	public void flights_invalid_originID_validation(String origin) throws ParseException {
		
		//this testcase will check invalid combinations of originID
		LocalDate current_date=LocalDate.now();
		LocalDate dep_to=current_date.plusDays(4);
		flights fl=new flights(origin,"JED",current_date.toString(),dep_to.toString(),"Economy",1,0,0);
		JSONObject res=new JSONObject();
		res=fl.rest_body();
		baseURI="https://www.almosafer.com";
		Response response= given().header("Content-Type","application/json;charset=UTF-8")
				.basePath("/api/v3/flights/flight/get-fares-calender")
		.body(res)
		.when()
		.post()
		.then().extract().response();
		Assert.assertEquals((int)response.statusCode(),200);
		
		//response should be empty
		Assert.assertEquals(response.getBody().asString(),"[]");
		
		  
	 }


}
