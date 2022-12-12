package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto create(UserDto userDto) {
        User user = mapper.fromDto(userDto);
        try {
            return mapper.fromUser(userRepository.save(user));
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Юзер email " + userDto.getEmail() + " уже существует");
        }
    }

    @Override
    public Optional<UserDto> update(Long id, UserDto userDto) {
            User userForUpdate = (userRepository.findById(id)).orElseThrow(
                    () -> new IllegalArgumentException("Юзер с id " + id + " не найден"));
            Optional.ofNullable(userDto.getName()).ifPresent(userForUpdate::setName);
            Optional.ofNullable(userDto.getEmail()).ifPresent(userForUpdate::setEmail);
            return Optional.of(mapper.fromUser(userRepository.save(userForUpdate)));
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Юзер с id " + id + " не найден"));
        return Optional.of(mapper.fromUser(user));
    }

    @Override
    public List<UserDto> getAll() {
        List<User> responseUserList = userRepository.findAll();
        if (responseUserList.isEmpty()) {
            throw new IllegalArgumentException("Ни один юзер не найден");
        }
        return responseUserList.stream()
                .map(mapper::fromUser)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

}
