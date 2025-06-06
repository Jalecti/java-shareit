package ru.practicum.shareit.request;


import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {
    @EntityGraph(value = "ItemRequest.forMapping")
    @NonNull
    @Override
    Optional<ItemRequest> findById(@NonNull Long id);

    @EntityGraph(value = "ItemRequest.forMapping")
    Collection<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

    @EntityGraph(value = "ItemRequest.forMapping")
    Collection<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long requestorId);
}
