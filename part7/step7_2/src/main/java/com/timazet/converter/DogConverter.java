package com.timazet.converter;

import com.timazet.controller.dto.Dog;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DogConverter {

    public static Dog convert(com.timazet.model.Dog dog) {
        return new Dog(dog.getId(), dog.getName(), dog.getBirthDate(), dog.getHeight(), dog.getWeight());
    }

    public static com.timazet.model.Dog convert(Dog dog) {
        return new com.timazet.model.Dog(dog.getId(), dog.getName(), dog.getBirthDate(), dog.getHeight(), dog.getWeight());
    }

}
