package com.timazet.service;

import com.timazet.aspect.MyTransactional;
import com.timazet.controller.dto.Dog;
import com.timazet.dao.DogDao;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
public class DogServiceImpl implements DogService {

    private final DogDao dao;

    @Override
    @MyTransactional
    public Collection<Dog> get() {
        return dao.get();
    }

    @Override
    @MyTransactional
    public Dog get(UUID id) {
        Assert.notNull(id, "Id should not be null");
        return dao.get(id);
    }

    @Override
    @MyTransactional
    public Dog create(Dog dog) {
        Assert.notNull(dog, "Dog should not be null");
        return dao.create(dog);
    }

    @Override
    @MyTransactional
    public Dog update(Dog dog) {
        Assert.notNull(dog, "Dog should not be null");
        Assert.notNull(dog.getId(), "Id should not be null");
        return dao.update(dog);
    }

    @Override
    @MyTransactional
    public void delete(UUID id) {
        Assert.notNull(id, "Id should not be null");
        dao.delete(id);
    }

}
