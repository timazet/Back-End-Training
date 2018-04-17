package com.timazet.dao;

import com.timazet.controller.dto.Dog;

import java.util.Collection;
import java.util.UUID;

public interface DogDao {

    Collection<Dog> get();

    Dog get(final UUID id);

    Dog create(final Dog dog);

    Dog update(final Dog dog);

    void delete(final UUID id);

}
