package com.timazet.model;

import lombok.*;

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
    private String name;
    private LocalDate birthDate;
    private int height;
    private int weight;

}
