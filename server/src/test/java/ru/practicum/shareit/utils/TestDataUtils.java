package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.comment.NewCommentRequest;
import ru.practicum.shareit.item.model.commet.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.time.LocalDateTime;
import java.util.HashSet;

public class TestDataUtils {
    public static final String TEXT_SEARCH_ITEM = "DescriptionAvailable";

    // Users
    public static User createUser() {
        return new User(1L, "User", "user@mail.ru");
    }

    public static User createOwner() {
        return new User(2L, "Owner", "owner@mail.ru");
    }

    public static User createBookerAuthor() {
        return new User(3L, "BookerAuthor", "bookerAuthor@mail.ru");
    }

    public static User createRequestor() {
        return new User(4L, "Requestor", "requestor@mail.ru");
    }

    public static User createUserFromNewUserRequest() {
        return new User(5L, "NewUser", "newUser@mail.ru");
    }

    public static NewUserRequest createNewUserRequest() {
        return new NewUserRequest("NewUser", "newUser@mail.ru");
    }

    public static NewUserRequest createNewUserRequestNotUniqueEmail() {
        return new NewUserRequest("NewUser", "user@mail.ru");
    }

    public static UpdateUserRequest createUpdateUserRequest() {
        return new UpdateUserRequest("UpdateUser", "updateUser@mail.ru");
    }

    // Items
    public static Item createAvailableItem() {
        return new Item(1L,
                "AvailableItem",
                "DescriptionAvailableItem",
                true,
                createOwner(),
                null,
                null,
                null,
                new HashSet<>());
    }

    public static Item createNotAvailableItem() {
        return new Item(2L,
                "NotAvailableItem",
                "DescriptionNotAvailableItem",
                false,
                createOwner(),
                null,
                null,
                createItemRequest().getId(),
                new HashSet<>());
    }

    public static Item createAvailableItemFromNewItemRequest() {
        return new Item(3L,
                "Item",
                "DescriptionItem",
                true,
                createOwner(),
                null,
                null,
                null,
                new HashSet<>());
    }

    public static NewItemRequest createNewItemRequest() {
        return new NewItemRequest("Item",
                "DescriptionItem",
                true,
                null);
    }

    public static NewItemRequest createNewItemRequestWithItemRequestId() {
        return new NewItemRequest("Item",
                "DescriptionItem",
                true,
                1L);
    }

    public static UpdateItemRequest createUpdateItemRequest() {
        return new UpdateItemRequest("UpdateItem",
                "UpdateDescriptionItem",
                true);
    }

    // Comments
    public static Comment createComment() {
        return new Comment(1L,
                "TextComment",
                createAvailableItem(),
                createBookerAuthor(),
                LocalDateTime.of(2025, 3, 28, 11, 32, 0));
    }

    public static Comment createCommentFromNewCommentRequest() {
        return new Comment(2L,
                "TextNewComment",
                createAvailableItem(),
                createBookerAuthor(),
                LocalDateTime.of(2025, 3, 30, 11, 32, 0));
    }

    public static NewCommentRequest createNewCommentRequest() {
        return new NewCommentRequest("TextNewComment",
                LocalDateTime.of(2025, 3, 30, 11, 32, 0));
    }

    // Bookings
    public static Booking createBookingAvailableItem() {
        return new Booking(1L,
                createAvailableItem(),
                LocalDateTime.of(3025, 3, 27, 12, 0, 0),
                LocalDateTime.of(3025, 3, 30, 12, 30, 0),
                createBookerAuthor(),
                BookingStatus.WAITING);
    }

    public static Booking createBookingAvailableItemFromNewBookingRequest() {
        return new Booking(5L,
                createAvailableItem(),
                LocalDateTime.of(3026, 3, 27, 12, 0, 0),
                LocalDateTime.of(3026, 3, 30, 12, 30, 0),
                createBookerAuthor(),
                BookingStatus.WAITING);
    }

    public static Booking createBookingAvailableItemStateCurrent() {
        return new Booking(2L,
                createAvailableItem(),
                LocalDateTime.of(2023, 3, 27, 12, 0, 0),
                LocalDateTime.of(3024, 3, 30, 12, 30, 0),
                createBookerAuthor(),
                BookingStatus.APPROVED);
    }

    public static Booking createBookingAvailableItemStatePast() {
        return new Booking(3L,
                createAvailableItem(),
                LocalDateTime.of(2022, 3, 27, 12, 0, 0),
                LocalDateTime.of(2022, 3, 30, 12, 30, 0),
                createBookerAuthor(),
                BookingStatus.APPROVED);
    }

    public static Booking createBookingAvailableItemStateRejected() {
        return new Booking(4L,
                createAvailableItem(),
                LocalDateTime.of(2020, 3, 27, 12, 0, 0),
                LocalDateTime.of(2020, 3, 30, 12, 30, 0),
                createBookerAuthor(),
                BookingStatus.REJECTED);
    }

    public static NewBookingRequest createNewBookingRequestAvailableItem() {
        return new NewBookingRequest(1L,
                LocalDateTime.of(3026, 3, 27, 12, 0, 0),
                LocalDateTime.of(3026, 3, 30, 12, 30, 0),
                BookingStatus.WAITING);
    }

    public static NewBookingRequest createNewBookingRequestNotAvailableItem() {
        return new NewBookingRequest(2L,
                LocalDateTime.of(3020, 3, 27, 12, 0, 0),
                LocalDateTime.of(3020, 3, 30, 12, 30, 0),
                BookingStatus.WAITING);
    }

    public static NewBookingRequest createNewBookingRequestNoItem() {
        return new NewBookingRequest(3L,
                LocalDateTime.of(3020, 3, 27, 12, 0, 0),
                LocalDateTime.of(3020, 3, 30, 12, 30, 0),
                BookingStatus.WAITING);
    }

    public static NewBookingRequest createNewBookingRequestDateIntersection() {
        return new NewBookingRequest(1L,
                LocalDateTime.of(3025, 3, 27, 12, 0, 0),
                LocalDateTime.of(3025, 3, 30, 12, 30, 0),
                BookingStatus.WAITING);
    }

    // ItemRequests
    public static ItemRequest createItemRequest() {
        return new ItemRequest(1L,
                "Description",
                createRequestor(),
                LocalDateTime.of(2025, 3, 27, 11, 32, 0));
    }

    public static ItemRequest createItemRequestNoResponses() {
        return new ItemRequest(2L,
                "Description",
                createOwner(),
                LocalDateTime.of(2025, 3, 30, 12, 30, 0));
    }

    public static ItemRequest createItemRequestFromNewItemRequestDto() {
        return new ItemRequest(3L,
                "ItemRequestDescription",
                createBookerAuthor(),
                LocalDateTime.of(2024, 3, 30, 12, 30, 0));
    }

    public static NewItemRequestDto createNewItemRequestDto() {
        return new NewItemRequestDto("ItemRequestDescription",
                LocalDateTime.of(2024, 3, 30, 12, 30, 0));
    }
}
