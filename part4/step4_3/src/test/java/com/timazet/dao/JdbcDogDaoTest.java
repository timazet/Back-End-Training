package com.timazet.dao;

import com.timazet.controller.dto.Dog;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.ThrowableAssert;
import org.h2.jdbc.JdbcSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ActiveProfiles("oracle")
@Sql(scripts = {"classpath:db/drop_all.sql", "classpath:db/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ContextConfiguration("classpath:persistence-context.xml")
public class JdbcDogDaoTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private DogDao dogDao;

    @Test
    public void shouldThrowExceptionOnNameBiggerThan100Symbols() {
        assertThatThrowExceptionOnFieldConstraintViolation(dog -> dog.setName(RandomStringUtils.randomAlphanumeric(101)));
    }

    @Test
    public void shouldThrowExceptionOnNullName() {
        assertThatThrowExceptionOnFieldConstraintViolation(dog -> dog.setName(null));
    }

    @Test
    public void shouldNotThrowExceptionOnNullBirthDate() {
        assertThatNoExceptionOnFieldConstraintViolation(dog -> dog.setBirthDate(null));
    }

    @Test
    public void shouldAllowSQLInjections() {
        Dog dog = generateValidDog();
        String name = dog.getName();

        String sqlInjectionUsingName = "SQL Injection', null, -1, -1) --";
        dog.setName(sqlInjectionUsingName);

        Dog created = dogDao.get(dogDao.create(dog).getId());
        assertThat(created.getName()).isNotEqualTo(name).isEqualTo("SQL Injection");
        assertThat(created.getBirthDate()).isNotEqualTo(dog.getBirthDate()).isNull();
        assertThat(created.getHeight()).isNotEqualTo(dog.getHeight()).isEqualTo(-1);
        assertThat(created.getWeight()).isNotEqualTo(dog.getWeight()).isEqualTo(-1);
    }

    @Test
    public void shouldInsertAndUpdateDogWithoutConstraints() {
        Dog dog = generateValidDog();
        Dog created = dogDao.get(dogDao.create(dog).getId());
        assertThatEquals(created, dog);

        dog = generateValidDog(created.getId());
        Dog updated = dogDao.get(dogDao.update(dog).getId());
        assertThatEquals(updated, dog);
    }

    private void assertThatThrowExceptionOnFieldConstraintViolation(final ConstraintViolationAction<Dog> action) {
        Dog toCreate = action.applyAndReturn(generateValidDog());
        assertThatJdbcSQLExceptionIsCause(() -> dogDao.create(toCreate));

        Dog toUpdate = action.applyAndReturn(dogDao.create(generateValidDog()));
        assertThatJdbcSQLExceptionIsCause(() -> dogDao.update(toUpdate));
    }

    private void assertThatNoExceptionOnFieldConstraintViolation(final ConstraintViolationAction<Dog> action) {
        Dog toCreate = action.applyAndReturn(generateValidDog());
        assertThatNoException(() -> dogDao.create(toCreate));

        Dog toUpdate = action.applyAndReturn(dogDao.create(generateValidDog()));
        assertThatNoException(() -> dogDao.update(toUpdate));
    }

    private void assertThatJdbcSQLExceptionIsCause(final ThrowableAssert.ThrowingCallable callable) {
        assertThatCode(callable).hasCauseExactlyInstanceOf(JdbcSQLException.class);
    }

    private void assertThatNoException(final ThrowableAssert.ThrowingCallable callable) {
        assertThatCode(callable).doesNotThrowAnyException();
    }

    private static void assertThatEquals(final Dog actual, final Dog expected) {
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getBirthDate()).isEqualTo(expected.getBirthDate());
        assertThat(actual.getHeight()).isEqualTo(expected.getHeight());
        assertThat(actual.getWeight()).isEqualTo(expected.getWeight());
    }

    private static Dog generateValidDog() {
        return generateValidDog(UUID.randomUUID());
    }

    private static Dog generateValidDog(final UUID id) {
        return new Dog(id, RandomStringUtils.randomAlphanumeric(100), LocalDate.now(),
                RandomUtils.nextInt(0, 60), RandomUtils.nextInt(0, 60));
    }

    @FunctionalInterface
    public interface ConstraintViolationAction<T> {

        void apply(T object);

        default T applyAndReturn(T object) {
            apply(object);
            return object;
        }

    }

}
