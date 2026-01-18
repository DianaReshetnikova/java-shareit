package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);


    @Query("SELECT b FROM Booking AS b " +
            " JOIN FETCH b.booker" +
            " JOIN FETCH b.item" +
            " WHERE b.item.id IN (SELECT it.id FROM Item it WHERE it.owner.id = ?1)" +
            " ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAllBookingOfItems(Long userId);

    @Query("SELECT b FROM Booking AS b " +
            " JOIN FETCH b.booker" +
            " JOIN FETCH b.item" +
            " WHERE b.item.id IN (SELECT it.id FROM Item it WHERE it.owner.id = ?1)" +
            " AND b.start < ?2 AND b.end > ?3" +
            " ORDER BY b.start DESC")
    List<Booking> findByOwnerIdCurrentBookingsOfItems(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking AS b " +
            " JOIN FETCH b.booker" +
            " JOIN FETCH b.item" +
            " WHERE b.item.id IN (SELECT it.id FROM Item it WHERE it.owner.id = ?1)" +
            " AND b.end < ?2" +
            " ORDER BY b.start DESC")
    List<Booking> findByOwnerIdPastBookingsOfItems(Long userId, LocalDateTime start);

    @Query("SELECT b FROM Booking AS b " +
            " JOIN FETCH b.booker" +
            " JOIN FETCH b.item" +
            " WHERE b.item.id IN (SELECT it.id FROM Item it WHERE it.owner.id = ?1)" +
            " AND b.start > ?2" +
            " ORDER BY b.start DESC")
    List<Booking> findByOwnerIdFutureBookingsOfItems(Long userId, LocalDateTime start);

    @Query("SELECT b FROM Booking AS b " +
            " JOIN FETCH b.booker" +
            " JOIN FETCH b.item" +
            " WHERE b.item.id IN (SELECT it.id FROM Item it WHERE it.owner.id = ?1)" +
            " AND b.status = ?2" +
            " ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStatusBookingsOfItems(Long userId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1" +
            " AND b.end < ?2 ORDER BY b.end DESC" +
            " LIMIT 1")
    Booking findLastBookingByItemIdAndDateTime(Long itemId, LocalDateTime dateTime);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1" +
            " AND b.start > ?2 ORDER BY b.end DESC" +
            " LIMIT 1")
    Booking findNextBookingByItemIdAndDateTime(Long itemId, LocalDateTime dateTime);

    boolean existsByBookerIdAndItemIdAndEndBefore(Long userId, Long itemId, LocalDateTime end);
}
