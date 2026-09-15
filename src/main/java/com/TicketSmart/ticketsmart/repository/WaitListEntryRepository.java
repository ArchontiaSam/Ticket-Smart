package com.TicketSmart.ticketsmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TicketSmart.ticketsmart.entity.WaitListEntry;

@Repository
public interface WaitListEntryRepository extends JpaRepository<WaitListEntry, Long> {

	List<WaitListEntry> findByEventId(Long eventId);

	void deleteByIdAndUserId(Long eventId, Long userId);

}
