package com.timazet.service;

import com.timazet.controller.dto.Dog;

import java.util.Collection;
import java.util.UUID;

public interface DogService {

    Collection<Dog> get();

    Dog get(UUID id);

    Dog create(Dog dog);

    Dog update(Dog dog);

    void delete(UUID id);

}
