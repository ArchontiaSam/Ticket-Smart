package com.TicketSmart.ticketsmart.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TicketSmart.ticketsmart.entity.Reservation;
import com.TicketSmart.ticketsmart.entity.ReservationStatus;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	List<Reservation> findByUserId(Long userId); //locate in which events user has reservations

	List<Reservation> findByEventId(Long userId); //knowing event id, to find if a user is going to go 

	@Query("SELECT r FROM Reservation r WHERE r.status = : status AND r.CreatedAt < : cutoff")
	List<Reservation> findExpiredPending(@Param("status") ReservationStatus status,   
			@Param("cutoff") LocalDateTime cutoff);		//find reservations which their status is PENDING, to see which of them are expired

}
