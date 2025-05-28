package ru.practicum.shareit.item;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Long> {

    @EntityGraph(value = "Item.forMapping")
    @NonNull
    @Override
    Optional<Item> findById(@NonNull Long id);

    @EntityGraph(value = "Item.forMapping")
    Collection<Item> findAllByOwnerId(Long ownerId);

    @EntityGraph(value = "Item.forMapping")
    @Query("select i " +
            "from Item as i " +
            "where " +
            "i.available = true " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')))")
    Collection<Item> findAllByText(String text);
}