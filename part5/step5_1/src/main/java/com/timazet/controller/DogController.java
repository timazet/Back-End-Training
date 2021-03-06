package com.timazet.controller;

import com.timazet.controller.dto.Dog;
import com.timazet.service.DogService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class DogController {

    public static final String API = "/api/v1";
    public static final String DOGS = API + "/dog";
    public static final String DOG = DOGS + "/{id}";

    private DogService service;

    @GetMapping(path = DOGS, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Dog> get() {
        return service.get();
    }

    @GetMapping(path = DOG, produces = MediaType.APPLICATION_JSON_VALUE)
    public Dog get(final @PathVariable UUID id) {
        return service.get(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = DOGS, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Dog create(final @Valid @RequestBody Dog dog) {
        return service.create(dog);
    }

    @PutMapping(path = DOG, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Dog update(final @Valid @RequestBody Dog dog, final @PathVariable UUID id) {
        dog.setId(id);
        return service.update(dog);
    }

    @DeleteMapping(path = DOG)
    public void delete(final @PathVariable UUID id) {
        service.delete(id);
    }

}
