package com.timazet.service;

import java.util.UUID;

public class DogNotFoundException extends RuntimeException {

    private final UUID id;

    public DogNotFoundException(final UUID id) {
        super(String.format("Dog with id = '%s' isn't found", id));
        this.id = id;
    }

}
