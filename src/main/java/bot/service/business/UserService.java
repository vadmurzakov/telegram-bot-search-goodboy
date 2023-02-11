package bot.service.business;

import bot.entity.domain.User;
import bot.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findByUserTelegramId(Long userTelegramId) {
        return Optional.ofNullable(userRepository.findByUserTelegramId(userTelegramId));
    }

    public User getById(UUID id) {
        return userRepository.findUserById(id);
    }

    public User getByTelegramId(Long userTelegramId) {
        return userRepository.findByUserTelegramId(userTelegramId);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
