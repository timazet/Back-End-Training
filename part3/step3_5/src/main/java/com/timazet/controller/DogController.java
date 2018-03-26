package com.timazet.controller;

import com.timazet.controller.dto.Dog;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class DogController {

    public static final String API = "/api/v1";
    public static final String DOGS = API + "/dog";
    public static final String DOG = DOGS + "/{id}";

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


    @GetMapping(path = DOGS, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Dog> get() {
        return dogs.values();
    }

    @GetMapping(path = DOG, produces = MediaType.APPLICATION_JSON_VALUE)
    public Dog get(final @PathVariable UUID id) {
        if (!dogs.containsKey(id)) {
            throw new DogNotFoundException(id);
        }
        return dogs.get(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = DOGS, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Dog create(final @RequestBody Dog dog) {
        dog.setId(UUID.randomUUID());
        dogs.put(dog.getId(), dog);
        return dog;
    }

    @PutMapping(path = DOG, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Dog update(final @RequestBody Dog dog, final @PathVariable UUID id) {
        if (!dogs.containsKey(id)) {
            throw new DogNotFoundException(id);
        }
        dog.setId(id);
        dogs.put(dog.getId(), dog);
        return dog;
    }

    @DeleteMapping(path = DOG)
    public void delete(final @PathVariable UUID id) {
        if (!dogs.containsKey(id)) {
            throw new DogNotFoundException(id);
        }
        dogs.remove(id);
    }

}
