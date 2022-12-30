package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	private Long itemId;
	@FutureOrPresent(message = "Дата бронирования должна быть в будущем или в настоящем времени")
	private LocalDateTime start;
	@Future(message = "Дата конца бронирования не может быть в прошлом")
	private LocalDateTime end;
}
