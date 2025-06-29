package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Map<Long, User> users = new HashMap<>();
    private static final Map<String, Long> existingEmails = new HashMap<>();

    @Override
    public List<UserDto> findAll() {
        return users.values()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        existingEmails.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User update(long userId, User user) {
        users.put(userId, user);
        existingEmails.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }
}

