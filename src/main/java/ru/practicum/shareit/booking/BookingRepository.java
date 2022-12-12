package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, Timestamp time, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, Timestamp time, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, Timestamp start, Timestamp end, Sort sort);

    List<Booking> findAllByItemId(Long itemId);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, Timestamp time);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, Timestamp end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(
            Long ownerId, Timestamp start, Timestamp end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, Timestamp start, Sort sort);

}
