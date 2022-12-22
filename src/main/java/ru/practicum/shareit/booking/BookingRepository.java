package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBefore(Long bookerId, Timestamp time, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfter(Long bookerId, Timestamp time, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, Timestamp start, Timestamp end, Pageable pageable);

    List<Booking> findAllByItemId(Long itemId);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, Timestamp time);

    Page<Booking> findAllByItemOwnerId(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, Timestamp end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(
            Long ownerId, Timestamp start, Timestamp end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, Timestamp start, Pageable pageable);

}
