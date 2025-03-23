package ru.practicum.shareit.item.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.commet.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
