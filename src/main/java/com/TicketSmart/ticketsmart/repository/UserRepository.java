package com.TicketSmart.ticketsmart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TicketSmart.ticketsmart.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

	Optional<User> findByEmail(String email); //for login
	boolean existsByEmail(String email);	//for register
}
