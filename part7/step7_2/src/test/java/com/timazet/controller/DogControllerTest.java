package com.timazet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timazet.controller.dto.Dog;
import com.timazet.controller.dto.ErrorResponse;
import org.hamcrest.CustomMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static com.timazet.controller.DogController.DOG;
import static com.timazet.controller.DogController.DOGS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hibernate")
@ContextConfiguration({"classpath:application-context.xml", "classpath:web-context.xml"})
@WebAppConfiguration
public class DogControllerTest extends AbstractTestNGSpringContextTests {

    private Dog jerryLee = new Dog(UUID.randomUUID(), "Jerry Lee", LocalDate.of(1989, 10, 18),
            60, 32);
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeMethod
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup((WebApplicationContext) applicationContext).build();
        objectMapper = Jackson2ObjectMapperBuilder.json().build();
    }

    @Test
    public void shouldReturnNotAcceptableForIncorrectAcceptHeader() throws Exception {
        assertThatNotAcceptable(mockMvc.perform(get(DOGS).accept(MediaType.TEXT_PLAIN)));

        assertThatNotAcceptable(mockMvc.perform(get(DOG, jerryLee.getId()).accept(MediaType.TEXT_PLAIN)));

        assertThatNotAcceptable(mockMvc.perform(post(DOGS).accept(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(jerryLee)).contentType(MediaType.APPLICATION_JSON_UTF8)));

        assertThatNotAcceptable(mockMvc.perform(put(DOG, jerryLee.getId()).accept(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(jerryLee)).contentType(MediaType.APPLICATION_JSON_UTF8)));
    }

    @Test
    public void shouldReturnBadRequestForIncorrectDogId() throws Exception {
        String incorrectUUID = "incorrectUUID";

        assertThatBadRequestAndContainsIdInResponse(mockMvc.perform(get(DOG, incorrectUUID)), incorrectUUID);

        assertThatBadRequestAndContainsIdInResponse(mockMvc.perform(put(DOG, incorrectUUID)
                .content(objectMapper.writeValueAsString(jerryLee)).contentType(MediaType.APPLICATION_JSON_UTF8)), incorrectUUID);

        assertThatBadRequestAndContainsIdInResponse(mockMvc.perform(delete(DOG, incorrectUUID)), incorrectUUID);
    }


    @Test
    public void shouldReturnNotFoundForNonexistentDog() throws Exception {
        UUID randomUUID = UUID.randomUUID();

        assertThatNotFoundAndContainsIdInResponse(mockMvc.perform(get(DOG, randomUUID)), randomUUID);

        assertThatNotFoundAndContainsIdInResponse(mockMvc.perform(put(DOG, randomUUID)
                .content(objectMapper.writeValueAsString(jerryLee)).contentType(MediaType.APPLICATION_JSON_UTF8)), randomUUID);

        assertThatNotFoundAndContainsIdInResponse(mockMvc.perform(delete(DOG, randomUUID)), randomUUID);
    }

    @Test
    public void shouldCreateDog() throws Exception {
        mockMvc.perform(post(DOGS).content(objectMapper.writeValueAsString(jerryLee))
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isBadRequest())
                .andDo(result -> assertThat(getResult(result, ErrorResponse.class).getMessage(), containsString("not supported")));

        mockMvc.perform(post(DOGS).accept(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(jerryLee))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(result -> assertThat(getResult(result, Dog.class), new CustomMatcher<Dog>("Dog matcher") {
                    @Override
                    public boolean matches(Object item) {
                        Dog result = (Dog) item;
                        return !Objects.equals(result.getId(), jerryLee.getId()) && Objects.equals(result.getName(), jerryLee.getName())
                                && Objects.equals(result.getBirthDate(), jerryLee.getBirthDate())
                                && Objects.equals(result.getHeight(), jerryLee.getHeight())
                                && Objects.equals(result.getWeight(), jerryLee.getWeight());
                    }
                }));

        Dog createdAgain = getResult(mockMvc.perform(post(DOGS).content(objectMapper.writeValueAsString(jerryLee))
                .contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn(), Dog.class);
        assertThatResponseIsRequiredDog(mockMvc.perform(get(DOG, createdAgain.getId()).accept(MediaType.APPLICATION_JSON_UTF8)), createdAgain);
    }

    @Test
    public void shouldUpdateDog() throws Exception {
        Dog created = getResult(mockMvc.perform(post(DOGS).content(objectMapper.writeValueAsString(jerryLee))
                .contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn(), Dog.class);

        UUID id = created.getId();
        created.setId(UUID.randomUUID());
        created.setName(created.getName() + " - updated");

        ResultActions actions = mockMvc.perform(put(DOG, id).accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(created)).contentType(MediaType.APPLICATION_JSON_UTF8));
        assertThatResponseIsRequiredDog(actions, created, id)
                .andDo(result -> assertThat(getResult(result, Dog.class), new CustomMatcher<Dog>("Dog matcher") {
                    @Override
                    public boolean matches(Object item) {
                        Dog result = (Dog) item;
                        return !Objects.equals(result.getId(), created.getId()) && !Objects.equals(result.getName(), jerryLee.getName());
                    }
                }));

        assertThatResponseIsRequiredDog(mockMvc.perform(get(DOG, id).accept(MediaType.APPLICATION_JSON_UTF8)), created, id);
    }

    @Test
    public void deleteDog() throws Exception {
        Dog created = getResult(mockMvc.perform(post(DOGS).content(objectMapper.writeValueAsString(jerryLee))
                .contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn(), Dog.class);

        mockMvc.perform(delete(DOG, created.getId()))
                .andExpect(status().isOk());

        assertThatNotFoundAndContainsIdInResponse(mockMvc.perform(delete(DOG, created.getId())), created.getId());
    }

    private ResultActions assertThatNotAcceptable(ResultActions actions) throws Exception {
        return actions.andExpect(status().isNotAcceptable());
    }

    private ResultActions assertThatBadRequestAndContainsIdInResponse(ResultActions actions, Object id) throws Exception {
        return assertThatRequiredStatusAndContainsIdInResponse(actions, HttpStatus.BAD_REQUEST, id);
    }

    private ResultActions assertThatNotFoundAndContainsIdInResponse(ResultActions actions, Object id) throws Exception {
        return assertThatRequiredStatusAndContainsIdInResponse(actions, HttpStatus.NOT_FOUND, id);
    }

    private ResultActions assertThatRequiredStatusAndContainsIdInResponse(ResultActions actions, HttpStatus status, Object id) throws Exception {
        return actions
                .andDo(result -> logger.info(result.getResponse().getContentAsString()))
                .andExpect(status().is(status.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(result -> assertThat(getResult(result, ErrorResponse.class).getMessage(), containsString(id.toString())));
    }

    private ResultActions assertThatResponseIsRequiredDog(final ResultActions actions, final Dog dog) throws Exception {
        return assertThatResponseIsRequiredDog(actions, dog, dog.getId());
    }

    private ResultActions assertThatResponseIsRequiredDog(final ResultActions actions, final Dog dog, final UUID id) throws Exception {
        return actions
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(result -> assertThat(getResult(result, Dog.class), new CustomMatcher<Dog>("Dog matcher") {
                    @Override
                    public boolean matches(Object item) {
                        Dog result = (Dog) item;
                        return Objects.equals(result.getId(), id) && Objects.equals(result.getName(), dog.getName())
                                && Objects.equals(result.getBirthDate(), dog.getBirthDate())
                                && Objects.equals(result.getHeight(), dog.getHeight())
                                && Objects.equals(result.getWeight(), dog.getWeight());
                    }
                }));

    }

    private <T> T getResult(final MvcResult result, final Class<T> clazz) throws java.io.IOException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), clazz);
    }

}
