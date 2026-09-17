package com.TicketSmart.ticketsmart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TicketSmart.ticketsmart.dto.UserDTO;
import com.TicketSmart.ticketsmart.entity.User;
import com.TicketSmart.ticketsmart.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public UserDTO createUser(UserDTO dto) {

		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email already in use: " + dto.getEmail());
		}

		User user = new User();
		user.setName(dto.getName());
		user.setEmail(dto.getEmail());
		user.setPassword(dto.getPassword());
		user.setPhoneNumber(dto.getPhoneNumber());
		user.setType(dto.getType());

		User savedUser = userRepository.save(user);
		return toDTO(savedUser);
	}

	public UserDTO getUserById(Long id) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));

		return toDTO(user);

	}

	@Transactional
	public UserDTO updateUser(Long id, UserDTO dto) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));

		user.setName(dto.getName());
		user.setEmail(dto.getEmail());

		// update password if user gives it
		if (dto.getPassword() != null && !dto.getPassword().isBlank())
			user.setPassword(dto.getPassword());

		user.setPhoneNumber(dto.getPhoneNumber());
		user.setType(dto.getType());

		User updatedUser = userRepository.save(user);
		return toDTO(updatedUser);
	}

	@Transactional
	public void deleteUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new RuntimeException("User not found with id: " + id);
		}
		userRepository.deleteById(id);
	}

	private UserDTO toDTO(User user) {
		UserDTO dto = new UserDTO();

		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setPhoneNumber(user.getPhoneNumber());
		dto.setType(user.getType());

		return dto;
	}
}
