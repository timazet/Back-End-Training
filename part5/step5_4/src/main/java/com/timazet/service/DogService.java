package com.timazet.service;

import com.timazet.controller.dto.Dog;
import com.timazet.dao.DogDao;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor //we should specify default constructor, because cglib will instantiate class using it
public class DogService {

    private DogDao dao;

    public Collection<Dog> get() {
        return dao.get();
    }

    public Dog get(UUID id) {
        Assert.notNull(id, "Id should not be null");
        return dao.get(id);
    }

    public Dog create(Dog dog) {
        Assert.notNull(dog, "Dog should not be null");
        return dao.create(dog);
    }

    public Dog update(Dog dog) {
        Assert.notNull(dog, "Dog should not be null");
        Assert.notNull(dog.getId(), "Id should not be null");
        return dao.update(dog);
    }

    public void delete(UUID id) {
        Assert.notNull(id, "Id should not be null");
        dao.delete(id);
    }

}
