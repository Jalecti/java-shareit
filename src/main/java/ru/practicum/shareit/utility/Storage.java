package ru.practicum.shareit.utility;

import java.util.Collection;
import java.util.Optional;

public interface Storage<T> {

    Collection<T> findAll();

    T save(T entity);

    T update(T newEntity);

    void delete(Long entityId);

    Optional<T> findById(Long entityId);
}
