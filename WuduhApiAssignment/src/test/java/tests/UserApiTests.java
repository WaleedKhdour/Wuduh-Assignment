package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserApiTests {

    private static final String BASE_URL = "https://reqres.in/api";

    @Test
    public void validateGetUsers() {
        // Set the base URI
        RestAssured.baseURI = BASE_URL;

        // Perform GET request
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/users")
                .then()
                .assertThat()
                // Validate Status Code
                .statusCode(200)
                // Validate Response Time (less than 2 seconds)
                .time(lessThan(2000L))
                // Validate JSON Schema
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/user_schema.json"))
                .extract()
                .response();

        // Extract response time dynamically
        long responseTime = response.getTime();
        System.out.println("Response Time: " + responseTime + " ms");

        // Validate the number of users in the response
        int numberOfUsers = response.jsonPath().getList("data").size();
        assertThat("Number of users should be greater than 0", numberOfUsers, greaterThan(0));

        // Additional Dynamic Validations
        assertThat("Response contains 'data'", response.jsonPath().get("data"), is(notNullValue()));
        assertThat("Response contains 'support.url'", response.jsonPath().getString("support.url"), startsWith("https://"));
    }

    @Test
    public void validateSingleUserResponse() {
        // Set the base URI
        RestAssured.baseURI = BASE_URL;

        // Perform GET request
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/users/2")
                .then()
                .assertThat()
                // Validate Status Code
                .statusCode(200)
                // Validate Response Time (less than 2 seconds)
                .time(lessThan(2000L))
                // Validate JSON Schema
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/single_user_schema.json"))
                .extract()
                .response();

        // Log response time
        long responseTime = response.getTime();
        System.out.println("Response Time: " + responseTime + " ms");

        // Validate the response body dynamically
        int id = response.jsonPath().getInt("data.id");
        String email = response.jsonPath().getString("data.email");
        String firstName = response.jsonPath().getString("data.first_name");
        String lastName = response.jsonPath().getString("data.last_name");
        String avatar = response.jsonPath().getString("data.avatar");

        // Validate the user details
        assertThat("User ID is valid", id, equalTo(2));
        assertThat("User email is valid", email, equalTo("janet.weaver@reqres.in"));
        assertThat("User first name is not null", firstName, equalTo("Janet"));
        assertThat("User last name is not null", lastName, equalTo("Weaver"));
        assertThat("User avatar URL starts with 'https://'", avatar, equalTo("https://reqres.in/img/faces/2-image.jpg"));

        // Validate support details
        String supportUrl = response.jsonPath().getString("support.url");
        String supportText = response.jsonPath().getString("support.text");
        assertThat("Support URL is valid", supportUrl, startsWith("https://"));
        assertThat("Support text is not null", supportText, notNullValue());
    }

}

