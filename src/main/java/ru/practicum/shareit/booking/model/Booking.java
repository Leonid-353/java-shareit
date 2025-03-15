package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    Item item; // вещь, которую пользователь бронирует

    @NotNull
    @Column(name = "start_date")
    LocalDateTime start; // дата и время начала бронирования

    @NotNull
    @Column(name = "end_date")
    LocalDateTime end; // дата и время конца бронирования

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booker_id")
    User booker; // пользователь, который осуществляет бронирование

    @Column
    @Enumerated(EnumType.STRING)
    BookingStatus status; // статус бронирования
}
