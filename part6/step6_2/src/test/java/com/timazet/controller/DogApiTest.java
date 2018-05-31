package com.timazet.controller;

import com.timazet.controller.dto.Dog;
import com.timazet.controller.dto.ErrorResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsNot;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static com.timazet.controller.DogController.DOG;
import static com.timazet.controller.DogController.DOGS;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.isEmptyString;

public class DogApiTest {

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.defaultParser = Parser.JSON;
    }

    @Test
    public void shouldReturnNotAcceptableForIncorrectAcceptHeader() {
        Dog dog = generateDog();

        assertThatNotAcceptable(given().accept(ContentType.TEXT).get(DOGS));

        assertThatNotAcceptable(given().accept(ContentType.TEXT).get(DOG, dog.getId()));

        assertThatNotAcceptable(given().body(dog).accept(ContentType.TEXT).contentType(ContentType.JSON).when().post(DOGS));

        assertThatNotAcceptable(given().body(dog).accept(ContentType.TEXT).contentType(ContentType.JSON).when().put(DOG, dog.getId()));
    }

    @Test
    public void shouldReturnBadRequestForIncorrectDogId() {
        String incorrectUUID = "incorrectUUID";

        assertThatBadRequestAndContainsIdInResponse(executeGetDog(incorrectUUID), incorrectUUID);

        assertThatBadRequestAndContainsIdInResponse(executeUpdateDog(generateDog(), incorrectUUID), incorrectUUID);

        assertThatBadRequestAndContainsIdInResponse(executeDeleteDog(incorrectUUID), incorrectUUID);
    }

    @Test
    public void shouldReturnBadRequestForViolatedDog() {
        Dog invalidDog = new Dog(UUID.randomUUID(), RandomStringUtils.randomAlphabetic(101), LocalDate.now().plusDays(1),
                0, 0);

        assertThatBadRequest(executeCreateDog(invalidDog));

        assertThatBadRequest(executeUpdateDog(invalidDog, invalidDog.getId()));
    }


    @Test
    public void shouldReturnNotFoundForNonexistentDog() {
        UUID randomUUID = UUID.randomUUID();

        assertThatNotFoundAndContainsIdInResponse(executeGetDog(randomUUID), randomUUID);

        assertThatNotFoundAndContainsIdInResponse(executeUpdateDog(generateDog(), randomUUID), randomUUID);

        assertThatNotFoundAndContainsIdInResponse(executeDeleteDog(randomUUID), randomUUID);
    }

    @Test
    public void shouldReturnDogs() {
        executeGetDogs().then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(4));
    }

    @Test
    public void shouldReturnDog() {
        Dog dog = executeGetDogs().jsonPath().getObject("[1]", Dog.class);
        assertThatResponseIsRequiredDog(executeGetDog(dog.getId()), dog);
    }

    @Test
    public void shouldCreateDog() {
        Dog dog = generateDog();

        Response response = executeCreateDog(dog);
        response.then().assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON);
        assertThatMatches(response.body().as(Dog.class), matchesAllFieldsExceptId(dog), IsNot.not(matchesId(dog.getId())));

        Dog createdAgain = createDog(dog);
        assertThatResponseIsRequiredDog(executeGetDog(createdAgain.getId()), createdAgain);
    }

    @Test
    public void shouldUpdateDog() {
        Dog created = createDog(generateDog());

        UUID id = created.getId();
        created.setId(UUID.randomUUID());
        created.setName(created.getName() + " - updated");

        Response response = executeUpdateDog(created, id);
        assertThatResponseIsRequiredDog(response, matchesAllFieldsExceptId(created), matchesId(id),
                IsNot.not(matchesId(created.getId())));

        assertThatResponseIsRequiredDog(executeGetDog(id), created, id);
    }

    @Test
    public void deleteDog() {
        Dog created = createDog(generateDog());

        executeDeleteDog(created.getId()).then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(isEmptyString())
                .content(isEmptyString());

        assertThatNotFoundAndContainsIdInResponse(executeDeleteDog(created.getId()), created.getId());
    }

    private static void assertThatNotAcceptable(final Response response) {
        response.then().assertThat().statusCode(HttpStatus.NOT_ACCEPTABLE.value());
    }

    private static void assertThatBadRequest(final Response response) {
        response.then().assertThat().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private static void assertThatBadRequestAndContainsIdInResponse(final Response response, final Object id) {
        assertThatRequiredStatusAndContainsIdInResponse(response, HttpStatus.BAD_REQUEST, id);
    }

    private static void assertThatNotFoundAndContainsIdInResponse(final Response response, final Object id) {
        assertThatRequiredStatusAndContainsIdInResponse(response, HttpStatus.NOT_FOUND, id);
    }

    private static void assertThatRequiredStatusAndContainsIdInResponse(final Response response,
                                                                        final HttpStatus status, final Object id) {
        response.then().assertThat()
                .statusCode(status.value())
                .contentType(ContentType.JSON);
        assertThat(response.body().as(ErrorResponse.class).getMessage(), containsString(id.toString()));
    }

    private static void assertThatResponseIsRequiredDog(final Response response, final Dog dog) {
        assertThatResponseIsRequiredDog(response, dog, dog.getId());
    }

    private static void assertThatResponseIsRequiredDog(final Response response, final Dog dog, final UUID id) {
        assertThatResponseIsRequiredDog(response, matchesId(id), matchesAllFieldsExceptId(dog));
    }

    @SafeVarargs
    private static void assertThatResponseIsRequiredDog(final Response response, final Matcher<Dog>... matchers) {
        response.then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);

        assertThatMatches(response.body().as(Dog.class), matchers);
    }

    @SafeVarargs
    private static void assertThatMatches(final Dog actual, final Matcher<Dog>... matchers) {
        if (ArrayUtils.isNotEmpty(matchers)) {
            Arrays.stream(matchers).forEach(matcher -> assertThat(actual, matcher));
        }
    }

    private static CustomMatcher<Dog> matchesId(final Object expectedId) {
        return new CustomMatcher<Dog>("Matches id") {
            @Override
            public boolean matches(Object item) {
                Dog actual = (Dog) item;
                return Objects.equals(actual.getId(), expectedId);
            }
        };
    }

    private static CustomMatcher<Dog> matchesAllFieldsExceptId(final Dog expected) {
        return new CustomMatcher<Dog>("Matches all fields except id") {
            @Override
            public boolean matches(Object item) {
                Dog actual = (Dog) item;
                return Objects.equals(actual.getName(), expected.getName())
                        && Objects.equals(actual.getBirthDate(), expected.getBirthDate())
                        && Objects.equals(actual.getHeight(), expected.getHeight())
                        && Objects.equals(actual.getWeight(), expected.getWeight());
            }
        };
    }

    private static Response executeGetDogs() {
        return given().accept(ContentType.JSON).when().get(DOGS);
    }

    private static Response executeGetDog(Object id) {
        return given().accept(ContentType.JSON).when().get(DOG, id);
    }

    private static Response executeCreateDog(Dog dog) {
        return given().body(dog).contentType(ContentType.JSON).when().post(DOGS);
    }

    private static Response executeUpdateDog(Dog dog, Object id) {
        return given().body(dog).contentType(ContentType.JSON).when().put(DOG, id);
    }

    private static Response executeDeleteDog(Object id) {
        return when().delete(DOG, id);
    }

    private static Dog createDog(Dog dog) {
        return given().body(dog).contentType(ContentType.JSON).when().post(DOGS).body().as(Dog.class);
    }

    private static Dog generateDog() {
        Random random = new Random();
        return new Dog(UUID.randomUUID(), RandomStringUtils.randomAlphanumeric(25),
                LocalDate.of(2000 + random.nextInt(5), random.nextInt(12) + 1,
                        random.nextInt(28) + 1), random.nextInt(60) + 1, random.nextInt(30) + 1);
    }

}
