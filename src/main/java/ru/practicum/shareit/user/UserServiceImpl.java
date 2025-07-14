package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InvalidUserInputException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.matListToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        checkExistingEmail(userDto.getEmail());
        User savedUser = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Нет пользователя с id=" + userId)
        );
        checkExistingEmail(userDto.getEmail());
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Нет пользователя с id=" + userId);
        }
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Нет пользователя с id=" + userId)
        ));
    }

    private void checkExistingEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Пользователь с email={} уже существует.", email);
            throw new InvalidUserInputException("Пользователь с данным email уже существует.");
        }
    }
}
