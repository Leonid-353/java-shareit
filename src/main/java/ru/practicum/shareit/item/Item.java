package ru.practicum.shareit.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.BookingDates;
import ru.practicum.shareit.item.model.commet.Comment;
import ru.practicum.shareit.user.User;

import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 1, max = 255, message = "Максимальная длина названия - 255 символов")
    String name;

    @Column
    @NotBlank(message = "Описание вещи не может быть пустым")
    @Size(min = 1, max = 1000, message = "Максимальная длина описания - 1000 символов")
    String description;

    @Column(name = "is_available")
    boolean available;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    User owner; // владелец вещи

    @Transient
    BookingDates lastBooking;

    @Transient
    BookingDates nextBooking;

    @Column(name = "request_id")
    Long requestId; // ссылка на соответствующий запрос (если вещь была создана по запросу другого пользователя)

    @OneToMany(mappedBy = "item")
    Set<Comment> comments;

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setItem(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
