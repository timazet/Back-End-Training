package com.timazet.controller;

import com.timazet.controller.dto.Dog;
import com.timazet.controller.dto.ErrorResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.CustomMatcher;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static com.timazet.controller.DogController.DOG;
import static com.timazet.controller.DogController.DOGS;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DogApiTest {

    private Dog jerryLee = new Dog(UUID.randomUUID(), "Jerry Lee", LocalDate.of(1989, 10, 18),
            60, 32);

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void shouldReturnNotAcceptableForIncorrectAcceptHeader() {
        assertThatNotAcceptable(given().accept(ContentType.TEXT).get(DOGS));

        assertThatNotAcceptable(given().accept(ContentType.TEXT).get(DOG, jerryLee.getId()));

        assertThatNotAcceptable(given().body(jerryLee).accept(ContentType.TEXT).contentType(ContentType.JSON).when().post(DOGS));

        assertThatNotAcceptable(given().body(jerryLee).accept(ContentType.TEXT).contentType(ContentType.JSON).when().put(DOG, jerryLee.getId()));
    }

    @Test
    public void shouldReturnBadRequestForIncorrectDogId() {
        String incorrectUUID = "incorrectUUID";

        assertThatBadRequestAndContainsIdInResponse(when().get(DOG, incorrectUUID), incorrectUUID);

        assertThatBadRequestAndContainsIdInResponse(
                given().body(jerryLee).contentType(ContentType.JSON).when().put(DOG, incorrectUUID), incorrectUUID);

        assertThatBadRequestAndContainsIdInResponse(when().delete(DOG, incorrectUUID), incorrectUUID);
    }

    @Test
    public void shouldReturnBadRequestForViolatedDog() {
        Dog invalidDog = new Dog(UUID.randomUUID(), RandomStringUtils.randomAlphabetic(101), LocalDate.now().plusDays(1),
                0, 0);

        assertThatBadRequest(given().body(invalidDog).contentType(ContentType.JSON).when().post(DOGS));

        assertThatBadRequest(given().body(invalidDog).contentType(ContentType.JSON).when().put(DOG, invalidDog.getId()));
    }


    @Test
    public void shouldReturnNotFoundForNonexistentDog() {
        UUID randomUUID = UUID.randomUUID();

        assertThatNotFoundAndContainsIdInResponse(when().get(DOG, randomUUID), randomUUID);

        assertThatNotFoundAndContainsIdInResponse(given().body(jerryLee).contentType(ContentType.JSON).when().put(DOG, randomUUID), randomUUID);

        assertThatNotFoundAndContainsIdInResponse(when().delete(DOG, randomUUID), randomUUID);
    }

    @Test
    public void shouldReturnDogs() {
        given().accept(ContentType.JSON).when().get(DOGS).then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(4));
    }

    @Test
    public void shouldReturnDog() {
        Dog dog = when().get(DOGS).jsonPath().getObject("[1]", Dog.class);
        assertThatResponseIsRequiredDog(given().accept(ContentType.JSON).when().get(DOG, dog.getId()), dog);
    }

    @Test
    public void shouldCreateDog() {
        Response response = given().body(jerryLee).contentType(ContentType.TEXT).when().post(DOGS);
        response.then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getMessage(), containsString("not supported"));

        response = given().body(jerryLee).accept(ContentType.JSON).contentType(ContentType.JSON).when().post(DOGS);
        response.then().assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON);
        assertThat(response.body().as(Dog.class), new CustomMatcher<Dog>("Dog matcher") {
            @Override
            public boolean matches(Object item) {
                Dog result = (Dog) item;
                return !Objects.equals(result.getId(), jerryLee.getId()) && Objects.equals(result.getName(), jerryLee.getName())
                        && Objects.equals(result.getBirthDate(), jerryLee.getBirthDate())
                        && Objects.equals(result.getHeight(), jerryLee.getHeight())
                        && Objects.equals(result.getWeight(), jerryLee.getWeight());
            }
        });

        Dog createdAgain = given().body(jerryLee).accept(ContentType.JSON).contentType(ContentType.JSON)
                .when().post(DOGS).body().as(Dog.class);
        assertThatResponseIsRequiredDog(given().accept(ContentType.JSON).when().get(DOG, createdAgain.getId()), createdAgain);
    }

    @Test
    public void shouldUpdateDog() {
        Dog created = given().body(jerryLee).contentType(ContentType.JSON).when().post(DOGS).body().as(Dog.class);

        UUID id = created.getId();
        created.setId(UUID.randomUUID());
        created.setName(created.getName() + " - updated");

        Response response = given().body(created).accept(ContentType.JSON).contentType(ContentType.JSON).when().put(DOG, id);
        assertThatResponseIsRequiredDog(response, created, id);
        assertThat(response.body().as(Dog.class), new CustomMatcher<Dog>("Dog matcher") {
            @Override
            public boolean matches(Object item) {
                Dog result = (Dog) item;
                return !Objects.equals(result.getId(), created.getId()) && !Objects.equals(result.getName(), jerryLee.getName());
            }
        });

        assertThatResponseIsRequiredDog(given().accept(ContentType.JSON).when().get(DOG, id), created, id);
    }

    @Test
    public void deleteDog() {
        Dog created = given().body(jerryLee).contentType(ContentType.JSON).when().post(DOGS).body().as(Dog.class);

        when().delete(DOG, created.getId()).then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(isEmptyString())
                .content(isEmptyString());

        assertThatNotFoundAndContainsIdInResponse(when().delete(DOG, created.getId()), created.getId());
    }

    private static void assertThatNotAcceptable(Response response) {
        response.then().assertThat()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value());
    }

    private static void assertThatBadRequest(Response response) {
        response.then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private static void assertThatBadRequestAndContainsIdInResponse(Response response, Object id) {
        assertThatRequiredStatusAndContainsIdInResponse(response, HttpStatus.BAD_REQUEST, id);
    }

    private static void assertThatNotFoundAndContainsIdInResponse(Response response, Object id) {
        assertThatRequiredStatusAndContainsIdInResponse(response, HttpStatus.NOT_FOUND, id);
    }

    private static void assertThatRequiredStatusAndContainsIdInResponse(Response response, HttpStatus status, Object id) {
        response.then().assertThat()
                .statusCode(status.value())
                .contentType(ContentType.JSON);
        assertThat(response.body().as(ErrorResponse.class).getMessage(), containsString(id.toString()));
    }

    private static void assertThatResponseIsRequiredDog(final Response response, final Dog dog) {
        assertThatResponseIsRequiredDog(response, dog, dog.getId());
    }

    private static void assertThatResponseIsRequiredDog(final Response response, final Dog dog, final UUID id) {
        response.then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);
        assertThat(response.body().as(Dog.class), new CustomMatcher<Dog>("Dog matcher") {
            @Override
            public boolean matches(Object item) {
                Dog result = (Dog) item;
                return Objects.equals(result.getId(), id) && Objects.equals(result.getName(), dog.getName())
                        && Objects.equals(result.getBirthDate(), dog.getBirthDate())
                        && Objects.equals(result.getHeight(), dog.getHeight())
                        && Objects.equals(result.getWeight(), dog.getWeight());
            }
        });
    }

}
