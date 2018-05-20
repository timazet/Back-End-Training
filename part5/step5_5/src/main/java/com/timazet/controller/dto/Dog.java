package com.timazet.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public class Dog {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String BIRTH_DATE = "birth_date";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";

    private UUID id;
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    /**
     * We should specify serializer and deserializer or set up the jackson mapper to use JavaTimeModule, because
     * it's new formats, which are not included into defaults
     */
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @PastOrPresent
    private LocalDate birthDate;
    @NotNull
    @Positive
    private int height;
    @NotNull
    @Positive
    private int weight;

    public Dog() {

    }

    public Dog(final UUID id, final String name, final LocalDate birthDate, final int height, final int weight) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.height = height;
        this.weight = weight;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setBirthDate(final LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public void setWeight(final int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Dog(id=" + this.getId() + ", name=" + this.getName() + ", birthDate=" + this.getBirthDate() + ", height=" + this.getHeight() + ", weight=" + this.getWeight() + ")";
    }

}
