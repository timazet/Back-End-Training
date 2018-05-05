package com.timazet.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

}
