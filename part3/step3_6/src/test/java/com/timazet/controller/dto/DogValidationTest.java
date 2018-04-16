package com.timazet.controller.dto;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DogValidationTest {

    private static final String VALID_NAME = "dog name";
    private static final LocalDate VALID_BIRTH_DATE = LocalDate.now();
    private static final int VALID_HEIGHT = 35;
    private static final int VALID_WEIGHT = 20;
    private static final String INVALID_NAME_MESSAGE = "size must be between 1 and 100";
    private static final String INVALID_BIRTH_DATE_MESSAGE = "must be a date in the past or in the present";
    private static final String INVALID_HEIGHT_MESSAGE = "must be greater than 0";
    private static final String INVALID_WIDTH_MESSAGE = "must be greater than 0";
    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldFindViolatedNameWhenMoreThan100Symbols() {
        String validName = RandomStringUtils.randomAlphabetic(100);
        assertThat(validator.validate(new Dog(null, validName, VALID_BIRTH_DATE, VALID_HEIGHT, VALID_WEIGHT)), empty());

        String invalidName = RandomStringUtils.randomAlphabetic(101);
        Set<ConstraintViolation<Dog>> violations = validator.validate(
                new Dog(null, invalidName, VALID_BIRTH_DATE, VALID_HEIGHT, VALID_WEIGHT));
        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), equalTo(INVALID_NAME_MESSAGE));
    }

    @Test
    public void shouldFindViolatedNameWhenLessThan1Symbol() {
        String validName = RandomStringUtils.randomAlphabetic(1);
        assertThat(validator.validate(new Dog(null, validName, VALID_BIRTH_DATE, VALID_HEIGHT, VALID_WEIGHT)), empty());

        String invalidName = RandomStringUtils.randomAlphabetic(0);
        Set<ConstraintViolation<Dog>> violations = validator.validate(
                new Dog(null, invalidName, VALID_BIRTH_DATE, VALID_HEIGHT, VALID_WEIGHT));
        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), equalTo(INVALID_NAME_MESSAGE));
    }

    @Test
    public void shouldFindViolatedBirthDateWhenDateInTheFuture() {
        LocalDate validBirthDate = LocalDate.now();
        assertThat(validator.validate(new Dog(null, VALID_NAME, validBirthDate, VALID_HEIGHT, VALID_WEIGHT)), empty());

        LocalDate invalidDateBirth = LocalDate.now().plusDays(1);
        Set<ConstraintViolation<Dog>> violations = validator.validate(
                new Dog(null, VALID_NAME, invalidDateBirth, VALID_HEIGHT, VALID_WEIGHT));
        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), equalTo(INVALID_BIRTH_DATE_MESSAGE));
    }

    @Test
    public void shouldFindViolatedHeightWhenNotPositive() {
        int validHeight = 1;
        assertThat(validator.validate(new Dog(null, VALID_NAME, VALID_BIRTH_DATE, validHeight, VALID_WEIGHT)), empty());

        int invalidHeight = 0;
        Set<ConstraintViolation<Dog>> violations = validator.validate(
                new Dog(null, VALID_NAME, VALID_BIRTH_DATE, invalidHeight, VALID_WEIGHT));
        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getMessage(), equalTo(INVALID_HEIGHT_MESSAGE));
    }

    @Test
    public void shouldFindAllViolatedFields() {
        Set<ConstraintViolation<Dog>> violations = validator.validate(new Dog(null, "", LocalDate.now().plusDays(1), 0, 0));
        assertThat(violations, hasSize(4));
        assertThat(violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()),
                containsInAnyOrder(INVALID_NAME_MESSAGE, INVALID_BIRTH_DATE_MESSAGE,
                        INVALID_HEIGHT_MESSAGE, INVALID_WIDTH_MESSAGE));
    }

}
