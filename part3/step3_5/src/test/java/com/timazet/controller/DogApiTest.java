package com.timazet.controller;

import com.timazet.controller.dto.Dog;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class DogApiTest {

    private static final String URL = "http://localhost:8080";
    private static final String DOGS = URL + DogController.DOGS;
    private static final String DOG = URL + DogController.DOG;

    private Dog jerryLee = new Dog(UUID.randomUUID(), "Jerry Lee", LocalDate.of(1989, 10, 18),
            60, 32);

    @Test
    public void getDogs() {
        given().accept(ContentType.TEXT).get(DOGS).then().assertThat()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value());

        given().accept(ContentType.JSON).when().get(DOGS).then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", hasSize(greaterThanOrEqualTo(4)));
    }

    @Test
    public void getDog() {
        Dog dog = when().get(DOGS).jsonPath().getObject("[1]", Dog.class);

        String incorrectUUID = "incorrectUUID";
        when().get(DOG, incorrectUUID).then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("message", containsString(incorrectUUID));

        UUID randomUUID = UUID.randomUUID();
        when().get(DOG, randomUUID).then().assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("message", containsString(randomUUID.toString()));

        given().accept(ContentType.TEXT).get(DOG, dog.getId()).then().assertThat()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value());

        ValidatableResponse response = given().accept(ContentType.JSON).when().get(DOG, dog.getId()).then().assertThat();
        checkCommon(response, dog);
    }

    @Test
    public void createDog() {
        given().body(jerryLee).contentType(ContentType.TEXT).when().post(DOGS).then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", containsString("not supported"));

        given().body(jerryLee).accept(ContentType.TEXT).contentType(ContentType.JSON).when().post(DOGS).then().assertThat()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value());

        given().body(jerryLee).accept(ContentType.JSON).contentType(ContentType.JSON).when().post(DOGS).then().assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("id", not(equalTo(jerryLee.getId().toString())))
                .body("name", equalTo(jerryLee.getName()))
                .body("birthDate", equalTo(jerryLee.getBirthDate().toString()))
                .body("height", equalTo(jerryLee.getHeight()))
                .body("weight", equalTo(jerryLee.getWeight()));

        Dog createdAgain = given().body(jerryLee).accept(ContentType.JSON).contentType(ContentType.JSON)
                .when().post(DOGS).body().as(Dog.class);
        ValidatableResponse response = given().accept(ContentType.JSON).when().get(DOG, createdAgain.getId()).then().assertThat();
        checkCommon(response, createdAgain);
    }

    @Test
    public void updateDog() {
        Dog created = given().body(jerryLee).contentType(ContentType.JSON).when().post(DOGS).body().as(Dog.class);

        String incorrectUUID = "incorrectUUID";
        given().body(jerryLee).contentType(ContentType.JSON).when().put(DOG, incorrectUUID).then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("message", containsString(incorrectUUID));

        UUID randomUUID = UUID.randomUUID();
        given().body(jerryLee).contentType(ContentType.JSON).when().put(DOG, randomUUID).then().assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("message", containsString(randomUUID.toString()));

        UUID id = created.getId();
        created.setId(UUID.randomUUID());
        created.setName(created.getName() + " - updated");
        given().body(created).accept(ContentType.TEXT).contentType(ContentType.JSON).when().put(DOG, id).then().assertThat()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value());

        ValidatableResponse response = given().body(created).accept(ContentType.JSON).contentType(ContentType.JSON)
                .when().put(DOG, id).then().assertThat();
        checkCommon(response, created, id.toString())
                .body("id", not(equalTo(created.getId().toString())))
                .body("name", not(equalTo(jerryLee.getName())));

        response = given().accept(ContentType.JSON).when().get(DOG, id).then().assertThat();
        checkCommon(response, created, id.toString());
    }

    @Test
    public void deleteDog() {
        Dog created = given().body(jerryLee).contentType(ContentType.JSON).when().post(DOGS).body().as(Dog.class);

        String incorrectUUID = "incorrectUUID";
        when().delete(DOG, incorrectUUID).then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("message", containsString(incorrectUUID));

        UUID randomUUID = UUID.randomUUID();
        when().delete(DOG, randomUUID).then().assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("message", containsString(randomUUID.toString()));

        when().delete(DOG, created.getId()).then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(isEmptyString())
                .content(isEmptyString());

        when().delete(DOG, created.getId()).then().assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("message", containsString(created.getId().toString()));
    }

    private static ValidatableResponse checkCommon(final ValidatableResponse response, final Dog dog) {
        return checkCommon(response, dog, dog.getId().toString());
    }

    private static ValidatableResponse checkCommon(final ValidatableResponse response, final Dog dog, final String id) {
        return response.statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(id))
                .body("name", equalTo(dog.getName()))
                .body("birthDate", equalTo(dog.getBirthDate().toString()))
                .body("height", equalTo(dog.getHeight()))
                .body("weight", equalTo(dog.getWeight()));
    }

}
