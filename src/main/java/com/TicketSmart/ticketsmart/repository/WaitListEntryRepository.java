package com.TicketSmart.ticketsmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TicketSmart.ticketsmart.entity.WaitListEntry;

@Repository
public interface WaitListEntryRepository extends JpaRepository<WaitListEntry, Long> {

	List<WaitListEntry> findByEventId(Long eventId); // from each event to locate users that are waiting and be able to
														// calculate their score

	void deleteByEventIdAndUserId(Long eventId, Long userId); // delete user from waitListEntry when he actually takes a
																// ticket

	void deleteByEventId(Long eventId);
}
