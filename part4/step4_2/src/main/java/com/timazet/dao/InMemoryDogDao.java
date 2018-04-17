package com.timazet.dao;

import com.timazet.controller.DogNotFoundException;
import com.timazet.controller.dto.Dog;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDogDao implements DogDao {

    private Map<UUID, Dog> dogs = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Dog pluto = new Dog(UUID.randomUUID(), "Pluto", LocalDate.of(1930, 1, 15),
                65, 25);
        Dog goofy = new Dog(UUID.randomUUID(), "Goofy", LocalDate.of(1932, 5, 13),
                175, 80);
        Dog hound = new Dog(UUID.randomUUID(), "Hound", LocalDate.of(1981, 8, 21),
                40, 20);
        Dog kommissarRex = new Dog(UUID.randomUUID(), "Kommissar Rex", LocalDate.of(1994, 11, 10),
                60, 30);

        dogs.put(pluto.getId(), pluto);
        dogs.put(goofy.getId(), goofy);
        dogs.put(hound.getId(), hound);
        dogs.put(kommissarRex.getId(), kommissarRex);
    }

    public Collection<Dog> get() {
        return dogs.values();
    }

    public Dog get(final UUID id) {
        if (!dogs.containsKey(id)) {
            throw new DogNotFoundException(id);
        }
        return dogs.get(id);
    }

    public Dog create(final Dog dog) {
        dog.setId(UUID.randomUUID());
        dogs.put(dog.getId(), dog);
        return dog;
    }

    public Dog update(final Dog dog) {
        if (!dogs.containsKey(dog.getId())) {
            throw new DogNotFoundException(dog.getId());
        }
        dogs.put(dog.getId(), dog);
        return dog;
    }

    public void delete(final UUID id) {
        if (!dogs.containsKey(id)) {
            throw new DogNotFoundException(id);
        }
        dogs.remove(id);
    }

}
