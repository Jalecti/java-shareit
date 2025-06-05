package ru.practicum.shareit.item.comment;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;


public interface CommentStorage extends JpaRepository<Comment, Long> {
    @EntityGraph(value = "Comment.forMapping")
    @NonNull
    @Override
    Optional<Comment> findById(@NonNull Long id);

    @EntityGraph(value = "Comment.forMapping")
    Collection<Comment> findByItemId(Long itemId);

    @EntityGraph(value = "Comment.forMapping")
    Collection<Comment> findByItemIdIn(Collection<Long> itemIds);
}
