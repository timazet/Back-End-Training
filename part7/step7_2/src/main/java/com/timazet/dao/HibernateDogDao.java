package com.timazet.dao;

import com.timazet.model.Dog;
import com.timazet.service.DogNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class HibernateDogDao implements DogDao {

    private final SessionFactory sessionFactory;

    @PostConstruct
    private void init() {
        log.info("Using DogDao based on jdbc prepared statement usage");
    }

    @Override
    public Collection<Dog> get() {
        return getSession().createQuery("select d from Dog d", Dog.class).list();
    }

    @Override
    public Dog get(final UUID id) {
        Dog result = getSession().get(Dog.class, id);

        if (result == null) {
            throw new DogNotFoundException(id);
        }
        return result;
    }

    @Override
    public Dog create(final Dog dog) {
        dog.setId(UUID.randomUUID());
        getSession().save(dog);
        return dog;
    }

    @Override
    public Dog update(final Dog dog) {
        Dog fromDB = getSession().get(Dog.class, dog.getId());
        if (fromDB == null) {
            throw new DogNotFoundException(dog.getId());
        }
        getSession().clear();
        getSession().update(dog);
        return dog;
    }

    @Override
    public void delete(final UUID id) {
        Dog dog = getSession().get(Dog.class, id);
        if (dog == null) {
            throw new DogNotFoundException(id);
        }

        getSession().delete(dog);
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

}
