package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;


public interface CommentStorage extends JpaRepository<Comment, Long> {
    Collection<Comment> findByItemId(Long itemId);

    Collection<Comment> findByItemIdIn(Collection<Long> itemIds);
}
