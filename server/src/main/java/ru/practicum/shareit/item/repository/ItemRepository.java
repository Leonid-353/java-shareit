package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select item from Item item " +
            "where item.available = true " +
            "and (:text is not null and :text <> '' and " +
            "     (lower(item.name) like lower(concat('%', :text, '%')) or " +
            "     lower(item.description) like lower(concat ('%', :text, '%'))))")
    Collection<Item> search(@Param("text") String text);
}
