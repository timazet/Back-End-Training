package com.timazet.service;

import com.timazet.controller.DogNotFoundException;
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
    public Collection<Dog> get() {
        return dao.get();
    }

    @Override
    public Dog get(UUID id) {
        Assert.notNull(id, "Id should not be null");

        Dog result = dao.get(id);
        if (result == null) {
            throw new DogNotFoundException(id);
        }
        return result;
    }

    @Override
    public Dog create(Dog dog) {
        Assert.notNull(dog, "Dog should not be null");

        dog.setId(UUID.randomUUID());
        dao.create(dog);
        return dog;
    }

    @Override
    public Dog update(Dog dog) {
        Assert.notNull(dog, "Dog should not be null");
        Assert.notNull(dog.getId(), "Id should not be null");

        if (dao.update(dog) <= 0) {
            throw new DogNotFoundException(dog.getId());
        }
        return dog;
    }

    @Override
    public void delete(UUID id) {
        Assert.notNull(id, "Id should not be null");

        if (dao.delete(id) <= 0) {
            throw new DogNotFoundException(id);
        }
    }

}
