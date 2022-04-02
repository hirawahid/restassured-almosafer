package test;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import java.io.File;

import org.json.simple.parser.ParseException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import junit.framework.Assert;

public class test_hotels {
	@DataProvider(name = "sanity")
	public Object[] get_payload_sanity() {
	baseURI="https://www.almosafer.com";
	Response response= given().header("token","skdjfh73273$7268u2j89s")
			.basePath("/api/enigma/hotel/lookup")
	.when()
	.get()
	.then().extract().response();
	return new Object[] {response};
	}
	@Test(dataProvider ="sanity" )
	public void hotels_status_code_validation(Response result) throws ParseException {
		Assert.assertEquals(result.statusCode(),200);
		
	 } 
	@Test(dataProvider ="sanity" )
	public void hotels_response_time_validation(Response result) throws ParseException {
		long time= result.time();
		assertThat((int)time,lessThan(5000));
		
	 }
	@Test(dataProvider ="sanity" )
	public void hotels_content_type_header_validation(Response result) throws ParseException {
		//System.out.println(result.header("Content-Type"));
		Assert.assertEquals(result.header("Content-Type"),"application/json");
	 } 
	@Test(dataProvider ="sanity" )
	public void hotels_response_not_null_validation(Response result) throws ParseException {
		assertThat(result.asByteArray(),not(nullValue()));
	 }
	@Test
	public void hotels_json_schema_validation() throws ParseException {
		baseURI="https://www.almosafer.com";
		given().header("token","skdjfh73273$7268u2j89s")
		.basePath("/api/enigma/hotel/lookup")
		.when()
		.get()
		.then()
		.assertThat()
		.body(JsonSchemaValidator.
			      matchesJsonSchema(new File(getClass().getResource("schema_hotels.json").getFile())));
		
	 }
	@Test
	public void hotels_validate_post_not_successful() throws ParseException {
		baseURI="https://www.almosafer.com";
		given().header("token","skdjfh73273$7268u2j89s")
		.basePath("/api/enigma/hotel/lookup")
		.when()
		.post()
		.then().statusCode(405);
		
	 }
	
	@Test
	public void hotels_validate_response_without_token() throws ParseException {
		baseURI="https://www.almosafer.com";
		given()
		.basePath("/api/enigma/hotel/lookup")
		.when()
		.post()
		.then().statusCode(401);
		
	 }
	
	@Test
	public void hotels_validate_with_invalid_token() throws ParseException {
		baseURI="https://www.almosafer.com";
		given().header("token","skdjfh73273$72j89s")
		.basePath("/api/enigma/hotel/lookup")
		.when()
		.post()
		.then().statusCode(401);
		
	 }
	
	@Test
	public void hotels_validate_invalid_url() throws ParseException {
		baseURI="https://www.almosafer.com";
		given().header("token","skdjfh73273$7268u2j89s")
		.basePath("/api/enigma/hotel/lo0okup")
		.when()
		.post()
		.then().statusCode(404);
		
	 }
	

}
