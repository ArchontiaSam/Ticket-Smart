package com.TicketSmart.ticketsmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TicketSmart.ticketsmart.entity.Friendship;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

	@Query("SELECT f FROM Friendship f WHERE f.userA.id = :userId OR f.userB.id = :userId")
	List<Friendship> findAllByUserId(@Param("userId") Long userId); //locate user friends

	//List<Friendship> findAll();
}
