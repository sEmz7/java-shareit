package ru.practicum.shareit.user;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Transactional
interface UserService {
    @Transactional(readOnly = true)
    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto user);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUser(long userId);

    @Transactional(readOnly = true)
    UserDto getUserById(long userId);
}

