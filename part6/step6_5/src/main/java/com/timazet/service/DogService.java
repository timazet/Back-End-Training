package com.timazet.service;

import com.timazet.controller.dto.Dog;
import com.timazet.dao.DogDao;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
public class DogService {

    private final DogDao dao;

    @Transactional
    public Collection<Dog> get() {
        return dao.get();
    }

    @Transactional
    public Dog get(UUID id) {
        Assert.notNull(id, "Id should not be null");
        return dao.get(id);
    }

    @Transactional
    public Dog create(Dog dog) {
        Assert.notNull(dog, "Dog should not be null");
        return dao.create(dog);
    }

    @Transactional
    public Dog update(Dog dog) {
        Assert.notNull(dog, "Dog should not be null");
        Assert.notNull(dog.getId(), "Id should not be null");
        return dao.update(dog);
    }

    @Transactional
    public void delete(UUID id) {
        Assert.notNull(id, "Id should not be null");
        dao.delete(id);
    }

}
