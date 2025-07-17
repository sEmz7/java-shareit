package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

interface UserService {
    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto user);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUser(long userId);

    UserDto getUserById(long userId);
}

