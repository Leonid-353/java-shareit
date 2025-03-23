package ru.practicum.shareit.item.mapper.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.NewCommentRequest;
import ru.practicum.shareit.item.model.commet.Comment;
import ru.practicum.shareit.user.User;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment mapToComment(NewCommentRequest request, User author) {
        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setAuthor(author);
        comment.setCreated(request.getCreated());

        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());
        dto.setAuthorName(comment.getAuthor().getName());

        return dto;
    }

    public static Set<CommentDto> mapToCommentsDto(Set<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toSet());
    }
}
