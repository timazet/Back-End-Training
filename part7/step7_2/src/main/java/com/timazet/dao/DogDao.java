package com.timazet.dao;

import com.timazet.model.Dog;

import java.util.Collection;
import java.util.UUID;

public interface DogDao {

    Collection<Dog> get();

    Dog get(UUID id);

    Dog create(Dog dog);

    Dog update(Dog dog);

    void delete(UUID id);

}
