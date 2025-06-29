package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<UserDto> findAll();

    User save(User user);

    Optional<User> findByEmail(String email);

    User update(long userId, User user);

    Optional<User> findById(long userId);

    void delete(long userId);
}
