package com.timazet.service;

import com.timazet.controller.dto.Dog;
import com.timazet.dao.DogDao;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.UUID;

public class DogServiceImpl implements DogService {

    private final DogDao dao;

    public DogServiceImpl(final DogDao dao) {
        this.dao = dao;
    }

    @Override
    public Collection<Dog> get() {
        return dao.get();
    }

    @Override
    public Dog get(UUID id) {
        Assert.notNull(id, "Id should not be null");
        return dao.get(id);
    }

    @Override
    public Dog create(Dog dog) {
        Assert.notNull(dog, "Dog should not be null");
        return dao.create(dog);
    }

    @Override
    public Dog update(Dog dog) {
        Assert.notNull(dog, "Dog should not be null");
        Assert.notNull(dog.getId(), "Id should not be null");
        return dao.update(dog);
    }

    @Override
    public void delete(UUID id) {
        Assert.notNull(id, "Id should not be null");
        dao.delete(id);
    }

}
