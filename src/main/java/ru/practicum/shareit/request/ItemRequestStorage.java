package ru.practicum.shareit.request;


import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {

}
