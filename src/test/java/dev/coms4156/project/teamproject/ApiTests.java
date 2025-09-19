package dev.coms4156.project.teamproject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.coms4156.project.teamproject.model.AccountProfile;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * API tests for all endpoints of all controllers.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiTests {
  private static int client1Id;
  private static int client2Id;
  private static int client1ProviderId;
  private static int client1RecipientId;
  private static int client2ProviderId;
  private static int client2RecipientId;
  private static int client1Listing1Id;
  private static int client1Listing2Id;
  private static int client2Listing1Id;
  private static int client2Listing2Id;
  private static int client1RequestId;
  private static int client2Request1Id;
  private static int client2Request2Id;
  private static int client2Request3Id;

  @Test
  @Order(1)
  public void createClients() {
    // Create client 1
    RequestSpecification spec = new RequestSpecBuilder()
                                    .setBaseUri("http://34.48.131.233:8080/api/clientProfiles")
                                    .build();
    Response response = given().spec(spec).when().post("/create")
                        .then().assertThat().statusCode(201).extract().response();
    JsonPath responseBody = response.jsonPath();
    client1Id = responseBody.getInt("clientId");

    // Create client 2
    Response response2 = given().spec(spec).when().post("/create")
        .then().assertThat().statusCode(201).extract().response();
    JsonPath responseBody2 = response2.jsonPath();
    client2Id = responseBody2.getInt("clientId");
  }

  @Test
  @Order(2)
  public void createAccountsForClient1() {
    // Create provider for client 1
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/accountProfiles")
        .build();

    Response response = given().spec(spec)
        .queryParam("clientId", client1Id)
        .queryParam("accountType", AccountProfile.AccountType.PROVIDER)
        .queryParam("phoneNumber", "1234567890")
        .queryParam("name", "vi")
        .when().post("/create")
        .then().assertThat().statusCode(201).extract().response();
    JsonPath responseBody = response.jsonPath();
    client1ProviderId = responseBody.getInt("accountId");

    // Create recipient for client 1
    Response response2 = given().spec(spec)
        .queryParam("clientId", client1Id)
        .queryParam("accountType", AccountProfile.AccountType.RECIPIENT)
        .queryParam("phoneNumber", "0987654321")
        .queryParam("name", "cait")
        .when().post("/create")
        .then().assertThat().statusCode(201).extract().response();
    JsonPath responseBody2 = response2.jsonPath();
    client1RecipientId = responseBody2.getInt("accountId");
  }

  @Test
  @Order(3)
  public void createAccountsForClient2() {
    // Create provider for client 2
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/accountProfiles")
        .build();

    Response response = given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("accountType", AccountProfile.AccountType.PROVIDER)
        .queryParam("phoneNumber", "1234567890")
        .queryParam("name", "einstein")
        .when().post("/create")
        .then().assertThat().statusCode(201).extract().response();
    JsonPath responseBody = response.jsonPath();
    client2ProviderId = responseBody.getInt("accountId");

    // Create recipient for client 2

    Response response2 = given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("accountType", AccountProfile.AccountType.RECIPIENT)
        .queryParam("phoneNumber", "0987654321")
        .queryParam("name", "physics")
        .when().post("/create")
        .then().assertThat().statusCode(201).extract().response();
    JsonPath responseBody2 = response2.jsonPath();
    client2RecipientId = responseBody2.getInt("accountId");
  }

  @Test
  @Order(4)
  public void createListingsForClient1() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();

    Response response = given().spec(spec)
        .queryParam("clientId", client1Id)
        .queryParam("accountId", client1ProviderId)
        .queryParam("foodType", "peach")
        .queryParam("quantityListed", 333)
        .queryParam("latitude", 34.052f)
        .queryParam("longitude", -118.243f)
        .when().post("/createFoodListing")
        .then().assertThat().statusCode(201).extract().response();
    String responseBody = response.getBody().asString();
    String idSubstring = responseBody.substring(responseBody.lastIndexOf(":") + 2);
    client1Listing1Id = Integer.parseInt(idSubstring.trim());

    Response response2 = given().spec(spec)
        .queryParam("clientId", client1Id)
        .queryParam("accountId", client1ProviderId)
        .queryParam("foodType", "noms")
        .queryParam("quantityListed", 3)
        .queryParam("latitude", 78.122f)
        .queryParam("longitude", 120.281f)
        .when().post("/createFoodListing")
        .then().assertThat().statusCode(201).extract().response();
    String responseBody2 = response2.getBody().asString();
    String idSubstring2 = responseBody2.substring(responseBody2.lastIndexOf(":") + 2);
    client1Listing2Id = Integer.parseInt(idSubstring2.trim());
  }

  @Test
  @Order(5)
  public void createListingsForClient2() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();

    Response response = given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("accountId", client2ProviderId)
        .queryParam("foodType", "sundubu jjigae")
        .queryParam("quantityListed", 111)
        .queryParam("latitude", 34.052f)
        .queryParam("longitude", -118.243f)
        .when().post("/createFoodListing")
        .then().assertThat().statusCode(201).extract().response();
    String responseBody = response.getBody().asString();
    String idSubstring = responseBody.substring(responseBody.lastIndexOf(":") + 2);
    client2Listing1Id = Integer.parseInt(idSubstring.trim());

    Response response2 = given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("accountId", client2ProviderId)
        .queryParam("foodType", "moo")
        .queryParam("quantityListed", 1)
        .queryParam("latitude", 33.989f)
        .queryParam("longitude", -118.243f)
        .when().post("/createFoodListing")
        .then().assertThat().statusCode(201).extract().response();
    String responseBody2 = response2.getBody().asString();
    String idSubstring2 = responseBody2.substring(responseBody2.lastIndexOf(":") + 2);
    client2Listing2Id = Integer.parseInt(idSubstring2.trim());
  }

  @Test
  public void getClientTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/clientProfiles")
        .build();
    Response response = given().spec(spec)
        .queryParam("clientId", client1Id)
        .when().get("/get")
        .then().assertThat().statusCode(200).extract().response();
    JsonPath responseBody = response.jsonPath();
    assertEquals(client1Id, responseBody.getInt("client_id"));

    Response response2 = given().spec(spec)
        .queryParam("clientId", client2Id)
        .when().get("/get")
        .then().assertThat().statusCode(200).extract().response();
    JsonPath responseBody2 = response2.jsonPath();
    assertEquals(client2Id, responseBody2.getInt("client_id"));
  }

  @Test
  public void getClientNotFoundTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/clientProfiles")
        .build();
    Response response = given().spec(spec)
        .queryParam("clientId", -1)
        .when().get("/get")
        .then().assertThat().statusCode(404).extract().response();
    JsonPath responseBody = response.jsonPath();
    assertEquals("Client ID not found.", responseBody.getString("error"));
  }

  @Test
  public void getAccountTestClient1() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/accountProfiles")
        .build();
    Response response = given().spec(spec)
        .queryParam("accountId", client1ProviderId)
        .when().get("/get")
        .then().assertThat().statusCode(200).extract().response();
    JsonPath responseBody = response.jsonPath();
    assertEquals(client1ProviderId, responseBody.getInt("account_id"));

    Response response2 = given().spec(spec)
        .queryParam("accountId", client1RecipientId)
        .when().get("/get")
        .then().assertThat().statusCode(200).extract().response();
    JsonPath responseBody2 = response2.jsonPath();
    assertEquals(client1RecipientId, responseBody2.getInt("account_id"));
  }

  @Test
  public void getAccountTestClient2() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/accountProfiles")
        .build();
    Response response = given().spec(spec)
        .queryParam("accountId", client2ProviderId)
        .when().get("/get")
        .then().assertThat().statusCode(200).extract().response();
    JsonPath responseBody = response.jsonPath();
    assertEquals(client2ProviderId, responseBody.getInt("account_id"));

    Response response2 = given().spec(spec)
        .queryParam("accountId", client2RecipientId)
        .when().get("/get")
        .then().assertThat().statusCode(200).extract().response();
    JsonPath responseBody2 = response2.jsonPath();
    assertEquals(client2RecipientId, responseBody2.getInt("account_id"));
  }

  @Test
  public void getAccountNotFoundTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/accountProfiles")
        .build();
    Response response = given().spec(spec)
        .queryParam("accountId", -1)
        .when().get("/get")
        .then().assertThat().statusCode(404).extract().response();
    JsonPath responseBody = response.jsonPath();
    assertEquals("Account ID not found.", responseBody.getString("error"));
  }

  @Test
  public void getFoodListingsTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();
    given().spec(spec)
        .queryParam("clientId", client1Id)
        .when().get("/getFoodListings")
        .then().assertThat().statusCode(200)
        .body("size()", greaterThan(0))
        .body("[0].listingId", anyOf(equalTo(client1Listing1Id), equalTo(client1Listing2Id)))
        .body("[0].foodType", anyOf(equalTo("peach"), equalTo("noms")))
        .body("[1].listingId", anyOf(equalTo(client1Listing1Id), equalTo(client1Listing2Id)))
        .body("[1].foodType", anyOf(equalTo("peach"), equalTo("noms")));
  }

  @Test
  public void getFoodListingsTest2() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();
    given().spec(spec)
        .queryParam("clientId", client2Id)
        .when().get("/getFoodListings")
        .then().assertThat().statusCode(200)
        .body("size()", greaterThan(0))
        .body("[0].listingId", anyOf(equalTo(client2Listing1Id), equalTo(client2Listing2Id)))
        .body("[0].foodType", anyOf(equalTo("sundubu jjigae"), equalTo("moo")))
        .body("[1].listingId", anyOf(equalTo(client2Listing1Id), equalTo(client2Listing2Id)))
        .body("[1].foodType", anyOf(equalTo("sundubu jjigae"), equalTo("moo")));
  }

  @Test
  public void getNearbyListingsNoneFoundTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();
    given().spec(spec)
        .queryParam("clientId", client1Id)
        .queryParam("latitude", 0f)
        .queryParam("longitude", 0.1231f)
        .when().get("/getNearbyListings")
        .then().assertThat()
        .statusCode(404);
  }

  @Test
  public void getNearbyListingsOneFoundTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();
    // listings 1 and 2 are far apart, should only find listing 1
    // when querying from listing 1's location
    given().spec(spec)
        .queryParam("clientId", client1Id)
        .queryParam("latitude", 34.052f)
        .queryParam("longitude", -118.243f)
        .when().get("/getNearbyListings")
        .then().assertThat()
        .statusCode(200)
        .body("size()", equalTo(1))
        .body("[0].listingId", equalTo(client1Listing1Id));

    given().spec(spec)
        .queryParam("clientId", client1Id)
        .queryParam("latitude", 78.122f)
        .queryParam("longitude", 120.281f)
        .when().get("/getNearbyListings")
        .then().assertThat()
        .body("size()", equalTo(1))
        .body("[0].listingId", equalTo(client1Listing2Id));
  }

  @Test
  @Order(7)
  public void getNearbyListingsMultipleFoundTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();
    // in client 2, listings 1 and 2 are within 10 km/miles/whatever of each other
    given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("latitude", 34.052f)
        .queryParam("longitude", -118.243f)
        .queryParam("maxDistance", 10)
        .when().get("/getNearbyListings")
        .then().assertThat()
        .statusCode(200)
        .body("size()", equalTo(2))
        .body("[0].listingId", anyOf(equalTo(client2Listing1Id), equalTo(client2Listing2Id)))
        .body("[1].listingId", anyOf(equalTo(client2Listing1Id), equalTo(client2Listing2Id)));
  }

  @Test
  public void getFoodListingsUnderAccountTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();
    given().spec(spec)
        .queryParam("clientId", client1Id)
        .queryParam("accountId", client1ProviderId)
        .when().get("/getFoodListingsUnderAccount")
        .then().assertThat()
        .statusCode(200)
        .body("size()", equalTo(2))
        .body("[0].listingId", anyOf(equalTo(client1Listing1Id), equalTo(client1Listing2Id)))
        .body("[1].listingId", anyOf(equalTo(client1Listing1Id), equalTo(client1Listing2Id)));

    // should fine none under recipient account
    given().spec(spec)
        .queryParam("clientId", client1Id)
        .queryParam("accountId", client1RecipientId)
        .when().get("/getFoodListingsUnderAccount")
        .then().assertThat()
        .statusCode(404);
  }

  @Test
  @Order(6)
  public void createRequestClient1Test() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/foodRequests")
        .build();
    Response response = given().spec(spec)
        .queryParam("clientId", client1Id)
        .queryParam("accountId", client1RecipientId)
        .queryParam("listingId", client1Listing1Id)
        .queryParam("quantityRequested", 10)
        .when().post("/create")
        .then().assertThat().statusCode(201).extract().response();
    JsonPath responseBody = response.jsonPath();
    client1RequestId = responseBody.getInt("requestId");
  }

  @Test
  @Order(6)
  public void createRequestClient2Test() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/foodRequests")
        .build();
    Response response = given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("accountId", client2RecipientId)
        .queryParam("listingId", client2Listing1Id)
        .queryParam("quantityRequested", 11)
        .when().post("/create")
        .then().assertThat().statusCode(201).extract().response();
    JsonPath responseBody = response.jsonPath();
    client2Request1Id = responseBody.getInt("requestId");

    Response response2 = given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("accountId", client2RecipientId)
        .queryParam("listingId", client2Listing2Id)
        .queryParam("quantityRequested", 1)
        .when().post("/create")
        .then().assertThat().statusCode(201).extract().response();
    JsonPath responseBody2 = response2.jsonPath();
    client2Request2Id = responseBody2.getInt("requestId");

    Response response3 = given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("accountId", client2RecipientId)
        .queryParam("listingId", client2Listing1Id)
        .queryParam("quantityRequested", 4)
        .when().post("/create")
        .then().assertThat().statusCode(201).extract().response();
    JsonPath responseBody3 = response3.jsonPath();
    client2Request3Id = responseBody3.getInt("requestId");
  }

  @Test
  public void getRequestTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/foodRequests")
        .build();
    Response response = given().spec(spec)
        .queryParam("requestId", client1RequestId)
        .when().get("/get")
        .then().assertThat().statusCode(200).extract().response();
    JsonPath responseBody = response.jsonPath();
    int requestId = responseBody.getInt("request_id");
    assertEquals(client1RequestId, requestId);
  }

  @Test
  public void getRequestNotFoundTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/foodRequests")
        .build();
    given().spec(spec)
        .queryParam("requestId", -1)
        .when().get("/get")
        .then().assertThat().statusCode(404);
  }

  @Test
  public void updateRequestTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080/api/foodRequests")
        .build();
    given()
        .spec(spec)
        .queryParam("requestId", client2Request1Id)
        .queryParam("quantityRequested", 127)
        .when()
        .put("/update")
        .then()
        .statusCode(200)
        .body("message", equalTo("Updated Successfully."));

    Response response = given().spec(spec)
        .queryParam("requestId", client2Request1Id)
        .when().get("/get").then().statusCode(200)
        .extract().response();
    JsonPath responseBody = response.jsonPath();
    int qtyReq = responseBody.getInt("quantity_requested");
    assertEquals(127, qtyReq);
  }

  @Test
  public void getRequestsForListingClient1Test() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();
    given().spec(spec)
        .queryParam("clientId", client1Id)
        .queryParam("accountId", client1ProviderId)
        .queryParam("listingId", client1Listing1Id)
        .when().get("/getRequestsForListing")
        .then().assertThat()
        .statusCode(200)
        .body("size()", equalTo(1))
        .body("[0].requestId", equalTo(client1RequestId));
  }

  @Test
  public void getRequestsForListingClient2Test() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();
    given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("accountId", client2ProviderId)
        .queryParam("listingId", client2Listing1Id)
        .when().get("/getRequestsForListing")
        .then().assertThat()
        .statusCode(200)
        .body("size()", equalTo(2))
        .body("[0].requestId", anyOf(equalTo(client2Request1Id), equalTo(client2Request3Id)))
        .body("[1].requestId", anyOf(equalTo(client2Request1Id), equalTo(client2Request3Id)));

    given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("accountId", client2ProviderId)
        .queryParam("listingId", client2Listing2Id)
        .when().get("/getRequestsForListing")
        .then().assertThat()
        .statusCode(200)
        .body("size()", equalTo(1))
        .body("[0].requestId", equalTo(client2Request2Id));
  }

  @Test
  public void fulfillRequestTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();
    given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("listingId", client2Listing1Id)
        .queryParam("quantityRequested", 2)
        .when().patch("/fulfillRequest")
        .then().assertThat()
        .statusCode(200)
        .body("message", equalTo("Updated Successfully."));
  }

  @Test
  public void updateFoodListingTest() {
    RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("http://34.48.131.233:8080")
        .build();
    given().spec(spec)
        .queryParam("clientId", client2Id)
        .queryParam("accountId", client2ProviderId)
        .queryParam("listingId", client2Listing1Id)
        .queryParam("newFoodType", "coconuts")
        .queryParam("newLatitude", 17.668f)
        .when().patch("/updateFoodListing")
        .then().assertThat()
        .statusCode(200)
        .body("message", equalTo("Updated Successfully."));
  }
}
